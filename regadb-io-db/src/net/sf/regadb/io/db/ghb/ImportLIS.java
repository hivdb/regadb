package net.sf.regadb.io.db.ghb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import net.sf.regadb.csv.Table;

public class ImportLIS 
{
    private Table lisTable;
    
    public ImportLIS()
    {
        File original = new File("/home/plibin0/import/ghb/GHB20070515.txt");
        createCSV(original);
    }
    
    public File createCSV(File original) {
            String line;
            int counter = 0;
            try {
                InputStream is = new FileInputStream(original);
                
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    if(!line.startsWith("EADnr\tEMDnr") && !line.trim().equals("")) {
                    StringTokenizer st = new StringTokenizer(line, "\t");
                    while(st.hasMoreTokens()) {
                        System.err.println(st.nextToken());
                    }
                    counter++;
                    if(counter>2)
                        break;
                    }
                }
                
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
    }

    public int findColumn(Table t, String name) {
        int column = t.findInRow(0, name);
        
        if (column == -1)
            throw new RuntimeException("Could not find column " + name);

        return column;
    }
    
     private Table readTable(String filename) throws FileNotFoundException {
        System.err.println(filename);
        return new Table(new BufferedInputStream(new FileInputStream(filename)), false);
    }
     
     public static void main(String [] args)
     {
            ImportLIS importghb = new ImportLIS();
     }
}
