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
        if(args.length<3) {
            System.err.println("IOAssist usage: IOAssist inputfile outputfile --usage(type,subtype,align,resistance) [proxyurl:proxyport]");
            System.exit(0);
        }
        System.err.println("IOAssist started");
        if(args.length==4 && args[3].contains(":")) {
            String proxyHost = args[3].split(":")[0];
            String proxyPort = args[3].split(":")[1];
            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", proxyPort);
        }
        long start = System.currentTimeMillis();
        ImportFromXML imp = new ImportFromXML();
        FileReader r;
        try 
        {
            r = new FileReader(new File(args[0]));
            File outFile = new File(args[1]);
            FileWriter out = new FileWriter(outFile);
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <viralIsolates>");
            IOAssistMode mode = null;
            if(args[2].equals("--type")) {
                mode = IOAssistMode.Type;
            } else if(args[2].equals("--subtype")) {
                mode = IOAssistMode.SubType;
            } else if(args[2].equals("--resistance")) {
                mode = IOAssistMode.Resistance;
            } else if(args[2].equals("--align")) {
                mode = IOAssistMode.Alignment;
            } else {
                System.err.println("Wrong usage: "+ args[2]);
                System.exit(0);
            }
            imp.readViralIsolates(new InputSource(r), new IOAssistImportHandler(out, mode));
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
