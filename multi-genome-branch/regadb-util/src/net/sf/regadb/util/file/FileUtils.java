package net.sf.regadb.util.file;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

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
}
