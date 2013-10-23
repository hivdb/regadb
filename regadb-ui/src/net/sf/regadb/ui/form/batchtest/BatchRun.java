package net.sf.regadb.ui.form.batchtest;

import net.sf.regadb.db.Test;



public interface BatchRun {
	public CharSequence getRunName();
	public boolean isRunning();
	public CharSequence getStatusMessage();
	public CharSequence getPercent();
	public BatchTestStatus getStatus();
	public void cancel();
	public boolean isTest(Test t);
	public void start();
}
