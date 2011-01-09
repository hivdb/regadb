package net.sf.regadb.contamination;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import net.sf.regadb.contamination.SequenceDistancesQuery.OutputType;
import net.sf.regadb.contamination.SequenceDistancesQuery.Range;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Transaction;
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

public class SampleDistances {
	public static void main(String [] args) throws WrongUidException, WrongPasswordException, DisabledUserException, IOException {
		if (args.length != 6) {
			System.err.println("user password outputfile.csv outputType ORF region");
			return;
		}
		
		RegaDBSettings.createInstance();
		
		SequenceDb db = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());
		
		Login login = Login.authenticate(args[0], args[1]);
		Transaction t = login.createTransaction();
		Query q = t.createQuery("from NtSequence");
		q.setCacheMode(CacheMode.IGNORE);
		ScrollableResults r = q.scroll();
		
		FileWriter fw = new FileWriter(new File(args[2]));
		
		String type = args[3].trim();
		OutputType outputType = null;
		if (type.equals("I"))
			outputType = OutputType.IntraPatient;
		else if (type.equals("O"))
			outputType = OutputType.ExtraPatient;
		
		Range range = new Range();
		range.orf = args[4];
		
		String region[] = args[5].split("-");
		range.start = Integer.parseInt(region[0]);
		range.end = Integer.parseInt(region[1]);
		
		int i = 0;
		long start = System.currentTimeMillis();
		while (r.next()) {
			NtSequence seq = (NtSequence)r.get(0);
			
			SequenceDistancesQuery distances = new SequenceDistancesQuery(seq, outputType, range);
			db.query(seq.getViralIsolate().getGenome(), distances);
			
			for (Map.Entry<Integer, SequenceDistance> e : distances.getSequenceDistances().entrySet()) {
				if (e.getKey().equals(seq.getNtSequenceIi())) {
					continue;
				}
				
				SequenceDistance f = e.getValue();
				
				double diff = ((double)f.numberOfDifferences/f.numberOfPositions);
				if (f.numberOfPositions != 0){
					fw.write(type + "," + diff + "\n");
					fw.flush();
				}
			}
			
			if (i % 100 == 0) {
				System.err.println("time:" + ((System.currentTimeMillis() - start) / 1000));
				start = System.currentTimeMillis();
				t.clearCache();
			}
			
			i++;
			System.err.println("Processed " + i);
		}
		
		fw.close();
	}
}
