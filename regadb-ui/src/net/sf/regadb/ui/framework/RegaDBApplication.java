package net.sf.regadb.ui.framework;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.witty.wt.core.WEnvironment;
import net.sf.witty.wt.widgets.WApplication;

import org.hibernate.Criteria;

public class RegaDBApplication extends WApplication
{
	private Login login_;
	
	private RegaDBWindow window_;
	
	public RegaDBApplication(WEnvironment env)
	{
		super(env);
		
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
    
    public Transaction createTransaction()
    {
    	return login_.createTransaction();
    }
    
    public Criteria createCriteria(Class classType)
    {
        return login_.createCriteria(classType);
    }
}