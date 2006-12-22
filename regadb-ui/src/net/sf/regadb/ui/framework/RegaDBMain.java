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
		WApplication app = new WApplication(env);
        app.root().addWidget(new RegaDBWindow());
        
        return app;
	}
	
	public static RegaDBWindow getWindow()
	{
		return (RegaDBWindow)WApplication.instance().root().getChildren_().get(0);
	}
	
	public static Tree getTree()
	{
		return getWindow().getTree_();
	}
	
	public static Header getHeader()
	{
		return getWindow().getHeader_();
	}
	
	public static FormContainer getFormContainer()
	{
		return getWindow().getContainer_();
	}
}
