package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;

import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.persistence.ExportToPersistentObjects;
import net.sf.regadb.util.hibernate.HibernateFilterConstraint;

public class CreateSnapshot {

	private ObjectOutputStream out;

	public CreateSnapshot(ObjectOutputStream out){
		this.out = out;
	}

	public void create(String uid, String passwd){
		try{
			ExportToPersistentObjects export = new ExportToPersistentObjects();
			Login login = Login.authenticate(uid, passwd);
			Transaction t = login.createTransaction();
			List<Dataset> datasets = t.getDatasets();
			String dataset = null;
			for (Dataset ds : datasets) {
				dataset = ds.getDescription();
				if(!dataset.equals("ghb")){
					continue;
				}
				System.err.println("now starting with: "+dataset);
				HibernateFilterConstraint hfc = new HibernateFilterConstraint();
				hfc.setClause(" dataset.description = :description ");
				hfc.addArgument("description", dataset);
				long n = t.getPatientCount(hfc);
				int maxResults = 1000;

				for (int i = 0; i < n; i += maxResults) {
					System.err.println(i+"/"+n);
					t.commit();
					t.clearCache();
					t = login.createTransaction();
					Collection<Patient> patients = t.getPatients(t.getDataset(dataset),i,maxResults);
					for (Patient p : patients) {
						export.initialize(p);
						out.writeObject(p);						
					}					
					out.reset();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException{
		if(args.length < 3){
			System.err.println("Usage: CreateSnapshot snapshot.output login password");
			System.exit(0);
		}
		long start = System.currentTimeMillis();
		CreateSnapshot cs = new CreateSnapshot(new ObjectOutputStream(new FileOutputStream(new File(args[0]))));
		cs.create(args[1],args[2]);
		long stop = System.currentTimeMillis();
		System.err.println("done in " + (stop - start) + " ms");
	}
}