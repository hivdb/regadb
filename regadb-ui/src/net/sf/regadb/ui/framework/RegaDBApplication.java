package net.sf.regadb.ui.framework;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.ui.framework.tree.TreeMenuNode;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.settings.Role;

import com.pharmadm.custom.rega.queryeditor.catalog.HibernateCatalogBuilder;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.hibernate.HibernateQuery;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WEvent;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class RegaDBApplication extends WApplication
{
	private Login login_;
	
	private RegaDBWindow window_;
	
	private ServletContext servletContext_;
	
	public RegaDBApplication(WEnvironment env, ServletContext servletContext)
	{
		super(env);
		
		setTitle("RegaDB");
		System.err.println("new regadb app");
		servletContext_ = servletContext;
		window_ = new RegaDBWindow();
		window_.init();
		getRoot().addWidget(window_);
		
		internalPathChanged().addListener(this.getRoot(), new Signal1.Listener<String>(){
			public void trigger(String ip) {
//                if (!getNavigation().getSelectedNode().canLeaveNode())
//                	return;
				
				String[] paths = ip.split("/");
				
				TreeMenuNode currentNode = window_.getTree_().getRootTreeNode();
				currentNode.gotoInternalPath(paths, 0);
			}
		});
	}

	public RegaDBWindow getWindow()
	{
		return window_;
	}
	
	public Tree getTree()
	{
		return getWindow().getTree_();
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
		DatabaseManager.initInstance(new RegaDBConnectorProvider(getLogin()), new HibernateQuery(), new HibernateCatalogBuilder(), false);
    }
    
    public void logout()
    {
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
		return getTree().getTreeContent().patientTreeNode.getSelectedItem();
	}
	
	public SettingsUser getSettingsUser(){
		return getLogin().createTransaction().getSettingsUser();
	}
	public Role getRole(){
		return RegaDBSettings.getInstance().getAccessPolicyConfig().getRole(getSettingsUser().getRole());
	}
	
	public Privileges getPrivilege(Dataset dataset){
		Transaction t = getLogin().createTransaction();
		String uid = t.getSettingsUser().getUid();
		t.commit();
		
		for(DatasetAccess da : dataset.getDatasetAccesses()){
			if(da.getId().getSettingsUser().getUid().equals(uid))
				return Privileges.getPrivilege(da.getPermissions());
		}
		return Privileges.NONE;
	}
	
	 protected void notify(WEvent event) throws IOException {
		try {
			super.notify(event);
			commitTransaction();
		} catch (Exception e) {
			if (isQuited())
				return;
			try {
				setError("Unexpected exception", null, e);
			} catch (Exception localException2) {
				localException2.printStackTrace();
			} finally {
				e.printStackTrace();
				quit();
			}
		}
	}

	  private void commitTransaction() {
		if (this.login_ == null)
			return;
		Transaction t = this.login_.getTransaction(false);
		if (t == null)
			return;
		try {
			t.commit();
		} catch (Exception e) {
			setError("Unexpected Database Error", null, e);
		}
	}

	  public void setError(String e1, String e2, Exception e) {
		getRoot().clear();
		WContainerWidget wc = new WContainerWidget(getRoot());
		wc.setStyleClass("regadb-error");
		new WText(tr("regadb.error.title"), wc);
		new WText(new WString("<p><b>" + e1 + "</b></p>"), wc);
		if (e2 != null)
			new WText(new WString(e2), TextFormat.PlainText, wc);
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		new WText(new WString(sw.toString()), TextFormat.PlainText, wc);
		quit();
		throw new RuntimeException("Unrecoverable error", e);
	}
	  
	public SequenceDb getSequenceDb() {
		return RegaDBSettings.getInstance().getSequenceDatabaseConfig().isConfigured() ?
			SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath()) : null;
	}
	
	@Override
	protected void finalize() throws Throwable{
		if (login_ != null)
			login_.closeSession();

		super.finalize();
	}
}