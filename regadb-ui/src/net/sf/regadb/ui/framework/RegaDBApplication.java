package net.sf.regadb.ui.framework;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.ui.transaction.CannotCreateTransactionException;
import net.sf.witty.wt.core.WEnvironment;
import net.sf.witty.wt.widgets.WApplication;

public class RegaDBApplication extends WApplication
{
	private Login login_;
	private Transaction transaction_;
	
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
	
	public Transaction createTransaction() throws CannotCreateTransactionException
	{
		if(transaction_==null)
			transaction_ = login_.createTransaction();
		else
			throw new CannotCreateTransactionException();
		
		return transaction_;
	}
	
	public Transaction getCurrentTransaction()
	{
		return transaction_;
	}
	
	public void commitCurrentTransaction()
	{
		transaction_.commit();
		transaction_ = null;
	}
	
	public void rollbackCurrentTransaction()
	{
		transaction_.rollback();
		transaction_ = null;
	}

    public Login getLogin() 
    {
        return login_;
    }
    
    public void login(String uid, String pwd) throws WrongUidException, WrongPasswordException
    {
    	login_ = Login.authenticate(uid, pwd);
    }
}