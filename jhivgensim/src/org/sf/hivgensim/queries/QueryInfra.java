package org.sf.hivgensim.queries;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Set;

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

	public void runOnSnapshot(File file, String loginname, String passwd){
		try
		{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fis);
			Patient p;
			while((p = (Patient)in.readObject()) != null){				
				performQuery(p);
			}
			in.close();
		}
		catch (EOFException e) 
		{
			//end of file reached but unexpected?
			return;
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		} 
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} 

	}

	public void runOnXml(File file, String loginname, String passwd) {
		Login login = null;
		try
		{
			login = Login.authenticate(loginname, passwd);
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

	public <T> void createSnapshot(String filename, Set<T> set){
		try 
		{
			ObjectOutputStream snapshotstream = new ObjectOutputStream(new FileOutputStream(filename));
			for(T t : set){
				snapshotstream.writeObject(t);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	public <T> void createSnapshot(String filename, List<T> list){
		try 
		{
			ObjectOutputStream snapshotstream = new ObjectOutputStream(new FileOutputStream(filename));
			for(T t : list){
				snapshotstream.writeObject(t);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}


}

