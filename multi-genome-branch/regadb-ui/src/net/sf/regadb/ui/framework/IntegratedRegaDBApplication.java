package net.sf.regadb.ui.framework;

import javax.servlet.ServletContext;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;

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
		
		
        getTree().getTreeContent().singlePatientMain.prograSelectNode();

		if(p == null){
			if(getRole().isSinglePatientView())
				getTree().getTreeContent().patientAdd.selectNode();
			else
				getTree().getTreeContent().patientSelect.selectNode();
		}
		else{
			getTree().getTreeContent().patientSelected.setSelectedItem(p);
	        getTree().getTreeContent().patientSelected.expand();
	        getTree().getTreeContent().viewPatient.selectNode();
		}
        getTree().getRootTreeNode().refreshAllChildren();
	}

	public void login(String uid){
		setLogin(Login.getLogin(uid));
		DatabaseManager.initInstance(new HibernateQuery(), new HibernateConnector(getLogin().copyLogin(), false));
	}
	
	public String getPatientId(){
	    return patientId;
	}
}
