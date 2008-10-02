package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ToSnapshot<T> extends QueryOutput<T> {
	
	public ToSnapshot(File file){
		super(file);
	}
	
	@Override
	public void generateOutput(Query<T> query) {
		try 
		{
			ObjectOutputStream snapshotstream = new ObjectOutputStream(new FileOutputStream(file));
			for(T t : query.getOutputList()){
				snapshotstream.writeObject(t);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	

}
