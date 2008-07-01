package net.sf.regadb.io.db.ucsc;

import java.io.File;

public class UCSCAutoImport {
    public static void main(String [] args) 
    {
        if(args.length!=3) 
        {
            System.err.println("Usage: UCSCAutoImport outputDirectory database.mdb mappingBasePath");
            System.exit(0);
        }

        File tempDir = new File(args[0]);
        
        if(tempDir.exists())
        {
        	System.out.println("Removing all files from directory...");
        	
        	File[] files = tempDir.listFiles();
        	
        	if(files.length != 0)
        	{
        		for(int i = 0; i < files.length; i++)
        		{
        			File file = files[i];
        			
        			if(file.isFile())
        			{
        				System.out.println("Deleting file " +file.getName());
        				file.delete();
        			}
        		}
        	}
        }
        else
        {
	        System.out.println("Creating directory...");
	        tempDir.mkdir();
        }
        
        ImportUcsc imp = new  ImportUcsc();
        imp.getData(tempDir, args[1], args[2]);
    }
}
