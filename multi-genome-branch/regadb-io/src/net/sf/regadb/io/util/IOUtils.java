package net.sf.regadb.io.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.exportXML.ExportToXML;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class IOUtils {
    public static void exportPatientsXML(Collection<Patient> patients, String fileName, ILogger logger)
    {
    	try {
    		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

    		ExportToXML l = new ExportToXML();
    
           FileWriter out = new FileWriter(fileName);
           out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <patients>");
           for (Patient p : patients) {
           	Element patient = new Element("patients-el");
	            l.writePatient(p, patient);
	            out.write(outputter.outputString(patient)+'\n');
           }
           out.write("</patients>");
           out.close();
    	} catch (IOException ioe) {
           ioe.printStackTrace();
           logger.logError("XML generation failed.");
    	}
    }
    
    public static void exportNTXML(Collection<ViralIsolate> viralIsolates, String fileName, ILogger logger) 
    {
    	try
    	{
	        ExportToXML l = new ExportToXML();
	        Element root = new Element("viralIsolates");
	        
	        for (ViralIsolate vi : viralIsolates) 
	        {
	            Element viralIsolateE = new Element("viralIsolates-el");
	            root.addContent(viralIsolateE);
	            l.writeViralIsolate(vi, viralIsolateE);            
	        }
	        
	        Document n = new Document(root);
	        XMLOutputter outputter = new XMLOutputter();
	        outputter.setFormat(Format.getPrettyFormat());
	
	        java.io.FileWriter writer;
	        writer = new java.io.FileWriter(fileName);
	        outputter.output(n, writer);
	        writer.flush();
	        writer.close();
    	}
        catch (IOException e) 
        {
            logger.logError("XML generation failed.");
        }
    }
    
    public static void exportNTXMLFromPatients(Collection<Patient> patients, String fileName, ILogger logger)
    {
       try
       {
           ExportToXML l = new ExportToXML();
           Element root = new Element("viralIsolates");
           
           for (Patient p : patients) {
               if(p.getViralIsolates().size()>0) {
                   for(ViralIsolate vi : p.getViralIsolates()) {
                       Element viralIsolateE = new Element("viralIsolates-el");
                       root.addContent(viralIsolateE);
                       
                       l.writeViralIsolate(vi, viralIsolateE);
                   }
               }
           }
           
           Document n = new Document(root);
           XMLOutputter outputter = new XMLOutputter();
           outputter.setFormat(Format.getPrettyFormat());
   
           java.io.FileWriter writer;
           writer = new java.io.FileWriter(fileName);
           outputter.output(n, writer);
           writer.flush();
           writer.close();
       }
        catch (IOException e) 
        {
            logger.logError("XML generation failed.");
        }
    }
}
