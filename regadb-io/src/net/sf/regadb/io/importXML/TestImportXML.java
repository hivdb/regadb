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
import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.ImportFromXML.SyncMode;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class TestImportXML implements ImportHandler<Patient> {

    Login login;
    int patients;
    ImportFromXML instance;
    private Dataset dataset;

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
        
        Transaction t = login.createTransaction();
        instance.loadDatabaseObjects(t);                

        dataset = new Dataset(t.getSettingsUser(), "PT", new Date());
        t.save(dataset);

        t.commit();
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
        System.err.println(self.instance.log);
        System.err.println("Read: " + self.patients + " patients");
    }

    public void importObject(Patient patient) {
        Transaction t = login.createTransaction();
        try {
            patient.setSourceDataset(dataset, t);
            instance.sync(t, patient, SyncMode.Update, false);
        } catch (ImportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        t.commit();
        ++patients;
    }

}
