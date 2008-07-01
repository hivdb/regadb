package net.sf.regadb.ui.framework;

import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.witty.wt.Configuration;
import net.sf.witty.wt.WApplication;
import net.sf.witty.wt.WEnvironment;
import net.sf.witty.wt.WebController;

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
        
        RegaDBSettings.getInstance().initProxySettings();
        
        return app;
	}
	
	public static RegaDBApplication getApp()
	{
		return (RegaDBApplication)WApplication.instance();
	}
}
