package net.sf.regadb.io.db.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.exportXML.ExportToXML;

public class Utils {
    public static Date createDate(String yearStr, String monthStr, String dayString) {
        Calendar cal = Calendar.getInstance();

        if (!yearStr.equals("")) {
            int year, month;

            year = Integer.parseInt(yearStr);
            if (year < 1900)
                return null;

            if (!monthStr.equals("")) {
                month = Integer.parseInt(monthStr);
            } else
                month = 0;

            int day = 1;
            if(dayString!=null)
                day = Integer.parseInt(dayString);
            cal.set(year, month, day);
            return new Date(cal.getTimeInMillis());
        } else {
            return null;
        }
    }
    
    public static Date parseAccessDate(String date) {
        if("".equals(date))
            return null;
        
        String dateNoTime = date.split(" ")[0];
        String [] dateTokens = dateNoTime.split("-");
       
        return Utils.createDate(dateTokens[2], dateTokens[1], dateTokens[0]);
    }
    
    public static int  findColumn(Table t, String name) 
    {
		int column = t.findInRow(0, name);
		
		if (column == -1)
		{
			ConsoleLogger.getInstance().logError("Could not find column " + name);
		}
		
		return column;
	}
    
     public static Table readTable(String filename) 
     {
    	try
    	{
    		return new Table(new BufferedInputStream(new FileInputStream(filename)), false);
    	}
    	catch(FileNotFoundException e)
    	{
    		ConsoleLogger.getInstance().logError("File '"+filename+"' not found.");
    	}
    	
    	return null;
    }
     
     public static boolean checkColumnValue(String value, int column, String patientID)
     {
    	 if(!"".equals(value))
    		 return true;
    	 else
    	 {
    		 ConsoleLogger.getInstance().logWarning(patientID, "No valid value found at column "+column+".");
    		 
    		 return false;
    	 }
     }
     
     public static boolean checkDrugValue(String value, int column, String patientID)
     {
    	 if(value.equals("1"))
    		 return true;
    	 else
    	 {
    		 ConsoleLogger.getInstance().logWarning(patientID, "No valid value found at column "+column+".");
    		 
    		 return false;
    	 }
     }
     
     public static boolean checkCDValue(String value, int column, String patientID)
     {
    	 if(!value.equals("0"))
    		 return true;
    	 else
    	 {
    		 ConsoleLogger.getInstance().logWarning(patientID, "No valid value found at column "+column+".");
    		 
    		 return false;
    	 }
     }
     
     public static Date convertDate(String germanDate)
     {
     	try
     	{
 	    	String[] split = germanDate.split(" ");
 			
 			String date = null;
 			
 			if(split != null &&
 			   split.length == 2)
 			{
 				date = split[0];
 			
 				if(date != null)
 				{
 					String[] parts = date.split("\\.");
 					
 					if(parts != null &&
 					   parts.length == 3)
 					{
 						String day = parts[0];
 						String month = parts[1];
 						String year = parts[2];
 						
 						return Utils.createDate(year, month, day);
 					}
 				}
 			}
     	}
     	catch(Exception e)
     	{
     		ConsoleLogger.getInstance().logWarning("Date conversion of string "+germanDate+" failed.");
     	}
 		
 		return null;
     }
     
     public void exportXML(HashMap<String, Patient> patientMap, String fileName) 
     {
     	try
     	{
 	        ExportToXML l = new ExportToXML();
 	        Element root = new Element("patients");
 	        
 	        for (String patientId:patientMap.keySet()) {
 	            Element patient = new Element("patients-el");
 	            root.addContent(patient);
 	
 	            Patient p = patientMap.get(patientId);
 	            l.writePatient(p, patient);            
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
             ConsoleLogger.getInstance().logError("XML generation failed.");
         }
     }
}
