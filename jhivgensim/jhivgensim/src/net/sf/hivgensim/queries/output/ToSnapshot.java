package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.sf.hivgensim.queries.framework.Query;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.persistence.ExportToPersistentObjects;

public class ToSnapshot {
	
	private ObjectOutputStream snapshotstream;
	
	public ToSnapshot(File file){
		try {
			snapshotstream = new ObjectOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void generateOutput(Query<Patient> query) {
		try 
		{
			ExportToPersistentObjects export = new ExportToPersistentObjects();
			
			int patientCounter = 0;
			for(Patient p : query.getOutputList()){
				export.initialize(p);
				snapshotstream.writeObject(p);
				patientCounter++;
				if(patientCounter==100) {
					System.out.print(".");
					patientCounter = 0;
				}
			}
			snapshotstream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}