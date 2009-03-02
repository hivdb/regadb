package net.sf.hivgensim.queries.input;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.regadb.db.Patient;

public class FromSnapshot extends QueryInput {
	
	private File file;	
	
	public FromSnapshot(File file){
		this.file = file;	
	}
	
	@Override
	protected void populateOutputList() {
		try
		{
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream in = new ObjectInputStream(fis);
			Patient p;
			while((p = (Patient)in.readObject()) != null){				
				outputList.add(p);
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

}
