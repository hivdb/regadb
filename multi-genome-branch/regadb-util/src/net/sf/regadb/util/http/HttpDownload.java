package net.sf.regadb.util.http;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class HttpDownload {
    public static void download(String urlAdress, String localFileName) {
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        try {
            URL url = new URL(urlAdress);
            out = new BufferedOutputStream(new FileOutputStream(localFileName));
            conn = url.openConnection();
            in = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int numRead;
            long numWritten = 0;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
                numWritten += numRead;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ioe) {
            }
        }
    }
}
