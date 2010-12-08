package net.sf.regadb.contamination;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
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
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.apache.commons.io.FileUtils;
import org.xml.sax.InputSource;

public class TestContaminationDetection {
	public static void main(String [] args) throws IOException, WrongUidException, WrongPasswordException, DisabledUserException {
		File inputFile = new File(args[0]);
		
		String user = args[1];
		String password = args[2];

		RegaDBSettings.createInstance();

		Login login = Login.authenticate(user, password);
	
		Transaction t = login.createTransaction();
		
		List<Patient> patients = getPatients(inputFile, t);
		
		File sequenceDbDir = new File("/tmp/test-contam/");
		SequenceDb seqDb = SequenceDb.getInstance(sequenceDbDir.getAbsolutePath());
		seqDb.init(t);
		
		for (Patient p : patients) {
			for (ViralIsolate vi : p.getViralIsolates()) {
				for (NtSequence ntSeq : vi.getNtSequences()) {
					double cf = ContaminationDetection.clusterFactor(ntSeq, seqDb);
					System.err.println("\"" + ntSeq.getNtSequenceIi() + "\",\"" + cf + "\",\"" + "true" + "\"");
				}
			}
		}
		
		FileUtils.deleteDirectory(sequenceDbDir);
	}
	
	private static void shuffleIsolates() {
		
	}
	
	private static int sequence_ii = 0;
	private static List<Patient> getPatients(File inputFile, Transaction t) {
		final List<Patient> patients = new ArrayList<Patient>();
        ImportFromXML imp = new ImportFromXML();
        imp.loadDatabaseObjects(t);
        FileReader r;
        try {
            r = new FileReader(inputFile);
            imp.readPatients(new InputSource(r), new ImportHandler<Patient>(){
				@Override
				public void importObject(Patient p) {
					System.err.println(patients.size());
					for (ViralIsolate vi : p.getViralIsolates())
						for (NtSequence ntSeq : vi.getNtSequences()) {
							ntSeq.setNtSequenceIi(sequence_ii);
							sequence_ii++;
						}
					patients.add(p);
				}
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return patients;
	}
}
