/*
 * Created on May 11, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.io.importXML.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.DatasetAccessId;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
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

    public Login login;

    public ImportFromXML instance;

    private PrintStream out = System.err;
    
    public ImportXML(Login l) throws WrongUidException, WrongPasswordException, DisabledUserException {
    	instance = new ImportFromXML();
    	login = l;    	
    }
    public ImportXML(String user, String password) throws WrongUidException, WrongPasswordException, DisabledUserException {
    	this(Login.authenticate(user, password));
    }
    
    /**
     * 
     */
    private Dataset loadOrCreateDataset(Transaction t, String name) {
        Dataset dataset = t.getDataset(name);

        if (dataset == null) {
            dataset = new Dataset(t.getSettingsUser(), name, new Date());
            dataset.setRevision(1);

            /*
             * Should not be possible like this ?
             */
            t.getSettingsUser().getDatasetAccesses()
                    .add(
                            new DatasetAccess(new DatasetAccessId(t
                                    .getSettingsUser(), dataset),
                                    Privileges.READWRITE.getValue(), t.getSettingsUser().getUid()));
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
                out.println(instance.getLog());
                instance.getLog().delete(0, instance.getLog().length());
            } catch (Exception e) {
                out.println("sync error:");
                out.println(instance.getLog());
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
    
    public void importPatients(InputSource s, String datasetName) throws SAXException, IOException {
        Transaction t = login.createTransaction();
        instance.loadDatabaseObjects(t);

        Dataset dataset = loadOrCreateDataset(t, datasetName);

        PatientImportHandler importHandler = new PatientImportHandler(t, dataset);
        instance.readPatients(s, importHandler);
        importHandler.t.commit();

        out.println(instance.getLog());
        out.println("Read: " + importHandler.patientsRead + " patients");
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
                out.println("Processing: '" + vi.getSampleId() + "'");
                ViralIsolate dbvi = t.getViralIsolate(dataset, vi.getSampleId());
                if (dbvi == null)
                    throw new RuntimeException("Viral Isolate '" + vi.getSampleId() + "' not found in database!");
                instance.syncPair(t, vi, dbvi, SyncMode.Update, false);
                out.println(instance.getLog());
                instance.getLog().delete(0, instance.getLog().length());
            } catch (Exception e) {
                out.println("sync error:");
                out.println(instance.getLog());
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
    
    public void importViralIsolates(InputSource s, String datasetName) throws SAXException, IOException {
        Transaction t = login.createTransaction();
        instance.loadDatabaseObjects(t);

        Dataset dataset = loadOrCreateDataset(t, datasetName);

        ViralIsolateImportHandler importHandler = new ViralIsolateImportHandler(t, dataset);
        instance.readViralIsolates(s, importHandler);
        importHandler.t.commit();

        out.println(instance.getLog());
        out.println("Read: " + importHandler.isolatesRead + " isolates");
    }
    
    public void importSubtypeTests(InputSource is, String datasetName) throws SAXException, IOException {
        Transaction t = login.createTransaction();
        instance.loadDatabaseObjects(t);

        Dataset dataset = loadOrCreateDataset(t, datasetName);
        
        t.commit();
        
        instance.readViralIsolates(is, new ImportHandler<ViralIsolate>() {
            public void importObject(ViralIsolate object) {
                Transaction innerT = login.createTransaction();
                Test subTypeTest = innerT.getTest("Rega HIV-1 Subtype Tool");
                List<ViralIsolate> dbVis = innerT.getViralIsolate(object.getSampleId());
                if(dbVis.size()>1) {
                    System.err.println("duplicate sample id; quitting");
                    System.exit(0);
                } else {
                    ViralIsolate dbVi = dbVis.get(0);
                    for(NtSequence ntseq : object.getNtSequences()) {
                        for(NtSequence dbNtseq : dbVi.getNtSequences()) {
                            if(ntseq.getLabel().equals(dbNtseq.getLabel())) {
                             for(TestResult tr : ntseq.getTestResults()) {
                                 dbNtseq.getTestResults().add(tr);
                                 tr.setNtSequence(dbNtseq);
                                 tr.setTest(subTypeTest);
                                 System.err.println("import subtype: "  + tr.getValue());
                                 break;
                             }
                            }
                        }
                    }
                    innerT.save(dbVi);
                    innerT.commit();
                }
            }
        });

        out.println(instance.getLog());
        out.println("Ready importing subtypes");
    }
    
    public void setPrintStream(PrintStream ps) {
    	out = ps;
    }
}
