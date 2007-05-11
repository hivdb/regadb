package net.sf.regadb.ui.framework;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletContext;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.witty.wt.WApplication;
import net.sf.witty.wt.WEnvironment;

public class RegaDBApplication extends WApplication
{
	private Login login_;
	
	private RegaDBWindow window_;
	
	private ServletContext servletContext_;
	
	public RegaDBApplication(WEnvironment env, ServletContext servletContext)
	{
		super(env);
		
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
    
    public void login(String uid, String pwd) throws WrongUidException, WrongPasswordException
    {
    	login_ = Login.authenticate(uid, pwd);
    }
    
    public void logout()
    {
        //!! close session (hibernate/wt/servlet)
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
}