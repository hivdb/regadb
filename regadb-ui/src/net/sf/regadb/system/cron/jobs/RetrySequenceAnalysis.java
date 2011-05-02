package net.sf.regadb.system.cron.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.ResistanceInterpretationAnalysis;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.SubtypeAnalysis;
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

	public void execute(Login login){
			
		try{
			
			retryBlast(login);
			retryResistanceAnalysis(login);
			retrySubtypeAnalysis(login);
			
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			login.closeSession();
		}
	}

	@SuppressWarnings("unchecked")
	public void retryBlast(Login login){
		Transaction t = login.createTransaction();
		Query q = t.createQuery("select v from ViralIsolate v where v.genome is null");

		for(ViralIsolate v : (List<ViralIsolate>)q.list()){
			if(v.getNtSequences().size() > 0){
				doBlastAnalysis(v, login.getUid());
			}
		}

		t.commit();
	}
	
	private void doBlastAnalysis(ViralIsolate v, String uid){
		BlastAnalysis blastAnalysis = new BlastAnalysis(v.getNtSequences().iterator().next(), uid);
        try{
            blastAnalysis.launch();
            Genome g = blastAnalysis.getGenome();
            v.setGenome(g);
        } catch(Exception e){
        	e.printStackTrace();
        }
	}

	@SuppressWarnings("unchecked")
	private void retryResistanceAnalysis(Login login){
		Transaction t = login.createTransaction();
		Map<Integer,List<Test>> gssTests = getGssTests(t);
	
		for(Map.Entry<Integer, List<Test>> me : gssTests.entrySet()){
			Integer genomeIi = me.getKey();
			
			for(Test gssTest : me.getValue()){
				Query q = t.createQuery("select v from ViralIsolate v where v.genome.genomeIi = "+ genomeIi +" and v.viralIsolateIi not in "
						+"(select distinct tr.viralIsolate.viralIsolateIi from TestResult tr where tr.test.testIi = "+ gssTest.getTestIi()+") "
						+"order by v.sampleId");
				
				for(ViralIsolate v : (List<ViralIsolate>)q.list()){
					doResistanceAnalysis(login, v, gssTest);
				}
			}
		}
		
		t.commit();
	}

	private void doResistanceAnalysis(Login login, ViralIsolate v, Test gssTest){
		log(v, gssTest);
		
		ResistanceInterpretationAnalysis ria = new ResistanceInterpretationAnalysis(v, gssTest, login.getUid());
		ria.launch(login);
	}
	
	@SuppressWarnings("unchecked")
	private void retrySubtypeAnalysis(Login login){
		Transaction t = login.createTransaction();
		Test subtypeTest = t.getTest(StandardObjects.getSubtypeTestDescription());
		Query q = t.createQuery("select g, nt from ViralIsolate v join v.genome g join v.ntSequence nt where nt.ntSequenceIi not in " +
				"(select ntSequenceIi from TestResult tr where tr.test.testIi = "+ subtypeTest.getTestIi() +")");
	
		for(Object[] o : (List<Object[]>)q.list()){
			Genome g = (Genome)o[0];
			NtSequence nt = (NtSequence)o[1];
			
			try{
				dosubtypeAnalysis(login, nt, subtypeTest, g);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	
		t.commit();
	}
	
	private void dosubtypeAnalysis(Login login, NtSequence nt, Test subtypeTest, Genome genome) throws ServiceException {
		log(nt, subtypeTest);
		
		SubtypeAnalysis sa = new SubtypeAnalysis(nt, subtypeTest, genome, login.getUid());
		sa.launch(login);
	}
	
	@SuppressWarnings("unchecked")
	private Map<Integer,List<Test>> getGssTests(Transaction t){
		
		Map<Integer,List<Test>> gssTests = new HashMap<Integer,List<Test>>();
		Query q = t.createQuery("select tt.genome.genomeIi, t from Test t join t.testType tt where tt.description = '"+ StandardObjects.getGssDescription()
				+"' order by tt.genome.genomeIi");
		
		for(Object[] o : (List<Object[]>)q.list()){
			Integer genomeIi = (Integer)o[0];
			Test test = (Test)o[1];
			
			List<Test> tests = gssTests.get(genomeIi);
			if(tests == null){
				tests = new ArrayList<Test>();
				gssTests.put(genomeIi, tests);
			}
			tests.add(test);
		}
		
		return gssTests;
	}
	
	private void log(ViralIsolate v, Test t){
		System.err.println(v.getSampleId() +","+ t.getDescription());
	}
	
	private void log(NtSequence nt, Test t){
		System.err.println(nt.getViralIsolate().getSampleId() +","+ nt.getLabel() +","+ t.getDescription());
	}
	
	public static void main(String[] args) throws JobExecutionException{
		RegaDBSettings.createInstance();
		
		RetrySequenceAnalysis rsa = new RetrySequenceAnalysis();
		rsa.execute("admin","admin");
	}
}
