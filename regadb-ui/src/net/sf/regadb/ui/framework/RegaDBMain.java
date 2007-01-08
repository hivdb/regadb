package net.sf.regadb.ui.framework;

import net.sf.witty.wt.core.WApplication;
import net.sf.witty.wt.core.WEnvironment;
import net.sf.witty.wt.web.Configuration;
import net.sf.witty.wt.web.WebController;

public class RegaDBMain extends WebController
{
	public RegaDBMain()
	{
		super(new Configuration());
	}

	@Override
	public WApplication createApplication(WEnvironment env)
	{
		RegaDBApplication app = new RegaDBApplication(env);
        
        return app;
	}
	
	public static RegaDBApplication getApp()
	{
		return (RegaDBApplication)WApplication.instance();
	}
}
