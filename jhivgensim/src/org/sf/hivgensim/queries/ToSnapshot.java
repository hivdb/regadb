package org.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ToSnapshot<T> extends QueryOutput<T> {
	
	public ToSnapshot(QueryImpl<T> query, File file){
		super(query,file);
	}
	
	@Override
	public void generateOutput() {
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
