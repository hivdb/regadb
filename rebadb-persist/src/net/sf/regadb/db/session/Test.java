/*
 * Created on Dec 15, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.session;

import java.util.Iterator;
import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Transaction;

public class Test {

    public static void main(String[] args) {        
        Test test = new Test("kdforc0", null);
        
        test.getPatients();
        //test.testModifySettings();
    }

    private void getPatients() {
        Transaction t = login.createTransaction();
        
        List<Dataset> datasets = t.getDatasets();
        
        for (Iterator<Dataset> i = datasets.iterator(); i.hasNext(); ) {
            Dataset d = i.next();
            
            System.err.println(d.getDescription());
            
            List<Patient> patients = t.getPatients(d);
            
            System.err.println(patients.size());
        }
    }

    private Login login;
    
    Test(String uid, String passwd) {
        login = Login.authenticate(uid, passwd);
        
        if (login == null) {
            throw new RuntimeException("Could not login with given username/password.");
        }
    }

    private void testModifySettings() {
        Transaction t = login.createTransaction();

        SettingsUser settings = t.getSettingsUser();

        t.commit();
        
        System.err.println(settings.getChartWidth());
        System.err.println(settings.getChartHeight());
        
        settings.setChartHeight(settings.getChartHeight()*2);
        
        t = login.createTransaction();

        t.save(settings);
        
        t.commit();
    }
}
