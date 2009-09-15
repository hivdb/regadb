package net.sf.regadb.ui.framework;

import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WtServlet;

@SuppressWarnings("serial")
public class RegaDBMain extends WtServlet
{
	public RegaDBMain()
	{
		super();
	}

	@Override
	public WApplication createApplication(WEnvironment env)
	{
		RegaDBApplication app;
		
		switch(RegaDBSettings.getInstance().getAccessPolicyConfig().getAccessMode()){
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
		return (RegaDBApplication)WApplication.getInstance();
	}
}
