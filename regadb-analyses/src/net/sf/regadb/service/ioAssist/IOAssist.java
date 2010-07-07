package net.sf.regadb.service.ioAssist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IOAssist 
{
    public static void main(String [] args)
    {
    	Arguments as = new Arguments();
    	PositionalArgument in = as.addPositionalArgument("inputfile", true);
    	PositionalArgument out = as.addPositionalArgument("outputfile", true);
    	ValueArgument conf = as.addValueArgument("conf-dir", "configuration directory", false);
    	
    	if(!as.handle(args))
    		return;

        if(conf.isSet())
        	RegaDBSettings.createInstance(conf.getValue());
        else
        	RegaDBSettings.createInstance();

        System.err.println("IOAssist started");
        run(new File(in.getValue()), new File(out.getValue()), null);
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
