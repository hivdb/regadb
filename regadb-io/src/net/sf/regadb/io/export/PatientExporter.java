package net.sf.regadb.io.export;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.TreeSet;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
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
        
        getOut().start();
        for(int i=0; i < n; i+=maxResults){
            t.commit();
            t.clearCache();
            t = getLogin().createTransaction();

            Collection<Patient> patients = t.getPatients(t.getDataset(getDataset()),i,maxResults);
            for(Patient p : patients)
                getOut().exportPatient(p);
        }
        getOut().stop();
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
    
    public static void main(String args[]) throws Exception{
    	Arguments as = new Arguments();
    	PositionalArgument user = as.addPositionalArgument("user", true);
    	PositionalArgument pass = as.addPositionalArgument("pass", true);
    	PositionalArgument output = as.addPositionalArgument("output.xml", true);
    	PositionalArgument ds = as.addPositionalArgument("dataset", true);
    	PositionalArgument patiendIds = as.addPositionalArgument("patient-ids", true);
    	ValueArgument conf = as.addValueArgument("conf-dir", "configuration directory", false);
    	
    	if(!as.handle(args))
    		return;

        if(conf.isSet())
        	RegaDBSettings.createInstance(conf.getValue());
        else
        	RegaDBSettings.createInstance();
        
        String[] idsArray = patiendIds.getValue().split(",");
        final TreeSet<String> ids = new TreeSet<String>();
        for(String id : idsArray)
        	ids.add(id);
        
        FileOutputStream fout = new FileOutputStream(new File(output.getValue()));
        PatientXMLOutputStream xmlout = new PatientXMLOutputStream(fout){
        	@Override
        	public void exportPatient(Patient p){
        		if(ids.contains(p.getPatientId())){
        			System.err.println("exporting: "+ p.getPatientId());
        			super.exportPatient(p);
        		}
        	}
        };
        
        Login login = Login.authenticate(user.getValue(), pass.getValue());
        
        PatientExporter<Patient> exportPatient = new PatientExporter<Patient>(login,ds.getValue(),xmlout);
        exportPatient.run();
    }
}
