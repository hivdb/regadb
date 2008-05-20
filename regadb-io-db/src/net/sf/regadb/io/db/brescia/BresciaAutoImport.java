package net.sf.regadb.io.db.brescia;

import java.io.File;

import net.sf.regadb.io.db.util.msaccess.AccessToCsv;

public class BresciaAutoImport {
    public static void main(String [] args) {
        if(args.length!=4) {
            System.err.println("Usage: BresciaAutoImport outputDirectory database.mdb mappingBasePath sequenceExcelFile");
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
            
        AccessToCsv a2c = new AccessToCsv();
        a2c.createCsv(new File(args[1]), tempDir);
        
        ImportUNIBS imp = new  ImportUNIBS();
        imp.getData(tempDir, args[2], args[3]);
    }
}
