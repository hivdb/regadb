package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.sf.regadb.db.*;
import net.sf.regadb.io.persistence.ExportToPersistentObjects;

public class ToSnapshot extends QueryOutput<Patient> {

	public ToSnapshot(File file){
		super(file);
	}

	@Override
	public void generateOutput(Query<Patient> query) {
		try 
		{
			ExportToPersistentObjects export = new ExportToPersistentObjects();
			ObjectOutputStream snapshotstream = new ObjectOutputStream(new FileOutputStream(file));
			for(Patient p : query.getOutputList()){
				export.initialize(p);
				snapshotstream.writeObject(p);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}