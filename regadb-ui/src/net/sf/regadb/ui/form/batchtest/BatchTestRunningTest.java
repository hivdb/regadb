package net.sf.regadb.ui.form.batchtest;

import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.wts.ResistanceInterpretationAnalysis;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.SubtypeAnalysis;
import eu.webtoolkit.jwt.WString;

public class BatchTestRunningTest extends Thread implements BatchRun {
	private Test test;
	private BatchTestStatus status;
	private Login login;
	
	private int processedTests = 0;
	private int testsToProcess = 0;
	
	public BatchTestRunningTest(Login copiedLogin, Test test) {
		this.test = test;
		this.login = copiedLogin;
	}
	
	public void run() {
		status = BatchTestStatus.RUNNING;
		
		String testObject = test.getTestType().getTestObject().getDescription().toLowerCase();
		
		try {
			if ( testObject.equals("resistance test") ) {
				Transaction t = login.createTransaction();
				List<ViralIsolate> list = t.getViralIsolatesByGenome(
						test.getTestType().getGenome());
				t.commit();
				new ResistanceBatchRun(list, login).run();
			} 
			else if ( testObject.equals("sequence analysis") ) {
				Transaction t = login.createTransaction();
				List<NtSequence> list = t.getSequences();
				t.commit();
				new SequenceBatchRun(list, login).run();
			}
			else if ( testObject.equals("viral isolate analysis")) {
				//TODO
				System.err.println("test '" + testObject + "' not yet supported");
			}			
			else {
				System.err.println("test '" + testObject + "' not processable");
				status = BatchTestStatus.FAILED;
			}
			
			if ( status == BatchTestStatus.RUNNING ) {
				status = BatchTestStatus.DONE;
			} else if( status == BatchTestStatus.CANCELING){
				status = BatchTestStatus.CANCELED;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			status = BatchTestStatus.FAILED;
		} finally {
			login.closeSession();
		}
	}
	

	
	public void cancel() {
		status = BatchTestStatus.CANCELING;
	}
	
	public CharSequence getRunName() {
		return test.getDescription();
	}
	
	public boolean isTest(Test t) {
		return t.getDescription().equals(test.getDescription());
	}
	
	public BatchTestStatus getStatus() {
		return status;
	}
	
	public boolean isRunning() { return status == BatchTestStatus.RUNNING || status == BatchTestStatus.CANCELING; }
	
	public CharSequence getPercent() {
		return this.processedTests + "/" + this.testsToProcess;
	}
	
	public WString getStatusMessage() {
		String key = "";
		
		if ( status == BatchTestStatus.RUNNING ) key = "form.batchtest.running.status.run";
		else if ( status == BatchTestStatus.DONE ) key = "form.batchtest.running.status.done";
		else if ( status == BatchTestStatus.FAILED ) key = "form.batchtest.running.status.failed";
		else if ( status == BatchTestStatus.CANCELING ) key = "form.batchtest.running.status.canceling";
		else if ( status == BatchTestStatus.CANCELED ) key = "form.batchtest.running.status.canceled";
		
		return WString.tr(key);
	}
	
	private abstract class BatchRun<DataType> {
		private List<DataType> list;
		private Login copiedLogin;
		
		public BatchRun(List<DataType> list, Login copiedLogin) {
			this.list = list;
			this.copiedLogin = copiedLogin;
		}
		
        public void run() {
        	testsToProcess = list.size();
        	
            while (processedTests<testsToProcess && status == BatchTestStatus.RUNNING) {            	
                DataType t = list.get(processedTests);
                try {
                    runSingleTest(t, copiedLogin);
                }
                catch (Throwable e) {
                    status = BatchTestStatus.FAILED;
                }
                processedTests++;
            }
        }
		
		public abstract void runSingleTest(DataType t, Login l);
	}
	
	private class ResistanceBatchRun extends BatchRun<ViralIsolate>{
		public ResistanceBatchRun(List<ViralIsolate> list, Login l) {
			super(list, l);
		}

		public void runSingleTest(ViralIsolate t, Login l) {
			try {
				new ResistanceInterpretationAnalysis(t, test, l.getUid()).launch(l);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class SequenceBatchRun extends BatchRun<NtSequence>{
		public SequenceBatchRun(List<NtSequence> list, Login l) {
			super(list, l);
		}

		public void runSingleTest(NtSequence t, Login l) {
			try {
				for(TestResult res : t.getTestResults()){
					if(res.getTest().getDescription().equals("Rega Subtype Tool") &&
							res.getValue() != null && !res.getValue().equals("")){
						return;
					}
				}
				Transaction trans = l.createTransaction();
				//TODO remove hard-coded (?)
				Genome g = trans.getGenome("HIV-1");
				trans.commit();
				
				new SubtypeAnalysis(t, test, g, l.getUid()).launch(l);
                
            } catch (ServiceException e) {
                e.printStackTrace();
            }
		}
	}
}
