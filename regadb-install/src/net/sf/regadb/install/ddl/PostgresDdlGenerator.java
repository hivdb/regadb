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
		config.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		config.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
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
                    str = processString(str);
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
            toWrite = toWrite.replaceAll("varchar\\(255\\)", "text");
			FileUtils.writeByteArrayToFile(new File(fileName), toWrite.getBytes());
		}
		catch (IOException e) 
        {
			e.printStackTrace();
		}
	}

	private String processString(String str) 
	{
	    if(str.startsWith("create table"))
        {
	        return processCreateTable(str);
        }
        else if(str.startsWith("alter table"))
        {
            return processAlterTable(str);
        }
        
        return str;
    }
    
    private String processCreateTable(String str)
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
                lineBuffer.insert(endOfPrimKeyName, " integer default nextval('" + tableName + "_" + tableName + "_ii_seq')");
            }
        }
        
        return lineBuffer != null ? lineBuffer.toString() : strBackup;
    }
    
    private String processAlterTable(String str)
    {
        String strBackup = str;

        int indexOfForeignKey = strBackup.indexOf("foreign key");
        if(indexOfForeignKey!=-1)
        {
            int firstBracket = strBackup.indexOf('(', indexOfForeignKey);
            String key = strBackup.substring(firstBracket+1, strBackup.indexOf(')', firstBracket));
            strBackup += "("+key+") ON UPDATE CASCADE";
            
            String alterTablePublic = "alter table public.";
            String alterTable = "alter table ";
            String tableName = null;
            if(strBackup.indexOf(alterTablePublic)!=-1)
            {
                alterTable = alterTablePublic;
            }
            
            if(strBackup.indexOf("add constraint ")!=-1)
            {
	            tableName = strBackup.substring(strBackup.indexOf(alterTable)+alterTable.length(),strBackup.indexOf(" ", strBackup.indexOf(alterTable)+alterTable.length()));
	            int referenceIndex = strBackup.indexOf("references")+"references".length();
	            String referencingTable = strBackup.substring(referenceIndex, strBackup.indexOf('(', referenceIndex));
	            referencingTable = referencingTable.trim();
	            referencingTable = referencingTable.replaceAll("public.", "");
	            String fk_name = "\"FK_"+tableName+"_"+referencingTable+'\"';
	            StringBuffer strBuffer = new StringBuffer(strBackup);
	            int indexOfAddConstraint = strBuffer.indexOf("add constraint ")+"add constraint ".length();
	            strBuffer.delete(indexOfAddConstraint, strBackup.indexOf(" ", indexOfAddConstraint));
	            strBuffer.insert(indexOfAddConstraint, fk_name);
	            strBackup = strBuffer.toString();
            }
        }
        
        return strBackup;
    }
}
