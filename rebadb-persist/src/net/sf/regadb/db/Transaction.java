/*
 * Created on Dec 14, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.session.Login;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Represents a short-lived transaction to the database.
 * 
 * The transaction must be either committed or rolled back. You should make
 * your transaction short-lived, since it consumes database resources.
 * 
 * You may fetch persistent objects in one transaction, modify them at will,
 * and then save them in a new transaction.
 */
public class Transaction {

    private Login login;
    private Session session;

    public Transaction(Login login, Session session) {
        this.login = login;
        this.session = session;
        begin();
    }
    
    private void begin() {
        session.beginTransaction();
    }
    
    public void commit() {
        session.getTransaction().commit();
    }
    
    public void rollback() {
        session.getTransaction().rollback();
    }

    /*
     * Lists of attributes, tests, test types, etc...
     */
    @SuppressWarnings("unchecked")
    public List<Attribute> getAttributes() {
        Query q = session.createQuery("from Attribute attribute");
        
        return q.list();
    }

    public Attribute getAttribute(String name) {
        Query q = session.createQuery("from Attribute attribute where attribute.name = :name");
        q.setParameter("name", name);
        
        return (Attribute) q.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    public List<Test> getTests() {
        Query q = session.createQuery("from Test test");
        
        return q.list();
    }

    public Test getTest(String description) {
        Query q = session.createQuery("from Test test where test.description = :description");
        q.setParameter("description", description);
        
        return (Test) q.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<Protein> getProteins() {
        Query q = session.createQuery("from Protein protein");
        return q.list();
    }
    
    public Map<String, Protein> getProteinMap() {
        List<Protein> proteins = getProteins();
        Map<String, Protein> result = new HashMap<String, Protein>();
        
        for (Protein p:proteins)
            result.put(p.getAbbreviation(), p);
        

        return result;
    }
    
    /*
     * Dataset queries
     */

    /**
     * Obtain a list of all datasets in the database.
     */
    @SuppressWarnings("unchecked")
    public List<Dataset> getDatasets() {
        Query q = session.createQuery("from Dataset dataset");
        
        return q.list();
    }
    
    /**
     * Obtain a list of all datasets in the database which the current user can acces.
     */
    @SuppressWarnings("unchecked")
    public List<Dataset> getCurrentUsersDatasets() {
        Query q = session.createQuery("from Dataset dataset where dataset.datasetAccesses.permissions >= 1");

        return q.list();
    }
    

    public Dataset getDataset(String description) {
        Query q = session.createQuery("from Dataset dataset where dataset.description = :description");
        q.setParameter("description", description);
        
        return (Dataset) q.uniqueResult();
    }    

    /**
     * Returns patients in this data set according, checking access permissions.
     */
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients(Dataset dataset) {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                "from PatientImpl as patient " +
                "join patient.datasets as dataset " +
                "join dataset.datasetAccesses access " +
                "where dataset = :dataset " +
                "and access.permissions >= 1 " +
                "and access.settingsUser.uid = :uid " +
                "group by patient");
        q.setParameter("dataset", dataset);
        q.setParameter("uid", login.getUid());

        return q.list();
    }

    /**
     * Returns all patients, checking access permissions.
     */
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients() {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                getPatientsQuery() + 
                "group by patient");
        q.setParameter("uid", login.getUid());

        return q.list();
    }
    
    /**
     * Returns a Page of patients, checking access permissions,
     * checking all the filter constraints and grouped by the selected col.
     */
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients(int firstResult, int maxResults, String sortField, boolean ascending, String filterConstraints) 
    {
        String queryString = 	"select new net.sf.regadb.db.Patient(patient, max(access.permissions)) " +
                				getPatientsQuery();
        if(!filterConstraints.equals(" "))
        {
        	queryString += "and" + filterConstraints;
        }
        queryString += " group by patient, " + sortField;
        queryString += " order by " + sortField + (ascending?" asc":" desc");
        
        Query q = session.createQuery(queryString);
        q.setParameter("uid", login.getUid());
        
        q.setFirstResult(firstResult);
        q.setMaxResults(maxResults);
        
        return q.list();
    }
    
    public String getPatientsQuery()
    {
        return "from PatientImpl as patient " +
        "join patient.datasets as dataset " +
        "join dataset.datasetAccesses access " +
        "where access.permissions >= 1 " +
        "and access.id.settingsUser.uid = :uid ";
    }
    
    public int getPatientCount()
    {
        Query q = session.createQuery(
                "select count(patient) " +
                getPatientsQuery());
        q.setParameter("uid", login.getUid());
        
        return ((Integer)q.uniqueResult()).intValue();
    }
    
    public long getPatientCount(String filterConstraints) 
    {
        String queryString =    "select count(patient) " +
                                getPatientsQuery();
        if(!filterConstraints.equals(" "))
        {
            queryString += "and" + filterConstraints;
        }
        
        Query q = session.createQuery(queryString);
        q.setParameter("uid", login.getUid());
        
        return ((Long)q.uniqueResult()).longValue();
    }

    public Patient getPatient(Dataset dataset, String id) {
        Query q = session.createQuery(
                "select new net.sf.regadb.db.Patient(patient, max(access.permissions))" +
                "from PatientImpl as patient " +
                "join patient.datasets as dataset " +
                "join dataset.datasetAccesses access " +
                "where dataset = :dataset " +
                "and access.permissions >= 1 " +
                "and access.settingsUser.uid = :uid " +
                "and patient.patientId = :patientId " +
                "group by patient");
        
        q.setParameter("dataset", dataset);
        q.setParameter("uid", login.getUid());
        q.setParameter("patientId", id);
        
        return (Patient) q.uniqueResult();
    }
    
    /*
     * User settings queries
     */

    /**
     * Authenticated access to user settings.
     */
    public SettingsUser getSettingsUser(String uid) {
        Query q = session.createQuery("from SettingsUser user where user.uid = :uid");
        q.setParameter("uid", uid);
        
        return (SettingsUser) q.uniqueResult();
    }

    /**
     * Unauthenticated access to own settings.
     */
    public SettingsUser getSettingsUser() {
        Query q = session.createQuery("from SettingsUser user where user.uid = :uid");  
        q.setParameter("uid", login.getUid());

        return (SettingsUser) q.uniqueResult();
    }

    /**
     * Save settings.
     * @param settings
     */
    public void save(SettingsUser settings) {
        session.saveOrUpdate(settings);
    }

    /*
     * Patient queries
     */

    /**
     * Check access permissions, and save the patient.
     */
    public void save(Patient patient) {
        if (patient.getPrivileges().canWrite()) {
            session.saveOrUpdate(patient.getPatient());
        } // TODO: else throw exception
    }
    
    public Criteria createCriteria(Class c)
    {
        return session.createCriteria(c);
    }
}
