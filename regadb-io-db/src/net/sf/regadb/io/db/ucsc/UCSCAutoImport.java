package net.sf.regadb.io.db.ucsc;

import java.io.File;

public class UCSCAutoImport {
    public static void main(String [] args) {
        if(args.length!=2) {
            System.err.println("Usage: UCSCAutoImport database.mdb mappingBasePath");
            System.exit(0);
        }

        File tempDir = null;
        tempDir = new File("C:\\temp\\");
        tempDir.mkdir();
        
        ImportUcsc imp = new  ImportUcsc();
        imp.getData(tempDir, args[0], args[1]);
    }
}
