package net.sf.regadb.io.db.util.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ProcessFile {
    public void process(File file, ILineHandler lineHandler) {
        try {
            int counter = 0;
            InputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            counter++;
            while(line!=null) {
                lineHandler.handleLine(line, counter);
                line = br.readLine();
                counter++;
            }
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
