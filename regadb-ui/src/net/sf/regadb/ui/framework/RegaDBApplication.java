package net.sf.regadb.ui.framework;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.ui.transaction.CannotCreateTransactionException;
import net.sf.witty.wt.core.WApplication;
import net.sf.witty.wt.core.WEnvironment;

public class RegaDBApplication extends WApplication
{
	private Login login_;
	private Transaction transaction_;
	
	public RegaDBApplication(WEnvironment env)
	{
		super(env);
		
		root().addWidget(new RegaDBWindow());
	}

	public RegaDBWindow getWindow()
	{
		return (RegaDBWindow)root().getChildren_().get(0);
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
}