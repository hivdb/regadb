package net.sf.regadb.io.db.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.regadb.util.string.StringTokenizer;

import org.apache.commons.io.FileUtils;

public class CreateCsvFiles 
{
    public static void main(String [] args)
    {
        generateCsvFiles(args[0]);
    }
    
    public static void generateCsvFile(File txtFile)
    {
        String thisLine;
        StringBuffer outputFile = new StringBuffer();
        String token;
        try
        {
            InputStream is = new FileInputStream(txtFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((thisLine = br.readLine()) != null)
            {
                StringTokenizer st = new StringTokenizer(thisLine, ";");
                st.setReturnEmptyTokens(true);
                while(st.hasMoreTokens())
                {
                    token = st.nextToken();
                    if(token.startsWith("\""))
                    {
                        outputFile.append(token + ",");
                    }
                    else
                    {
                        outputFile.append("\"" + token + "\"" + ",");
                    }
                }
                outputFile.deleteCharAt(outputFile.length()-1);
                outputFile.append('\n');
            }
            File fileToWriteTo = new File(txtFile.getAbsolutePath().replaceAll(".txt", ".csv"));
            FileUtils.writeStringToFile(fileToWriteTo, outputFile.toString(), null);
            System.err.println("Processed file: " + txtFile.getAbsolutePath());
            outputFile.delete(0, outputFile.length());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    
    public static void generateCsvFiles(String workingDir)
    {
        File work = new File(workingDir);

        for(File txtFile : work.listFiles())
        {
            if(txtFile.getAbsolutePath().endsWith(".txt"))
            {
                generateCsvFile(txtFile);
            }
        }
    }
}
