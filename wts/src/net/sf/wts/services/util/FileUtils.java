package net.sf.wts.services.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils 
{
    public static byte[] getByteArrayFromFile(File file)
    {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        
        try {
            FileInputStream fin = new FileInputStream(file);
            copy(fin, bout);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        byte[] out = bout.toByteArray();
        
        try {
            bout.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return out;
    }
    
    private static void copy(InputStream in, OutputStream out) throws IOException {
        synchronized (in) {
            synchronized (out) {
                byte[] buffer = new byte[256];
                while (true) {
                    int bytesRead = in.read(buffer);
                    
                    if (bytesRead == -1) {
                        break;
                    }
                    
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }
}
