/*
 * Created on May 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.importXML;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.DatasetAccessId;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.ImportFromXMLBase.SyncMode;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class TestImportXML implements ImportHandler<Patient> {

    Login login;
    int patients;
    ImportFromXML instance;
    private Dataset dataset;
    private Transaction t;

    public TestImportXML() throws WrongUidException, WrongPasswordException, DisabledUserException {
        instance = new ImportFromXML();
        patients = 0;

        login = Login.authenticate("test", "test");
        
        t = login.createTransaction();
        instance.loadDatabaseObjects(t);                

        t.commit();
        
        t = login.createTransaction();
        loadDataset();
    }

    /**
     * 
     */
    private void loadDataset() {
        dataset = t.getDataset("PT");
        if (dataset == null) {
            dataset = new Dataset(t.getSettingsUser(), "PT", new Date());
            t.getSettingsUser().getDatasetAccesses().add(new DatasetAccess(new DatasetAccessId(t.getSettingsUser(), dataset), Privileges.READWRITE.getValue(), "nobody"));
            t.save(dataset);
        }
    }
    
    /**
     * @param args
     * @throws SAXException 
     * @throws IOException 
     * @throws DisabledUserException 
     * @throws WrongPasswordException 
     * @throws WrongUidException 
     */
    public static void main(String[] args) throws SAXException, IOException, WrongUidException, WrongPasswordException, DisabledUserException {
        TestImportXML self = new TestImportXML();
    
        FileReader r = new FileReader(new File(args[0]));

        self.instance.readPatients(new InputSource(r), self);
 
        System.err.println(self.instance.log);
        System.err.println("Read: " + self.patients + " patients");

        self.t.commit();
    }

    public void importObject(Patient patient) {
        try {
            patient.setSourceDataset(dataset, t);
            instance.sync(t, patient, SyncMode.Update, false);
            System.err.println(instance.getLog());
            instance.getLog().delete(0, instance.getLog().length());            
        } catch (Exception e) {
            System.err.println("sync error:");
            System.err.println(instance.getLog());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        ++patients;

        if (patients % 50 == 0) {
            t.commit();
            t.clearCache();
            instance.loadDatabaseObjects(t);                
            t = login.createTransaction();
            loadDataset();
        }
    }
}
