package net.sf.regadb.analysis.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FastaMerge 
{
    public static void main(String [] args)
    {
        if(args.length!=3)
        {
            System.err.println("Please provide following parameters:");
            System.err.println("\t Directory containing the fasta files");
            System.err.println("\t Extension of the fasta files to merge");
            System.err.println("\t Path of the result file");
            System.exit(0);
        }
        else
        {
            File srcDir = new File(args[0]);
            if(!srcDir.exists())
            {
                System.err.println("The source directory of the fasta files does not exist");
                System.exit(0);
            }
            if(!srcDir.isDirectory())
            {
                System.err.println("The source directory of the fasta files is not a directory");
                System.exit(0);
            }
            File resultFile = new File(args[2]);
            if(resultFile.exists())
            {
                System.err.println("The provided result file already exists, please provide a non-existing file");
                System.exit(0);
            }
            if(resultFile.isDirectory())
            {
                System.err.println("The result file cannot be a directory");
                System.exit(0);
            }
            
            FastaMerge fm = new FastaMerge();
            fm.mergeFastas(srcDir, args[1], resultFile);
        }
    }
    
    public void mergeFastas(File srcDir, String extension, File resultFile)
    {
        FileWriter fw = null;
        try 
        {
             fw = new FileWriter(resultFile);
        } 
        catch (IOException e1) 
        {
            e1.printStackTrace();
        }
        String line;
        for(File f : srcDir.listFiles())
        {
            if(!f.getAbsolutePath().equals(resultFile.getAbsolutePath()))
            {
                if(f.getAbsolutePath().endsWith("."+extension))
                {
                    System.err.println("File " + f.getAbsolutePath() + " is put in the result file");
                    try 
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                        try 
                        {
                            while((line = br.readLine())!=null)
                            {
                                fw.write(line + '\n');
                            }
                        } 
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    } 
                    catch (FileNotFoundException e) 
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    System.err.println("File " + f.getAbsolutePath() + " is ignored");
                }
            }
        }
    }
}
