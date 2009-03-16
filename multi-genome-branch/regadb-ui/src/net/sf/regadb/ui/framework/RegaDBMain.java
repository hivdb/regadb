package net.sf.regadb.ui.framework;

import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.Configuration;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WebController;

public class RegaDBMain extends WebController
{
	public RegaDBMain()
	{
		super(new Configuration());
	}

	@Override
	public WApplication createApplication(WEnvironment env)
	{
		RegaDBApplication app = new RegaDBApplication(env, this.getServletContext());
        
        RegaDBSettings.getInstance().getProxyConfig().initProxySettings();
        
        return app;
	}
	
	public static RegaDBApplication getApp()
	{
		return (RegaDBApplication)WApplication.instance();
	}
}
