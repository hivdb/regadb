package net.sf.regadb.io.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.exportXML.ExportToXMLOutputStream.PatientXMLOutputStream;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;
import net.sf.regadb.util.settings.RegaDBSettings;

public class PatientExporter<T> {
    private Login login;
    private String dataset;
    private ExportPatient<T> out;
    
    private List<String> errors = new ArrayList<String>();
    
    public PatientExporter(Login login, String dataset, ExportPatient<T> xmlout){
        setLogin(login);
        setDataset(dataset);
        setOut(xmlout);
    }
        
    public void run(){
        Transaction t = getLogin().createTransaction();
        
        HibernateFilterConstraint hfc = new HibernateFilterConstraint();
        hfc.setClause(" dataset.description = :description ");
        hfc.addArgument("description", getDataset());
        long n = t.getPatientCount(hfc);
        int maxResults = 100;
        
        getOut().start(t);
        for(int i=0; i < n; i+=maxResults){
            t.commit();
            t.clearCache();
            t = getLogin().createTransaction();

            Collection<Patient> patients = t.getPatients(t.getDataset(getDataset()),i,maxResults);
            for(Patient p : patients) {
            	try { 
            		getOut().exportPatient(t, p);
            	} catch (Exception e) {
            		String error 
            			= "<p><b>An exception was thrown while exporting patient " + p.getPatientId() + ".</b></p>"
            			+ "<p>" + stackTraceToString(e) + "</p>";
            		errors.add(error);
            	}
            }
        }
        getOut().stop(t);
        
        t.commit();
    }
    
	private String stackTraceToString(Throwable e) {
		String retValue = null;
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			retValue = sw.toString();
		} finally {
			try {
				if (pw != null)
					pw.close();
				if (sw != null)
					sw.close();
			} catch (IOException ignore) {
			}
		}
		return retValue;
	}

    protected void setLogin(Login login) {
        this.login = login;
    }

    protected Login getLogin() {
        return login;
    }

    protected void setDataset(String dataset) {
        this.dataset = dataset;
    }

    protected String getDataset() {
        return dataset;
    }

    protected void setOut(ExportPatient<T> out) {
        this.out = out;
    }

    protected ExportPatient<T> getOut() {
        return out;
    }
    
    public static void main(String args[]) throws FileNotFoundException, WrongUidException, WrongPasswordException, DisabledUserException {
    	Arguments as = new Arguments();
    	PositionalArgument user = as.addPositionalArgument("user", true);
    	PositionalArgument pass = as.addPositionalArgument("pass", true);
    	PositionalArgument output = as.addPositionalArgument("output.xml", true);
    	PositionalArgument ds = as.addPositionalArgument("dataset", true);
    	final PositionalArgument patiendIds = as.addPositionalArgument("patient-ids", false);
    	ValueArgument conf = as.addValueArgument("conf-dir", "configuration directory", false);
    	
    	if(!as.handle(args))
    		return;

        if(conf.isSet())
        	RegaDBSettings.createInstance(conf.getValue());
        else
        	RegaDBSettings.createInstance();
        
        final TreeSet<String> ids = new TreeSet<String>();
        if (patiendIds.getValue() != null) {
	        String[] idsArray = patiendIds.getValue().split(",");
	        for(String id : idsArray)
	        	ids.add(id);
        }
        
        FileOutputStream fout = new FileOutputStream(new File(output.getValue()));
        PatientXMLOutputStream xmlout = new PatientXMLOutputStream(fout){
        	@Override
        	public void exportPatient(Transaction t, Patient p){
        		if(patiendIds.getValue() == null || ids.contains(p.getPatientId())){
        			System.err.println("exporting: "+ p.getPatientId());
        			super.exportPatient(t, p);
        		}
        	}
        };
        
        Login login = Login.authenticate(user.getValue(), pass.getValue());
        
        PatientExporter<Patient> exportPatient = new PatientExporter<Patient>(login,ds.getValue(),xmlout);
        exportPatient.run();
    }
    
    public List<String> getErrors() {
    	return errors;
    }
}
