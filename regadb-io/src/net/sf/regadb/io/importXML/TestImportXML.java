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
import java.util.Date;

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

    public TestImportXML() {
        instance = new ImportFromXML();
        patients = 0;

        login = null;
        try
        {
            login = Login.authenticate("test", "test");
        }
        catch (WrongUidException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (WrongPasswordException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (DisabledUserException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
        
        t = login.createTransaction();
        instance.loadDatabaseObjects(t);                

        t.commit();
        
        t = login.createTransaction();
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
     */
    public static void main(String[] args) throws SAXException, IOException {
        TestImportXML self = new TestImportXML();
    
        FileReader r = new FileReader(new File(args[0]));

        self.instance.readPatients(new InputSource(r), self);
        //self.instance.readTests(new InputSource(r), null);
        System.err.println(self.instance.log);
        System.err.println("Read: " + self.patients + " patients");

        self.t.commit();
    }

    public void importObject(Patient patient) {
        try {
            System.err.println("syncing:");
            patient.setSourceDataset(dataset, t);
            instance.sync(t, patient, SyncMode.Update, false);
            System.err.println(instance.getLog());
            instance.getLog().delete(0, instance.getLog().length());
         } catch (Exception e) {
            System.err.println("sync error:");
            System.err.println(instance.getLog());
            e.printStackTrace();
        }
        ++patients;
    }
}
