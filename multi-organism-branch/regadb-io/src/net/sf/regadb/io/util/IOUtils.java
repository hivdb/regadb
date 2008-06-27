package net.sf.regadb.io.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.exportXML.ExportToXML;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class IOUtils {
    public static void exportPatientsXMLI(Map<Integer, Patient> patientMap, String fileName, ILogger logger) {
        Map<String, Patient> patientMapS = new HashMap<String, Patient>();
        for (Integer patientId:patientMap.keySet()) {
            patientMapS.put(patientId+"", patientMap.get(patientId));
        }
        
        exportPatientsXML(patientMapS, fileName, logger);
    }
    
    public static void exportPatientsXML(Map<String, Patient> patientMap, String fileName, ILogger logger) 
    {
    	try {
    		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

    		ExportToXML l = new ExportToXML();
    
           FileWriter out = new FileWriter(fileName);
           out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <patients>");
           for (String patientId:patientMap.keySet()) {
           	Element patient = new Element("patients-el");
	            Patient p = patientMap.get(patientId);
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
    
    public static void exportNTXML(Map<String, ViralIsolate> ntMap, String fileName, ILogger logger) 
    {
    	try
    	{
	        ExportToXML l = new ExportToXML();
	        Element root = new Element("viralIsolates");
	        
	        for (String seqFinalSampleId:ntMap.keySet()) 
	        {
	            Element viralIsolateE = new Element("viralIsolates-el");
	            root.addContent(viralIsolateE);
	
	            ViralIsolate vi = ntMap.get(seqFinalSampleId);
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
    
    public static void exportNTXMLFromPatients(Map<String, Patient> patientMap, String fileName, ILogger logger) 
    {
       try
       {
           ExportToXML l = new ExportToXML();
           Element root = new Element("viralIsolates");
           
           for (String patientSampleId:patientMap.keySet()) {
               Patient p = patientMap.get(patientSampleId);
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
