package net.sf.regadb.db.test;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

import org.hibernate.Query;

public class TestPatient
{
	public static void main(String [] args)
	{
		Login login = null;
		try
		{
			login = Login.authenticate("admin", "admin");
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
        catch (DisabledUserException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		Transaction t = login.createTransaction();
		
		//Query q = t.createQuery("from TestType as testType where testType.description = :description and ((testType.genome is null and length(:organismName) = 0) or ((not testType.genome is null and length(:organismName) > 0) and (testType.genome.organismName = :organismName)))");
		Query q = t.createQuery("from TestType as testType where testType.description = :description and ((testType.genome is null and :organismName is null))");// or ((testType.genome is not null and :organismName is not null) and (testType.genome.organismName = ':organismName')))");
		String description = "Genotypic Susceptibility Score (GSS)";
		String organismName = null;
		q.setParameter("description", description);
		q.setParameter("organismName", organismName);
		
		System.out.println(q.list().size());
		
		
		
		//List<Patient> pList = t.getPatients(0, 10, "dataset.description", true, " ");
		/*for(Patient p : pList)
		{
			System.err.println(((Dataset)p.getDatasets().toArray()[0]).getDescription());
		}*/
	}
}
