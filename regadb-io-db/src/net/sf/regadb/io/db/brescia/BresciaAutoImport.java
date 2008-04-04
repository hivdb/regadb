package net.sf.regadb.io.db.brescia;

import java.io.File;

import net.sf.regadb.io.db.util.msaccess.AccessToCsv;

public class BresciaAutoImport {
    public static void main(String [] args) {
        if(args.length!=2) {
            System.err.println("Usage: BresciaAutoImport database.mdb mappingBasePath");
            System.exit(0);
        }

        System.setProperty("http.proxyHost", "192.168.5.4");
        System.setProperty("http.proxyPort", "8080");

        File tempDir = null;
        tempDir = new File("C:\\temp\\");
        tempDir.mkdir();
            
        AccessToCsv a2c = new AccessToCsv();
        a2c.createCsv(new File(args[0]), tempDir);
        
        ImportUNIBS imp = new  ImportUNIBS();
        imp.getData(tempDir, args[1]);
    }
}
