/*
 * Created on Dec 15, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.session;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;

public class Test {

    public static void main(String[] args) {        
        Test test = new Test("kdforc0", "Vitabis1");
        
        test.getPatients();
        //test.testModifySettings();
        test.modifyPatient();
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
        
        System.err.println("Total patients: " + t.getPatients().size());
        
        t.commit();
    }

    private void modifyPatient() {
        Transaction t = login.createTransaction();
        
        Patient p = t.getPatient(t.getDataset("TEST"), "12312");
        
        p.setFirstName("Flapfoo");
        
        ViralIsolate v = p.createViralIsolate();
        v.setSampleDate(new Date());
        v.setSampleId("Flupke");
        NtSequence s = new NtSequence(v);
        v.getNtSequences().add(s);
        s.setNucleotides("ACGT");
        
        TestResult result = new TestResult(t.getTest("CD4 Count (generic)"));
        result.setTestDate(new Date());
        result.setValue("<1234");
        
        Attribute genderAttribute = t.getAttribute("Gender", "RegaDB");
        PatientAttributeValue genderValue = p.createPatientAttributeValue(genderAttribute);
        AttributeNominalValue firstValue = genderAttribute.getAttributeNominalValues().iterator().next();
        genderValue.setAttributeNominalValue(firstValue);
        
        t.update(p);
        
        t.commit();
    }
    
    private Login login;
    
    Test(String uid, String passwd) {
        try
		{
			login = Login.authenticate(uid, passwd);
		}
		catch (WrongUidException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (WrongPasswordException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DisabledUserException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
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

        t.update(settings);
        
        t.commit();
    }
}
