package com.pharmadm.custom.rega.queryeditor;

import com.pharmadm.util.work.WorkManager;

public interface FrontEnd {
	public void showException(Exception e, String title);
	public WorkManager getWorkManager();
}
