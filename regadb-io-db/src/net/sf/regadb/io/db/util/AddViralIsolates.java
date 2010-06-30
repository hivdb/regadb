package net.sf.regadb.io.db.util;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.util.pair.Pair;

public class AddViralIsolates {
	@SuppressWarnings("serial")
	public static class AddViralIsolateException extends Exception{
		public AddViralIsolateException(String msg){
			super(msg);
		}
	}
	@SuppressWarnings("serial")
	public static class PatientNotFoundException extends AddViralIsolateException{
		public PatientNotFoundException(String patientId){
			super("patient does not exist: '"+ patientId +"'");
		}
	}
	@SuppressWarnings("serial")
	public static class DuplicateSampleIdException extends AddViralIsolateException{
		public DuplicateSampleIdException(String sampleId){
			super("duplicate sample id: '"+ sampleId +"'");
		}
	}
	@SuppressWarnings("serial")
	public static class SampleIdNotFoundException extends AddViralIsolateException{
		public SampleIdNotFoundException(String sampleId){
			super("sample id does not exist: '"+ sampleId +"'");
		}
	}

	private String datasetDescription;
	private Login login;
	private Transaction t;
	
	private List<ViralIsolate> viralIsolates = new LinkedList<ViralIsolate>();
	
	public AddViralIsolates(String user, String pass, String datasetDescription) throws WrongUidException, WrongPasswordException, DisabledUserException{
		this.login = Login.authenticate(user, pass);
		this.datasetDescription = datasetDescription;
	}
	
	public void handleViralIsolate(String patientId, String sampleId, Date sampleDate, Collection<Pair<String,String>> sequences) throws AddViralIsolateException{
		t = login.createTransaction();
		boolean dupe = isDuplicate(sampleId);
		t.commit();
		
		if(dupe)
			throw new DuplicateSampleIdException(sampleId);

		Patient p = getPatient(patientId);
		
		t = login.createTransaction();
		
		ViralIsolate vi = createViralIsolate(sampleId, sampleDate, sequences);
		p.addViralIsolate(vi);
		viralIsolates.add(vi);
		
		t.commit();
	}
	
	public void handleSequence(String sampleId, String label, String nucleotides) throws SampleIdNotFoundException{
		t = login.createTransaction();
		List<ViralIsolate> vis = t.getViralIsolate(sampleId);
		
		if(vis.size() == 0){
			t.commit();
			throw new SampleIdNotFoundException(sampleId);
		}
		
		ViralIsolate vi = vis.get(0);
		NtSequence nt = new NtSequence();
		nt.setLabel(label);
		nt.setNucleotides(nucleotides);
		nt.setViralIsolate(vi);
		vi.getNtSequences().add(nt);
		
		t.commit();
		
		viralIsolates.add(vi);
	}
	
	protected boolean isDuplicate(String sampleId){
		return t.isUsedSampleId(sampleId);
	}
	
	protected Patient getPatient(String patientId) throws PatientNotFoundException{
		t = login.createTransaction();
		Patient p = t.getPatient(t.getDataset(datasetDescription), patientId);
		t.commit();

		if(p == null)
			throw new PatientNotFoundException(patientId);
		
		return p;
	}
	
	protected ViralIsolate createViralIsolate(String sampleId, Date sampleDate, Collection<Pair<String,String>> sequences){
    	ViralIsolate vi = new ViralIsolate();
    	vi.setSampleId(sampleId);
    	vi.setSampleDate(sampleDate);

    	for(Pair<String,String> sequence : sequences){
	    	NtSequence nt = new NtSequence();
	    	nt.setLabel(sequence.getKey());
	    	nt.setSequenceDate(sampleDate);
	    	nt.setNucleotides(sequence.getValue());
	    	
	    	nt.setViralIsolate(vi);
	    	vi.getNtSequences().add(nt);
    	}

    	return vi;
    }
	
	public void exportViralIsolates(File xmlfile){
		IOUtils.exportNTXML(viralIsolates, xmlfile.getAbsolutePath(), false, ConsoleLogger.getInstance());
	}
}
