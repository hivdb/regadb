package net.sf.regadb.ui.form.batchtest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
	private boolean clear, cancel;
	private BatchTestStatus status;
	private Transaction trans_;
	private Login login_;
	private boolean cancelled = false;
	private int percent;
	private File logFile;
	
	public BatchTestRunningTest(Test t) {
		test = t;
		
		trans_ = RegaDBMain.getApp().createTransaction();
		login_ = RegaDBMain.getApp().getLogin();
		logFile = RegaDBMain.getApp().createTempFile(t.getDescription().toLowerCase().replaceAll(" ", "_"), "log");
		percent = 0;
	}
	
	public void run() {
		status = BatchTestStatus.RUNNING;
		
		PrintStream err = System.err;
		try {
			err = new PrintStream(logFile);
		} catch ( FileNotFoundException e ) {
			e.printStackTrace(err);
		}
		
		try {
			String def = test.getTestType().getTestObject().getDescription().toLowerCase();
			
			if ( def.equals("resistance test") ) {
				List<ViralIsolate> virals = trans_.getViralIsolates();
				int total = virals.size();
				
				for ( int i=0; i<total; i++ ) {
					percent = i * 100 / total;
					
					ViralIsolate vi = virals.get(i);
					new ResistanceInterpretationAnalysis(vi, test, login_.getUid()).launch(login_);
			        
					if ( cancel ) {
						status = BatchTestStatus.CANCELED;
						return;
					}
					
					try { Thread.sleep(200); } catch ( InterruptedException e ) {}
				}
			} else if ( def.equals("sequence analysis") ) {
				List<NtSequence> seqs = trans_.getSequences();
				int total = seqs.size();
				
				for ( int i=0; i<total; i++ ) {
					percent = i * 100 / total;
					
					NtSequence seq = seqs.get(i);
					new NtSequenceAnalysis(seq, test, login_.getUid()).launch(login_);
					
					if ( cancelled ) {
						status = BatchTestStatus.CANCELED;
						return;
					}
				}
//TODO			} else if ( def.equals("iets met viral isolates") ) {
			} else {
				System.err.println("test '" + def + "' not processable");
				status = BatchTestStatus.FAILED;
			}
			
			if ( status == BatchTestStatus.RUNNING ) {
				percent = 100;
				status = BatchTestStatus.DONE;
			}
		} catch ( Exception e ) {
			e.printStackTrace(err);
			status = BatchTestStatus.FAILED;
		}
	}
	
	public void cancel() {
		status = BatchTestStatus.CANCELING;
		cancelled = true;
	}
	
	public WMessage testName() {
		return new WMessage(test.getDescription(), true);
	}
	
	public void setClearChecked(boolean c) {
		clear = c;
	}
	public void setCancelChecked(boolean c) {
		cancel = c;
	}
	
	public boolean clearIsChecked() {
		return clear;
	}
	public boolean cancelIsChecked() {
		return cancel;
	}
	
	public boolean isTest(Test t) {
		return t.getDescription().equals(test.getDescription());
	}
	
	public BatchTestStatus getStatus() {
		return status;
	}
	
	public File getLogFile() {
		return logFile;
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
}
