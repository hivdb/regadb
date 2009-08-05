package net.sf.hivgensim.queries.input;

import java.util.ArrayList;
import java.util.List;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;

import org.hibernate.ScrollableResults;

public class FromDatabase extends QueryInput {
	private List<Patient> patientsCache;
	private boolean cacheOn = false;

	private String loginname;
	private String passwd;

	public FromDatabase(String loginname, String passwd,
			IQuery<Patient> nextQuery) {
		super(nextQuery);
		this.loginname = loginname;
		this.passwd = passwd;
	}

	public void run() {
		if (patientsCache == null) {
			if(cacheOn)
				patientsCache = new ArrayList<Patient>();
			
			Login login = null;
			try {
				login = Login.authenticate(loginname, passwd);
			} catch (WrongUidException e) {
				e.printStackTrace();
			} catch (WrongPasswordException e) {
				e.printStackTrace();
			} catch (DisabledUserException e) {
				e.printStackTrace();
			}
			Transaction t = login.createTransaction();
			ScrollableResults patients = t.getPatientsScrollable();
			int i = 0;
			while (patients.next()) {
				if (i % 100 == 0) {
					System.out.print(".");
					if (i % 8000 == 0) {
						System.out.print("\n");
					}
				}
				i++;
				Object[] os = patients.get();
				getNextQuery().process((Patient) os[0]);
				
				if(cacheOn)
					patientsCache.add((Patient) os[0]);
			}
			getNextQuery().close();
		} else {
			for (Patient p : patientsCache) {
				getNextQuery().process(p);
			}
		}
	}
	
	public boolean isCacheOn() {
		return cacheOn;
	}

	public void setCacheOn(boolean cacheOn) {
		this.cacheOn = cacheOn;
	}
}
