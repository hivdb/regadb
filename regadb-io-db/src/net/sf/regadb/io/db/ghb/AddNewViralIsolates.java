package net.sf.regadb.io.db.ghb;

import java.io.File;
import java.util.Collection;
import java.util.Date;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

public class AddNewViralIsolates extends GetViralIsolates{
	private String datasetDescription;
	private Login login;
	private Transaction t;
	
	public AddNewViralIsolates(String user, String pass, String datasetDescription) throws WrongUidException, WrongPasswordException, DisabledUserException{
		this.login = Login.authenticate(user, pass);
		this.datasetDescription = datasetDescription;
	}
	
	@Override
	public void handleIsolate(String sampleId, String nucleotides){
		t = login.createTransaction();
		super.handleIsolate(sampleId, nucleotides);
		t.commit();
	}
	
	@Override
	protected boolean isDuplicate(String sampleId){
		return super.isDuplicate(sampleId)
			|| t.isUsedSampleId(sampleId);
	}
	
	@Override
	protected Patient getPatient(String ead){
		return t.getPatient(t.getDataset(datasetDescription), ead);
	}
	
	@Override
	protected Collection<Patient> getPatients(){
		return t.getPatients(t.getDataset(datasetDescription));
	}
	
	@Override
	protected boolean findInTestResults(String sampleId, String nucleotides) {
		Query q = t.createQuery("select tr.patient.id, tr.testDate from TestResult tr where tr.sampleId = :sampleId group by tr.patient.id");
		q.setParameter("sampleId", sampleId);
		if(q.list().size() > 0){
			Object[] r = (Object[])q.list().iterator().next();
			Patient p = t.getPatient((Integer)r[0]);
			Date d = (Date)r[1];
			
			ViralIsolate vi = super.createViralIsolate(sampleId, d, nucleotides);
			p.addViralIsolate(vi);
			return true;
		}
		return false;
	}
	
	public static void main(String args[]) {
		Arguments as = new Arguments();
    	ValueArgument conf			= as.addValueArgument("conf-dir", "configuration directory", false);
    	PositionalArgument user		= as.addPositionalArgument("regadb user", true);
    	PositionalArgument pass		= as.addPositionalArgument("regadb password", true);
    	PositionalArgument dataset	= as.addPositionalArgument("regadb dataset", true);
		PositionalArgument seqDir = as.addPositionalArgument("sequence-dir", true);
		if(!as.handle(args))
			return;
		
		if(conf.isSet())
			RegaDBSettings.createInstance(conf.getValue());
		else
			RegaDBSettings.createInstance();
		
		try {
			AddNewViralIsolates anv = new AddNewViralIsolates(user.getValue(), pass.getValue(), dataset.getValue());
			anv.run(seqDir.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
