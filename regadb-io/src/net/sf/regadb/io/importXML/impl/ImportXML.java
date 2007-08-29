/*
 * Created on May 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.importXML.impl;

import java.io.IOException;
import java.util.Date;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.DatasetAccessId;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.importXML.ImportHandler;
import net.sf.regadb.io.importXML.ImportFromXMLBase.SyncMode;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ImportXML {

    Login login;

    ImportFromXML instance;

    public ImportXML(String user, String password) throws WrongUidException, WrongPasswordException,
            DisabledUserException {
        instance = new ImportFromXML();

        login = Login.authenticate(user, password);
    }

    /**
     * 
     */
    private Dataset loadOrCreateDataset(Transaction t, String name) {
        Dataset dataset = t.getDataset(name);

        if (dataset == null) {
            dataset = new Dataset(t.getSettingsUser(), name, new Date());

            /*
             * Should not be possible like this ?
             */
            t.getSettingsUser().getDatasetAccesses()
                    .add(
                            new DatasetAccess(new DatasetAccessId(t
                                    .getSettingsUser(), dataset),
                                    Privileges.READWRITE.getValue(), "nobody"));
            t.save(dataset);
        }

        return dataset;
    }

    private class PatientImportHandler implements ImportHandler<Patient>
    {
        private Transaction t;
        private Dataset dataset;
        private int patientsRead;

        public PatientImportHandler(Transaction t, Dataset dataset)
        {
            this.t = t;
            this.dataset = dataset;
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

            ++patientsRead;

            if (patientsRead % 50 == 0) {
                t.commit();
                t.clearCache();
                t = login.createTransaction();
                instance.loadDatabaseObjects(t);
                dataset = loadOrCreateDataset(t, dataset.getDescription());
            }
        }        
    }
    
    void importPatients(InputSource s, String datasetName) throws SAXException, IOException {
        Transaction t = login.createTransaction();
        instance.loadDatabaseObjects(t);

        Dataset dataset = loadOrCreateDataset(t, datasetName);

        PatientImportHandler importHandler = new PatientImportHandler(t, dataset);
        instance.readPatients(s, importHandler);
        importHandler.t.commit();

        System.err.println(instance.getLog());
        System.err.println("Read: " + importHandler.patientsRead + " patients");
    }

    private class ViralIsolateImportHandler implements ImportHandler<ViralIsolate>
    {
        private Transaction t;
        private Dataset dataset;
        private int isolatesRead;

        public ViralIsolateImportHandler(Transaction t, Dataset dataset)
        {
            this.t = t;
            this.dataset = dataset;
        }

        public void importObject(ViralIsolate vi) {

            try {
                System.err.println("Processing: '" + vi.getSampleId() + "'");
                ViralIsolate dbvi = t.getViralIsolate(dataset, vi.getSampleId());
                if (dbvi == null)
                    throw new RuntimeException("Viral Isolate '" + vi.getSampleId() + "' not found in database!");
                instance.syncPair(t, vi, dbvi, SyncMode.Update, false);
                System.err.println(instance.getLog());
                instance.getLog().delete(0, instance.getLog().length());
            } catch (Exception e) {
                System.err.println("sync error:");
                System.err.println(instance.getLog());
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            ++isolatesRead;

            if (isolatesRead % 5 == 0) {
                t.commit();
                t.clearCache();
                t = login.createTransaction();
                instance.loadDatabaseObjects(t);
                dataset = loadOrCreateDataset(t, dataset.getDescription());
            }
        }        
    }
    
    void importViralIsolates(InputSource s, String datasetName) throws SAXException, IOException {
        Transaction t = login.createTransaction();
        instance.loadDatabaseObjects(t);

        Dataset dataset = loadOrCreateDataset(t, datasetName);

        ViralIsolateImportHandler importHandler = new ViralIsolateImportHandler(t, dataset);
        instance.readViralIsolates(s, importHandler);
        importHandler.t.commit();

        System.err.println(instance.getLog());
        System.err.println("Read: " + importHandler.isolatesRead + " isolates");
    }
}
