package net.sf.regadb.io.export;

import java.util.Collection;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

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
}
