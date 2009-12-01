package net.sf.regadb.system;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import net.sf.regadb.install.initdb.InitRegaDB;
import net.sf.regadb.system.cron.Cron;
import net.sf.regadb.util.settings.RegaDBSettings;

public class Launcher implements javax.servlet.ServletContextListener{
	private Cron cron = null;
	
	public void contextDestroyed(ServletContextEvent arg0) {
		System.err.println("RegaDB stopped");
		
		if(cron != null)
			cron.stop();
	}

	public void contextInitialized(ServletContextEvent arg0) {
		System.err.println("RegaDB started");
		
        initSettings(arg0.getServletContext());
        initDB();
        initCron();
	}
	
	private void initSettings(ServletContext context){
		RegaDBSettings settings;
		
		String confDir = context.getInitParameter("conf-dir");
		if(confDir != null)
		    settings = RegaDBSettings.createInstance(confDir);
		else
		    settings = RegaDBSettings.createInstance();
		
        settings.getProxyConfig().initProxySettings();
	}
	
	private void initDB(){
        InitRegaDB initDB = new InitRegaDB();
        initDB.run();
	}
	
	private void initCron(){
		if(RegaDBSettings.getInstance().getCronConfig().getJobs().size() > 0){		
			cron = new Cron();
			cron.start();
		}
	}
}
