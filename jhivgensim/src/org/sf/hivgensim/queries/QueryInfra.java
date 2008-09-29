package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.importXML.ImportHandler;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class QueryInfra {
	public QueryInfra() {
		
	}
	public void run(File file) {
		Login login = null;
		try
		{
			login = Login.authenticate("admin", "admin");
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
        
        ImportFromXML imp = new ImportFromXML();
        imp.loadDatabaseObjects(t);
        FileReader r;
        try 
        {
            r = new FileReader(file);
            imp.readPatients(new InputSource(r), new ImportHandler<Patient>() {
				@Override
				public void importObject(Patient p) {
					performQuery(p);
				}
            });
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (SAXException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}
	
	protected abstract void performQuery(Patient p);
}

