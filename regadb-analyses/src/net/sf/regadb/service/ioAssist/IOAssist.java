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
        if(args.length<2) {
            System.err.println("IOAssist usage: IOAssist inputfile outputfile [proxyurl:proxyport] [--wtsUrl url]");
            System.exit(0);
        }
        System.err.println("IOAssist started");
        if(args.length==3 && args[2].contains(":")) {
            String proxyHost = args[2].split(":")[0];
            String proxyPort = args[2].split(":")[1];
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", proxyPort);
        }
        String wtsUrl = null;
        for(int i = 0; i<args.length; i++) {
            if(args[i].equals("--wtsUrl")) {
                wtsUrl = args[i+1];
                break;
            }
        }

        run(new File(args[0]), new File(args[1]), wtsUrl);
    }
    
    public static void run(File inputFile, File outputFile, String wtsUrl) {
        long start = System.currentTimeMillis();
        ImportFromXML imp = new ImportFromXML();
        FileReader r;
        try 
        {
            r = new FileReader(inputFile);
            FileWriter out = new FileWriter(outputFile);
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <viralIsolates>");
            imp.readViralIsolates(new InputSource(r), new IOAssistImportHandler(out, wtsUrl));
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
