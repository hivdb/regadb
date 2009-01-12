package net.sf.regadb.io.exportXML;

import java.util.Collection;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

public class ExportPatient<T> {
    private Login login;
    private String dataset;
    private ExportToXMLOutputStream<T> xmlOut;
    
    public ExportPatient(Login login, String dataset, ExportToXMLOutputStream<T> xmlout){
        setLogin(login);
        setDataset(dataset);
        setXmlOut(xmlout);
    }
        
    public void run(){
        Transaction t = getLogin().createTransaction();
        
        HibernateFilterConstraint hfc = new HibernateFilterConstraint();
        hfc.setClause(" dataset.description = :description ");
        hfc.addArgument("description", getDataset());
        long n = t.getPatientCount(hfc);
        int maxResults = 100;
        
        getXmlOut().start();
        for(int i=0; i < n; i+=maxResults){
            t.commit();
            t.clearCache();
            t = getLogin().createTransaction();

            Collection<Patient> patients = t.getPatients(t.getDataset(getDataset()),i,maxResults);
            for(Patient p : patients)
                getXmlOut().exportPatient(p);
        }
        getXmlOut().stop();
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

    protected void setXmlOut(ExportToXMLOutputStream<T> xmlout) {
        this.xmlOut = xmlout;
    }

    protected ExportToXMLOutputStream<T> getXmlOut() {
        return xmlOut;
    }
}
