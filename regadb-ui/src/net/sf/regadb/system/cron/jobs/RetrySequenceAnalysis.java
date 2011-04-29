package net.sf.regadb.system.cron.jobs;

import java.util.List;

import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.ResistanceInterpretationAnalysis;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;
import org.quartz.JobExecutionException;

public class RetrySequenceAnalysis extends ParameterizedJob {

	@Override
	public void execute() throws JobExecutionException {
		execute(getParam("user"), getParam("pass"));
	}
	
	public void execute(String user, String pass) throws JobExecutionException {
		try {
			execute(Login.authenticate(user, pass));
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void execute(Login login){
			
		try{
			Transaction t = login.createTransaction();
			Query q;
			
			List<Test> gssTests = getGssTests(t);
			
			for(Test gssTest : gssTests){
				q = t.createQuery("select v from ViralIsolate v where v.viralIsolateIi not in "
						+"(select distinct tr.viralIsolate.viralIsolateIi from TestResult tr where tr.test.testIi = "+ gssTest.getTestIi()+") "
						+"order by v.sampleId");
				
				for(ViralIsolate v : (List<ViralIsolate>)q.list()){
					doResistanceAnalysis(login, v, gssTest);
				}
			}
			
			t.commit();
			
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			login.closeSession();
		}
	}
	
	private void doResistanceAnalysis(Login login, ViralIsolate v, Test gssTest){
		log(v, gssTest);
		
		ResistanceInterpretationAnalysis ria = new ResistanceInterpretationAnalysis(v, gssTest, login.getUid());
		ria.launch(login);
	}
	
	@SuppressWarnings("unchecked")
	private List<Test> getGssTests(Transaction t){
		Query q = t.createQuery("select t from Test t join t.testType tt where tt.description = '"+ StandardObjects.getGssDescription()
				+"' order by t.description");
		return (List<Test>)q.list();
	}
	
	private void log(ViralIsolate v, Test t){
		System.err.println(v.getSampleId() +","+ t.getDescription());
	}
	
	public static void main(String[] args) throws JobExecutionException{
		RegaDBSettings.createInstance();
		
		RetrySequenceAnalysis rsa = new RetrySequenceAnalysis();
		rsa.execute("admin","admin");
	}
}
