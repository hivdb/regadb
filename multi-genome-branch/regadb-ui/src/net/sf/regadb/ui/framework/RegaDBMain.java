package net.sf.regadb.ui.framework;

import net.sf.regadb.install.initdb.InitRegaDB;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Configuration;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WebController;

@SuppressWarnings("serial")
public class RegaDBMain extends WebController
{
	public RegaDBMain()
	{
		super(new Configuration());
	}

	@Override
	public WApplication createApplication(WEnvironment env)
	{
		RegaDBApplication app;
		RegaDBSettings settings;
		
		String confDir = getServletContext().getInitParameter("conf-dir");
		if(confDir != null)
		    settings = RegaDBSettings.getInstance(confDir);
		else
		    settings = RegaDBSettings.getInstance();
		
        settings.getProxyConfig().initProxySettings();
        
        InitRegaDB initDB = new InitRegaDB();
        initDB.run();
		
		switch(settings.getAccessPolicyConfig().getAccessMode()){
			case INTEGRATED:
				app = new IntegratedRegaDBApplication(env, this.getServletContext()); 
				break;
			default:
				app = new RegaDBApplication(env, this.getServletContext());
				break;
		}
        
        return app;
	}
	
	public static RegaDBApplication getApp()
	{
		return (RegaDBApplication)WApplication.instance();
	}
}
