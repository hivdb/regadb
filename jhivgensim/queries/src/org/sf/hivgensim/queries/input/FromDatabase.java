package org.sf.hivgensim.queries.input;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

import org.hibernate.ScrollableResults;
import org.sf.hivgensim.queries.framework.QueryInput;

public class FromDatabase extends QueryInput {
	
	private String loginname;
	private String passwd;
	
	public FromDatabase(String loginname, String passwd){
		this.loginname = loginname;
		this.passwd = passwd;
	}
	
	@Override
	protected void populateOutputList() {
		Login login = null;
		try
		{
			login = Login.authenticate(loginname,passwd);
		}
		catch (WrongUidException e)
		{
			e.printStackTrace();
		}
		catch (WrongPasswordException e)
		{
			e.printStackTrace();
		} 
        catch (DisabledUserException e) 
        {
            e.printStackTrace();
        }
        Transaction t = login.createTransaction();
        ScrollableResults patients = t.getPatientsScrollable();
        while(patients.next()){
        	Object[] os = patients.get();
            outputList.add((Patient)os[0]);            
        }
	}

}
