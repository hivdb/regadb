package net.sf.regadb.util.zip;

import java.io.File;
import java.io.IOException;

public class Zip {
    public static void unzip(File toUnzip, File placeToUnzip) throws IOException {
        System.err.println("Extracting " + toUnzip.getAbsolutePath() + " to " + placeToUnzip.getAbsolutePath());
        java.util.jar.JarFile jar = new java.util.jar.JarFile(toUnzip);
        java.util.Enumeration enumE = jar.entries();
        java.util.jar.JarEntry file;
        java.io.File f;
        java.io.InputStream is;
        java.io.FileOutputStream fos;
        while (enumE.hasMoreElements()) {
            file = (java.util.jar.JarEntry) enumE.nextElement();
            f = new java.io.File(placeToUnzip.getAbsolutePath() + java.io.File.separator + file.getName());
            if (file.isDirectory()) { // if its a directory, create it
                f.mkdir();
                continue;
            }
            is = jar.getInputStream(file); // get the input stream
            fos = new java.io.FileOutputStream(f);
            while (is.available() > 0) {  // write contents of 'is' to 'fos'
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
        System.err.println("Finished extracting " + toUnzip.getAbsolutePath());
    }
}
