package net.sf.regadb.ui.framework;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.Role;

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.hibernate.HibernateConnector;
import com.pharmadm.custom.rega.queryeditor.port.hibernate.HibernateQuery;

import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;

public class RegaDBApplication extends WApplication
{
	private Login login_;
	
	private RegaDBWindow window_;
	
	private ServletContext servletContext_;
	
	public RegaDBApplication(WEnvironment env, ServletContext servletContext)
	{
		super(env);
		System.err.println("new regadb app");
		
		servletContext_ = servletContext;
		window_ = new RegaDBWindow();
		window_.init();
		root().addWidget(window_);
	}

	public RegaDBWindow getWindow()
	{
		return window_;
	}
	
	public Tree getTree()
	{
		return getWindow().getTree_();
	}
	
	public Header getHeader()
	{
		return getWindow().getHeader_();
	}
	
	public FormContainer getFormContainer()
	{
		return getWindow().getContainer_();
	}	
	
    public Login getLogin() 
    {
        return login_;
    }
    protected void setLogin(Login login)
    {
    	login_ = login;
    }
    
    public void login(String uid, String pwd) throws WrongUidException, WrongPasswordException, DisabledUserException
    {
    	login_ = Login.authenticate(uid, pwd);
		DatabaseManager.initInstance(new HibernateQuery(), new HibernateConnector(login_.copyLogin(), false));
    }
    
    public void logout()
    {
        //close the wt and servlet session
        login_.closeSession();
        login_=null;
    }
    
    public Transaction createTransaction()
    {
    	return login_.createTransaction();
    }

	public ServletContext getServletContext()
	{
		return servletContext_;
	}
	
	/*
	 * This function creates a temporary file
	 * If something goes wrong during this process
	 * a null reference is returned
	 * */
	public File createTempFile(String prefix, String postfix)
	{
		File directory = (File)getServletContext().getAttribute("javax.servlet.context.tmpdir");
		File file = null;
		try
		{
			file = File.createTempFile(prefix, postfix, directory);
		}
		catch (IOException e)
		{
			
		}
		
		return file;
	}
	
	public Patient getSelectedPatient(){
		return getTree().getTreeContent().patientSelected.getSelectedItem();
	}
	
	public SettingsUser getSettingsUser(){
		return getLogin().createTransaction().getSettingsUser();
	}
	public Role getRole(){
		return RegaDBSettings.getInstance().getAccessPolicyConfig().getRole(getSettingsUser().getRole());
	}
}