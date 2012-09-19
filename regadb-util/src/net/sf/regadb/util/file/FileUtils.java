package net.sf.regadb.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    
    public static String getHumanReadableFileSize(long fs){
        DecimalFormat df = new DecimalFormat("########.00");
        if (fs > 1024 * 1024 * 1024)
            return (df.format((float) fs / (1024 * 1024 * 1024))) + " GB";
        if (fs > 1024 * 1024)
            return (df.format((float) fs / (1024 * 1024))) + " MB";
        if (fs > 1024) return (fs / 1024) + " KB";
 
        return fs + " bytes";
    }

    public static String getHumanReadableFileSize(File f){
        return getHumanReadableFileSize(f.length());
    }
    
    public static File createTempDir(String prefix, String name) throws IOException {
        File tempFile = File.createTempFile(prefix, name);
        if (!tempFile.delete())
            throw new IOException();
        if (!tempFile.mkdir())
            throw new IOException();
        return tempFile;
    }
    
    /**
     * zip a bunch of files
     * @param zipFile path where the zip will be created
     * @param inputFiles map of files to be created where the key is the name of the zip entry and value is the file to be zipped 
     * @throws IOException 
     */
    public static void createZipFile(File zipFile, Map<String, File> inputFiles) throws IOException{
    	// Create a buffer for reading the files
	    byte[] buf = new byte[1024];
	    
        // Create the ZIP file
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
    
        // Compress the files
        for (Map.Entry<String, File> inputFile : inputFiles.entrySet()) {
            FileInputStream in = new FileInputStream(inputFile.getValue());
    
            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(inputFile.getKey()));
    
            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
    
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        out.close();
    }
}
