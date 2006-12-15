/*
 * Created on Dec 14, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import net.sf.regadb.db.session.Login;

public class Transaction {

    private Login login;
    private Session session;

    public Transaction(Login login, Session session) {
        this.login = login;
        this.session = session;
        begin();
    }
    
    private void begin() {

    }
    
    public void commit() {
        
    }
    
    public void rollback() {
        
    }

    /*
     * Dataset queries
     */

    /**
     * Obtain a list of all datasets in the database.
     */
    public List<Dataset> getDatasets() {
        return null;
    }

    /**
     * Returns patients in this data set according to access
     * permissions.
     * 
     * @param dataset
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Patient> getPatients(Dataset dataset) {
        Query q = session.createQuery(
                "select distinct patient from Patient as patient" +
                "join patient.datasets as dataset " +
                "join dataset.datasetAccesses access " +
                "where access.permissions >= 3 " +
                "and access.settingsUser.uid = :uid");
        
        q.setParameter("uid", login.getUid());

        return q.list();
    }

    /*
     * User queries
     */

    public SettingsUser getSettingsUser(String uid, String passwd) {
        Query q = session.createQuery(
                "from SettingsUser user where user.uid = :uid");
  
        q.setParameter("uid", uid);

        return (SettingsUser) q.uniqueResult();
    }

    /*
     * Patient queries
     */
    
    /**
     * Check access permissions, and save the patient.
     */
    public void save(Patient patient) {
        
    }
    
    List<Patient> getPatientList() {
        return null;
    }
}
