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

public class ToSnapshot {

	private ObjectOutputStream snapshotstream;

	public ToSnapshot(File file) {
		try {
			snapshotstream = new ObjectOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateOutput() {
		try {
			Login login = Login.authenticate("gbehey0", "bla123");
			ExportToPersistentObjects export = new ExportToPersistentObjects();
			Transaction t = login.createTransaction();
			List<Dataset> datasets = t.getDatasets();
			String dataset = null;
			for (Dataset ds : datasets) {
				dataset = ds.getDescription();
				System.err.println("now starting with: "+dataset);
				HibernateFilterConstraint hfc = new HibernateFilterConstraint();
				hfc.setClause(" dataset.description = :description ");
				hfc.addArgument("description", dataset);
				long n = t.getPatientCount(hfc);
				int maxResults = 10;

				for (int i = 0; i < n; i += maxResults) {
					t.commit();
					t.clearCache();
					t = login.createTransaction();
					Collection<Patient> patients = t.getPatients(t.getDataset(dataset),i,maxResults);
					for (Patient p : patients) {
						export.initialize(p);
						snapshotstream.writeObject(p);
						if(i % 100 == 0){
							System.err.print("."); 
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
	}
}