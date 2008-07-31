package net.sf.regadb.ui.form.batchtest;

import java.util.List;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.wts.NtSequenceAnalysis;
import net.sf.regadb.service.wts.ResistanceInterpretationAnalysis;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.witty.wt.i8n.WMessage;

public class BatchTestRunningTest extends Thread {
	private Test test;
	private BatchTestStatus status;
	private int percent;
	private Transaction t;
	private Login copiedLogin;
	
	public BatchTestRunningTest(Test test) {
		this.test = test;
		percent = 0;
        t = RegaDBMain.getApp().createTransaction();
		copiedLogin = RegaDBMain.getApp().getLogin().copyLogin();
	}
	
	public void run() {
		status = BatchTestStatus.RUNNING;
		
		String testObject = test.getTestType().getTestObject().getDescription().toLowerCase();
		
		try {
			if ( testObject.equals("resistance test") ) {
				List<ViralIsolate> list = t.getViralIsolates();
				t.commit();
				new ResistanceBatchRun(list, copiedLogin).run();
			} 
			else if ( testObject.equals("sequence analysis") ) {
				List<NtSequence> list = t.getSequences();
				t.commit();
				new SequenceBatchRun(list, copiedLogin).run();
			}
//TODO			} else if ( def.equals("iets met viral isolates") ) {
			else {
				System.err.println("test '" + testObject + "' not processable");
				status = BatchTestStatus.FAILED;
			}
			
			if ( status == BatchTestStatus.RUNNING ) {
				percent = 100;
				status = BatchTestStatus.DONE;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			status = BatchTestStatus.FAILED;
		}
	}
	

	
	public void cancel() {
		status = BatchTestStatus.CANCELING;
	}
	
	public WMessage testName() {
		return new WMessage(test.getDescription(), true);
	}
	
	public boolean isTest(Test t) {
		return t.getDescription().equals(test.getDescription());
	}
	
	public BatchTestStatus getStatus() {
		return status;
	}
	
	public boolean isRunning() { return status == BatchTestStatus.RUNNING || status == BatchTestStatus.CANCELING; }
	
	public WMessage getPercent() {
		return new WMessage(percent + "%", true);
	}
	
	public WMessage getStatusMessage() {
		String key = "";
		
		if ( status == BatchTestStatus.RUNNING ) key = "form.batchtest.running.status.run";
		else if ( status == BatchTestStatus.DONE ) key = "form.batchtest.running.status.done";
		else if ( status == BatchTestStatus.FAILED ) key = "form.batchtest.running.status.failed";
		else if ( status == BatchTestStatus.CANCELING ) key = "form.batchtest.running.status.canceling";
		else if ( status == BatchTestStatus.CANCELED ) key = "form.batchtest.running.status.canceled";
		
		return new WMessage(key);
	}
	
	private abstract class BatchRun<DataType> {
		private List<DataType> list;
		private Login copiedLogin;
		
		public BatchRun(List<DataType> list, Login copiedLogin) {
			this.list = list;
			this.copiedLogin = copiedLogin;
		}
		
		public void run() {
			
			for ( int i=0; i<list.size(); i++ ) {
				percent = i * 100 / list.size();
				
				DataType t = list.get(i);
				runSingleTest(t, copiedLogin);
		        
				if (status == BatchTestStatus.CANCELING) {
					status = BatchTestStatus.CANCELED;
					return;
				}					
			}
		}
		
		public abstract void runSingleTest(DataType t, Login l);
	}
	
	private class ResistanceBatchRun extends BatchRun<ViralIsolate>{
		public ResistanceBatchRun(List<ViralIsolate> list, Login l) {
			super(list, l);
		}

		public void runSingleTest(ViralIsolate t, Login l) {
			new ResistanceInterpretationAnalysis(t, test, l.getUid()).launch(l);
		}
	}
	
	private class SequenceBatchRun extends BatchRun<NtSequence>{
		public SequenceBatchRun(List<NtSequence> list, Login l) {
			super(list, l);
		}

		public void runSingleTest(NtSequence t, Login l) {
			new NtSequenceAnalysis(t, test, l.getUid()).launch(l);
		}
	}	
}
