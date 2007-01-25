package net.sf.regadb.db.test;

import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

public class TestPatient
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
		
		Transaction t = login.createTransaction();
		
		List<Patient> pList = t.getPatients(0, 10, -1, " ");
		for(Patient p : pList)
		{
			System.err.println(((Dataset)p.getDatasets().toArray()[0]).getDescription());
		}
	}
}
