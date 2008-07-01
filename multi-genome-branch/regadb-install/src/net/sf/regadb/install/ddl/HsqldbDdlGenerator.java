package net.sf.regadb.install.ddl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import net.sf.regadb.util.reflection.PackageUtils;

import org.apache.commons.io.FileUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HsqldbDdlGenerator 
{
	public static void main(String [] args) 
	{
		String fileName = PackageUtils.getDirectoryPath("net.sf.regadb.install.ddl.schema", "regadb-install");
		HsqldbDdlGenerator gen = new HsqldbDdlGenerator();
		gen.createDdl(fileName+File.separatorChar+"hsqldbSchema.sql");
	}
	
	public void createDdl(String fileName)
	{
		Configuration config = new Configuration().configure();
		config.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		config.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
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
	    
	    String toWrite = buffer.substring(indexOfCreateSequence).concat(buffer.substring(indexOfCreate, indexOfCreateSequence)).replaceAll(" int4", " integer ");
        
	    try 
        {
			FileUtils.writeByteArrayToFile(new File(fileName), toWrite.getBytes());
		}
	    catch (IOException e) 
        {
			e.printStackTrace();
		}
	    
		try 
        {
			array = FileUtils.readFileToByteArray(new File(fileName));
		}
		catch (IOException e) 
        {
			e.printStackTrace();
		}
		
		try 
        {
		        BufferedReader in = new BufferedReader(new FileReader(fileName));
		        
		        String str;
		        
		        buffer = new StringBuffer();
		        
		        while ((str = in.readLine()) != null) 
                {
                    buffer.append(str+";\n");
		        }
		        
		        in.close();
		}
		catch (IOException e) 
        {
		    e.printStackTrace();
		}
		    
		try 
        {
            toWrite = buffer.toString();
            toWrite = toWrite.replaceAll("varchar\\(255\\)", "longvarchar");
			FileUtils.writeByteArrayToFile(new File(fileName), toWrite.getBytes());
		}
		catch (IOException e) 
        {
			e.printStackTrace();
		}
	}
}
