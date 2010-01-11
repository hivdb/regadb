package net.sf.regadb.ui.framework;

import javax.servlet.ServletContext;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;

import com.pharmadm.custom.rega.queryeditor.catalog.HibernateCatalogBuilder;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.hibernate.HibernateConnector;
import com.pharmadm.custom.rega.queryeditor.port.hibernate.HibernateQuery;

import eu.webtoolkit.jwt.WEnvironment;

public class IntegratedRegaDBApplication extends RegaDBApplication{
    private String patientId;

	public IntegratedRegaDBApplication(WEnvironment env,
			ServletContext servletContext) {
		super(env, servletContext);
		
		login(env.getParameter("uid"));
		
		Transaction t = getLogin().createTransaction();
		Dataset ds = t.getDataset(env.getParameter("dataset"));

		patientId = env.getParameter("patient_id");
		Patient p = t.getPatient(ds, patientId);
		t.commit();
		
		
        getTree().getTreeContent().patientTreeNode.prograSelectNode();

		if(p == null){
			if(getRole().isSinglePatientView())
				getTree().getTreeContent().patientTreeNode.getAddActionItem().selectNode();
			else
				getTree().getTreeContent().patientTreeNode.getSelectActionItem().selectNode();
		}
		else{
			getTree().getTreeContent().patientTreeNode.setSelectedItem(p);
	        getTree().getTreeContent().patientTreeNode.expand();
	        getTree().getTreeContent().patientTreeNode.getViewActionItem().selectNode();
		}
        getTree().getRootTreeNode().refreshAllChildren();
	}

	public void login(String uid){
		setLogin(Login.getLogin(uid));
		DatabaseManager.initInstance(new RegaDBConnectorProvider(getLogin()), new HibernateQuery(), false);
		DatabaseManager.getInstance().fillCatalog(new HibernateCatalogBuilder());
	}
	
	public String getPatientId(){
	    return patientId;
	}
}
