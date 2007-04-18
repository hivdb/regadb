package net.sf.regadb.db.test;

import java.util.List;
import java.util.Set;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

public class TestMeasurement 
{
    public static void main(String [] args)
    {
        Login login = null;
        try
        {
            login = Login.authenticate("kdforc0", "Vitabis1");
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
        }
        
        Login login2 = null;
        try
        {
            login2 = Login.authenticate("kdforc0", "Vitabis1");
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
        }
        
        Transaction t = login.createTransaction();
        
        List<Dataset> dsl = t.getCurrentUsersDatasets();
        Dataset ptds = null;
        for(Dataset ds : dsl)
        {
            if(ds.getDescription().equals("PT"))
                {
                    ptds = ds;
                }
        }
        Patient p1 = t.getPatient(ptds, "19217");
        
        t.commit();

        
        t = login.createTransaction();
        Patient p2 = t.getPatient(ptds, "19217");
        dsl = t.getCurrentUsersDatasets();
        ptds = null;
        for(Dataset ds : dsl)
        {
            if(ds.getDescription().equals("PT"))
                {
                    ptds = ds;
                }
        }
        p2 = t.getPatient(ptds, "19217");
        
        Set<TestResult>trl = p2.getTestResults();
        for(TestResult tr : trl)
        {
            tr.setValue("66");
            break;
        }

        t.commit();
        
        t = login.createTransaction();
        t.attach(p1);
        trl = p2.getTestResults();
        for(TestResult tr : trl)
        {
            tr.setValue("77");
            break;
        }
        t.commit();
    }
}
