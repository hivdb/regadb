package net.sf.regadb.ui.framework;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Session;

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

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t  = new Transaction(null, session);
		
		String path = RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath();
		if (path != null)
			SequenceDb.getInstance(path).init(t);
		
		session.close();
	}
}
