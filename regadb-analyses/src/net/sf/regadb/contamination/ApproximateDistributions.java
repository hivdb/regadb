package net.sf.regadb.contamination;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.sequencedb.SequenceUtils.SequenceDistance;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;

public class ApproximateDistributions {
	public static void main(String [] args) throws WrongUidException, WrongPasswordException, DisabledUserException {
		RegaDBSettings.createInstance();
		
		SequenceDb db = new SequenceDb(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());
		
		Login login = Login.authenticate(args[0], args[1]);
		Transaction t = login.createTransaction();
		Query q = t.createQuery("from NtSequence");
		q.setCacheMode(CacheMode.IGNORE);
		ScrollableResults r = q.scroll();
		int i = 0;
		while (r.next()) {
			NtSequence seq = (NtSequence)r.get(0);
			
			Patient p = new Patient(seq.getViralIsolate().getPatient(), Privileges.READONLY.getValue());
			Set<Integer> intraPatientSeqs = new HashSet<Integer>();
			for (ViralIsolate vi : p.getViralIsolates()) 
				for (NtSequence ntseq : vi.getNtSequences()) 
					intraPatientSeqs.add(ntseq.getNtSequenceIi());
			
			SequenceDistancesQuery distances = new SequenceDistancesQuery(seq);
			db.query(seq.getViralIsolate().getGenome(), distances);
			
			for (Map.Entry<Integer, SequenceDistance> e : distances.getSequenceDistances().entrySet()) {
				String type = "O";
				if (intraPatientSeqs.contains(e.getKey()))
					type = "I";
				SequenceDistance f = e.getValue();
				
				double diff = ((double)f.numberOfDifferences/f.numberOfPositions);
				if (f.numberOfPositions != 0)
					System.out.println(type + ";" + diff + "(" + f.numberOfDifferences + "/" + f.numberOfPositions + ")");
			}
			
			i++;
			if (i > 100) { 
				t.clearCache();
				i = 0;
			}
		}
	}
}
