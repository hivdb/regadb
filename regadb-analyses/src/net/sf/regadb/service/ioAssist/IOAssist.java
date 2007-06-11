package net.sf.regadb.service.ioAssist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.regadb.io.importXML.ImportFromXML;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IOAssist 
{
    public static void main(String [] args)
    {
        System.err.println("IOAssist started");
        long start = System.currentTimeMillis();
        ImportFromXML imp = new ImportFromXML();
        FileReader r;
        try 
        {
            r = new FileReader(new File(args[0]));
            File outFile = new File(args[1]);
            FileWriter out = new FileWriter(outFile);
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <viralIsolates>");
            imp.readViralIsolates(new InputSource(r), new IOAssistImportHandler(out));
            out.write("</viralIsolates>");
            out.close();
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        } 
        catch (SAXException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        long stop = System.currentTimeMillis();
        System.err.println("IOAssist finished in:" + (stop-start)/1000.0);
    }
}
