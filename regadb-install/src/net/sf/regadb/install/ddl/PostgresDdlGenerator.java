package net.sf.regadb.install.ddl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.regadb.util.reflection.PackageUtils;

import org.apache.commons.io.FileUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class PostgresDdlGenerator 
{
	public static void main(String [] args) 
	{
		String fileName = PackageUtils.getDirectoryPath("net.sf.regadb.install.ddl.schema", "regadb-install");
		PostgresDdlGenerator gen = new PostgresDdlGenerator();
		gen.createDdl(fileName+File.separatorChar+"postgresSchema.sql");
	}
	
	public void createDdl(String fileName)
	{
		Configuration config = new Configuration().configure();
		SchemaExport export = new SchemaExport(config);
		export.setOutputFile(fileName);
		export.create(true, false); 
		
		byte[] array = null;
		
		try 
		{
			array = FileUtils.readFileToByteArray(new File(fileName));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		StringBuffer buffer = new StringBuffer(new String(array));
		
	    int indexOfCreate = buffer.indexOf("create");
	    int indexOfCreateSequence = buffer.indexOf("create sequence");
	    
	    String toWrite = buffer.substring(indexOfCreateSequence).concat(buffer.substring(indexOfCreate, indexOfCreateSequence)).replaceAll("int4", "integer");
	    
	    try {
			FileUtils.writeByteArrayToFile(new File(fileName), toWrite.getBytes());
		}
	    catch (IOException e) {
			e.printStackTrace();
		}
	    
		try {
			array = FileUtils.readFileToByteArray(new File(fileName));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
		        BufferedReader in = new BufferedReader(new FileReader(fileName));
		        
		        String str;
		        
		        buffer = new StringBuffer();
		        
		        while ((str = in.readLine()) != null) {
		        	buffer.append(processString(str)+";\n");
		        }
		        
		        in.close();
		}
		catch (IOException e) {
		    	
		}
		    
		try {
			FileUtils.writeByteArrayToFile(new File(fileName), buffer.toString().getBytes());
		}
		catch (IOException e) {
				e.printStackTrace();
		}
	}

	private String processString(String str) 
	{
		String strBackup = str;
		
		int indexOfPrimaryKey = strBackup.indexOf("primary key");
		
		StringBuffer lineBuffer = null;
		
		if(indexOfPrimaryKey!=-1)
		{
			String primaryKeyArgs = strBackup.substring(indexOfPrimaryKey, strBackup.indexOf(')',indexOfPrimaryKey));
				
			if(!primaryKeyArgs.contains(",") && primaryKeyArgs.contains("_ii"))
			{
				String tableName = str.substring(str.indexOf("public.") + "public.".length(), str.indexOf(' ', str.indexOf("public.")));
				
				lineBuffer = new StringBuffer(str);
				int endOfPrimKeyName = str.indexOf(' ', str.indexOf('('));
				int endOfPrimKeyArgs = str.indexOf(',', endOfPrimKeyName);
				
				lineBuffer.delete(endOfPrimKeyName, endOfPrimKeyArgs);
				lineBuffer.insert(endOfPrimKeyName, " integer default nextval(\'" + tableName + "_" + tableName + "_ii_seq')");
			}
		}
		
		return lineBuffer != null ? lineBuffer.toString() : strBackup;
	}
}
