package net.sf.regadb.io.db.portugal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.analysis.functions.FastaReadStatus;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;

public class FixSpreadSequences {
	Map<String, ViralIsolate> vis = new HashMap<String, ViralIsolate>();
	
	private String sequencePath;
	private Login login;	
	
	public FixSpreadSequences(String sequencePath) {
		this.sequencePath = sequencePath;
	}
	
	public static void main(String [] args) throws FileNotFoundException, UnsupportedEncodingException, ParseException {
		FixSpreadSequences fss = new FixSpreadSequences(args[3]);
		fss.run(args[0], args[1], args[2]);
		IOUtils.exportNTXML(fss.vis.values(), args[4], ConsoleLogger.getInstance());
	}

	public void run(String userName, String password, String csvFile) throws FileNotFoundException, UnsupportedEncodingException, ParseException {
		try {
			login = Login.authenticate(userName, password);
			Transaction t = login.createTransaction();
			List<Patient> ps = t.getPatients();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			
			Table table = Table.readTable(csvFile);
			for(int i = 1; i<table.numRows(); i++) {
				String patientId = table.valueAt(0, i);
				String sampleId = table.valueAt(1, i);
				Date date = sdf.parse(table.valueAt(3, i)+"/"+table.valueAt(2, i)+"/1");
				boolean found = findPatient(ps, patientId, sampleId, date);
				if(!found)
					System.err.println("cannot find patient: " + patientId);
			}
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
	}
	
	public boolean findPatient(List<Patient> ps, String patientId, String sampleId, Date d) {
		for(Patient p : ps) {
			for(PatientAttributeValue pav : p.getPatientAttributeValues()) {
				if(pav.getAttribute().getName().equals("old_id") && pav.getValue().equals(patientId)) {
					addViralIsolate(p, sampleId, patientId, d);
					return true;
				}
			}
			if(p.getPatientId().equals(patientId)) {
				addViralIsolate(p, sampleId, patientId, d);
				return true;
			}
		}
		
		return false;
	} 
	
	public void addViralIsolate(Patient p, String sampleId, String patientId, Date d) {
		File sequenceDir = new File(this.sequencePath);
		File f = findFasta(sequenceDir, sampleId);
		if(f==null) {
			System.err.println("Cannot find fasta for sample - patient: " + sampleId + " - " + patientId);
		} else {
	          FastaRead fr = FastaHelper.readFastaFile(f, true);

	          if(fr.status_ == FastaReadStatus.Valid) {
	        	  for(ViralIsolate otherVis : p.getViralIsolates()) {
	        		  if(otherVis.getSampleId().equals(sampleId)) {
	        			  NtSequence ntseq = (NtSequence)otherVis.getNtSequences().toArray()[0];
	        			  boolean seqsCheck = ntseq.getNucleotides().equals(Utils.clearNucleotides(fr.xna_));
	        			  if(seqsCheck)
	        				  return;
	        			  else {
	        				  System.err.println("Already in the database: " + sampleId + "---" + seqsCheck);
	        				  return;
	        			  }
	        		  }
	        	  }
	        	  Transaction t = login.createTransaction();
	              ViralIsolate vi = p.createViralIsolate();
	              vi.setSampleDate(d);
	              vi.setSampleId(sampleId);
	
	              vis.put(sampleId, vi);
	              
	              NtSequence nts = new NtSequence(vi);
	              vi.getNtSequences().add(nts);
	              nts.setNucleotides(Utils.clearNucleotides(fr.xna_));
	              nts.setLabel("Sequence 1");
	              
	              t.save(p);
	              t.commit();
	          } else {
	        	  System.err.println("invalid fasta " + f.getAbsolutePath());
	          }
		}
	}
	
	public File findFasta(File path, String sampleId) {
		for(File f : path.listFiles()) {
			if(f.isDirectory()) {
				File ff = findFasta(f,sampleId);
				if(ff!=null)
					return ff;
			} else if(f.getName().equals(sampleId+".fasta") || f.getName().equals(sampleId+"s.fasta") || f.getName().equals(sampleId+"S.fasta")) {
				return f;
			}
		}
		return null;
	}
}