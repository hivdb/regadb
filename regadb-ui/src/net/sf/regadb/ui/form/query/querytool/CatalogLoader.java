package net.sf.regadb.ui.form.query.querytool;


import net.sf.regadb.ui.form.query.querytool.widgets.ProgressReporter;
import net.sf.witty.wt.i8n.WMessage;

import com.pharmadm.custom.rega.queryeditor.catalog.HibernateCatalogBuilder;
import com.pharmadm.custom.rega.queryeditor.catalog.AWCPrototypeCatalog.Status;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;


public class CatalogLoader implements ProgressReporter {
	private static CatalogLoader instance;
	private Thread thread;
	
	public static CatalogLoader getInstance() {
		if (instance == null) {
			instance = new CatalogLoader();
		}
		return instance;
	}
	
	private CatalogLoader()
	{
		Runnable run_ = new Runnable() {
			public void run() {
		        DatabaseManager.getInstance().fillCatalog(new HibernateCatalogBuilder());
			}
			
		};
		thread = new Thread(run_);
	}
	
	public int getProgress() {
		return DatabaseManager.getInstance().getAWCCatalog().getSizePercentage();
	}
	
	public static Status getStatus() {
		return DatabaseManager.getInstance().getAWCCatalog().getStatus();
	}

	public WMessage getMessage() {
		Status st = CatalogLoader.getStatus();
		if (st == Status.FAILED) {
			return new WMessage("form.query.querytool.catalog.failed");
		}
		else {
			return new WMessage("form.query.querytool.catalog.busy");
		}
	}

	public boolean isDone() {
		return DatabaseManager.getInstance().getAWCCatalog().getStatus() == Status.DONE ||
		DatabaseManager.getInstance().getAWCCatalog().getStatus() == Status.FAILED;
	}

	@Override
	public void start() {
		thread.start();
	}
}
