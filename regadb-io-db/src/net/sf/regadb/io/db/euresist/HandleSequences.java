package net.sf.regadb.io.db.euresist;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;

public class HandleSequences {
	private ExportDB exportDb_;
	public HandleSequences(ExportDB exportDb) {
		exportDb_ = exportDb;
	}
	
	public void run(Map<String,Patient> patients) {
        try {
			ResultSet rs = exportDb_.getDb().executeQuery("SELECT * FROM RawSequences");
			
			while(rs.next()) {
				int patientId = rs.getInt("patientID");
				int sampleId = rs.getInt("originalID");
				Date sequenceDate = rs.getDate("sequence_date");
				String sequence = rs.getString("raw_sequence");
				
				Patient p = patients.get(patientId+"");
				if(p!=null) {
	                ViralIsolate vi = p.createViralIsolate();
	                vi.setSampleDate(sequenceDate);
	                vi.setSampleId(sampleId+"");
	                
	                NtSequence ntseq = new NtSequence();
	                ntseq.setLabel("Sequence 1");
	                ntseq.setSequenceDate(sequenceDate);
	                ntseq.setNucleotides(Utils.clearNucleotides(sequence));
	                
	                vi.getNtSequences().add(ntseq);
				} else {
                    ConsoleLogger.getInstance().logWarning(
                            "No patient with id " + patientId + " for viral isolate with id " + sampleId);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String [] args) {
		HandleSequences hs = new HandleSequences(new ExportDB("EuResist","root", "Eatnomeat001"));
		hs.run(null);
	}
}
