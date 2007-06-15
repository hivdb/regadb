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
        
        RegaDBSettings settings = RegaDBSettings.getInstance();
        String proxyHost = settings.getPropertyValue("http.proxy.url");
        String proxyPort = settings.getPropertyValue("http.proxy.port");
        
        if(proxyHost!=null && !"default".equals(proxyHost))
        {
            System.setProperty("http.proxyHost", proxyHost);
        }
        if(proxyPort!=null && !"default".equals(proxyPort))
        {
            System.setProperty("http.proxyPort", proxyPort);
        }
        
        return app;
	}
	
	public static RegaDBApplication getApp()
	{
		return (RegaDBApplication)WApplication.instance();
	}
}
