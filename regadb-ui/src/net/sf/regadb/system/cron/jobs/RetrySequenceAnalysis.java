package net.sf.regadb.system.cron.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

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
import net.sf.regadb.util.mail.MailUtils;
import net.sf.regadb.util.settings.EmailConfig;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;
import org.quartz.JobExecutionException;

public class RetrySequenceAnalysis extends ParameterizedJob {
	private boolean sendMail;
	
	private PrintStream logOutput;
	private StringBuilder logString = new StringBuilder();
	
	@Override
	public void execute() throws JobExecutionException {
		sendMail = false;
		
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
		
		try {
			logOutput = new PrintStream(
							new FileOutputStream(
								new File(
									RegaDBSettings.getInstance().getInstituteConfig().getLogDir().getAbsolutePath()
									+ File.separator +"retry_sequence_analysis-"
									+ new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()) +".log"
									)));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			logOutput = System.err;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try{
			log("Batch analysis started at: "+ dateFormat.format(new Date()) +"\n\n");
			
			retryBlast(login);
			retryResistanceAnalysis(login);
			retrySubtypeAnalysis(login);
			
			checkAlignments(login);
			
			log("Batch analysis finished at: "+ dateFormat.format(new Date()) +"\n\n");

		} catch(Exception e){
			log("Batch analysis stopped at: "+ dateFormat.format(new Date()) +"\n"+
					"stop reason:\n"+ e.getMessage());
			e.printStackTrace();
		} finally{
			login.closeSession();
			
			if(logOutput != System.err)
				logOutput.close();
		}

		sendLog();
	}

	@SuppressWarnings("unchecked")
	public void retryBlast(Login login){
		Transaction t = login.createTransaction();
		Query q = t.createQuery("select v from ViralIsolate v where v.genome is null");
		
		List<ViralIsolate> r = (List<ViralIsolate>)q.list();
		log("blast analyses ("+ r.size() +"):\n");

		for(ViralIsolate v : r){
			if(v.getNtSequences().size() > 0){
				sendMail = true;
				log("\t"+ v.getSampleId() +",");
				try {
					doBlastAnalysis(v, login.getUid());
					log("ok");
				} catch (ServiceException e) {
					log("fail,"+ e.getMessage());
					e.printStackTrace();
				}
				log("\n");
			}
		}

		t.commit();
	}
	
	private void doBlastAnalysis(ViralIsolate v, String uid) throws ServiceException{
		BlastAnalysis blastAnalysis = new BlastAnalysis(v.getNtSequences().iterator().next(), uid);
        blastAnalysis.launch();
        Genome g = blastAnalysis.getGenome();
        v.setGenome(g);
	}

	@SuppressWarnings("unchecked")
	private void retryResistanceAnalysis(Login login){
		log("resistance analysis:\n");
		
		Transaction t = login.createTransaction();
		Map<Integer,List<Test>> gssTests = getGssTests(t);
		t.commit();
	
		for(Map.Entry<Integer, List<Test>> me : gssTests.entrySet()){
			Integer genomeIi = me.getKey();
			
			for(Test gssTest : me.getValue()){
				Query q = t.createQuery("select v from ViralIsolate v where v.genome.genomeIi = "+ genomeIi +" and v.viralIsolateIi not in "
						+"(select distinct tr.viralIsolate.viralIsolateIi from TestResult tr where tr.test.testIi = "+ gssTest.getTestIi()+") "
						+"order by v.sampleId");
				List<ViralIsolate> r = (List<ViralIsolate>)q.list();
				log("\t"+ gssTest.getDescription() +"("+ r.size() +"):\n");
				for(ViralIsolate v : r){
					sendMail = true;
					log("\t\t"+ v.getSampleId() +",");
					
					try{
						doResistanceAnalysis(login, v, gssTest);
						log("ok");
					}catch(Exception e){
						log("fail,"+ e.getMessage());
					}
					
					log("\n");
				}
			}
		}
	}

	private void doResistanceAnalysis(Login login, ViralIsolate v, Test gssTest) throws ServiceException {
		ResistanceInterpretationAnalysis ria = new ResistanceInterpretationAnalysis(v, gssTest, login.getUid());
		ria.launch(login);
	}
	
	@SuppressWarnings("unchecked")
	private void retrySubtypeAnalysis(Login login){
		Transaction t = login.createTransaction();
		Test subtypeTest = t.getTest(StandardObjects.getSubtypeTestDescription());
		Query q = t.createQuery("select g, nt, v from ViralIsolate v join v.genome g join v.ntSequences nt where nt.ntSequenceIi not in " +
				"(select tr.ntSequence.ntSequenceIi from TestResult tr where tr.test.testIi = "+ subtypeTest.getTestIi() +")");
		List<Object[]> list = (List<Object[]>)q.list();
		t.commit();
		
		log("subtype analysis ("+ list.size() +"):\n");
		for(Object[] o : list){
			Genome g = (Genome)o[0];
			NtSequence nt = (NtSequence)o[1];
			ViralIsolate v = (ViralIsolate)o[2];
			
			sendMail = true;
			log("\t"+ v.getSampleId() +","+ nt.getLabel() +",");
			try{
				dosubtypeAnalysis(login, nt, subtypeTest, g);
				log("ok");
			} catch(Exception e){
				e.printStackTrace();
				log("fail,"+ e.getMessage());
			}
			log("\n");
		}
	}
	
	private void dosubtypeAnalysis(Login login, NtSequence nt, Test subtypeTest, Genome genome) throws ServiceException {
		SubtypeAnalysis sa = new SubtypeAnalysis(nt, subtypeTest, genome, login.getUid());
		sa.launch(login);
	}
	
	@SuppressWarnings("unchecked")
	private Map<Integer,List<Test>> getGssTests(Transaction t){
		
		Map<Integer,List<Test>> gssTests = new HashMap<Integer,List<Test>>();
		Query q = t.createQuery("select tt.genome.genomeIi, t from Test t join t.testType tt where tt.description = '"+ StandardObjects.getGssDescription()
				+"' or tt.description = '"+ StandardObjects.getTDRDescription() +"' order by tt.genome.genomeIi");
		
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
	
	@SuppressWarnings("unchecked")
	private void checkAlignments(Login login){
		log("alignments:\n");
		
		Transaction t = login.createTransaction();
		Query q = t.createQuery("select v from ViralIsolate v where v.genome is null or v.viralIsolateIi not in " +
				"(select distinct nt.viralIsolate.viralIsolateIi from AaSequence aa join aa.ntSequence nt)");
		for(ViralIsolate v : (List<ViralIsolate>)q.list()){
			log("\t"+ v.getSampleId() +"\n");
		}
		t.commit();
	}
	
	private void sendLog(){
		if (!sendMail)
			return;
		
		EmailConfig ecfg = RegaDBSettings.getInstance().getInstituteConfig().getEmailConfig();
		if(ecfg != null){
			try {
				MailUtils.sendMail(ecfg.getHost(), ecfg.getFrom(), ecfg.getTo(), "RegaDB batch analysis retry log", logString.toString());
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws JobExecutionException{
		RegaDBSettings.createInstance();
		
		RetrySequenceAnalysis rsa = new RetrySequenceAnalysis();
		rsa.execute("admin","admin");
	}
	
	private void log(String msg){
		logString.append(msg);
		logOutput.print(msg);
	}
}
