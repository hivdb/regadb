package net.sf.regadb.io.db.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.service.wts.FileProvider;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Utils {
    private static DateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
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
     
     public static boolean checkColumnValue(String value, int row, String patientID)
     {
    	 if(!"".equals(value))
    		 return true;
    	 else
    	 {
    		 ConsoleLogger.getInstance().logWarning(patientID, "No valid string value found at row "+row+".");
    		 
    		 return false;
    	 }
     }
     
     public static boolean checkDrugValue(String value, int row, String patientID)
     {
    	 if(value.equals("1"))
    		 return true;
    	 else
    	 {
    		 ConsoleLogger.getInstance().logWarning(patientID, "No valid drug value found at row "+row+".");
    		 
    		 return false;
    	 }
     }
     
     public static boolean checkCDValue(String value, int row, String patientID)
     {
    	 if(!value.equals("0"))
    		 return true;
    	 else
    	 {
    		 ConsoleLogger.getInstance().logWarning(patientID, "No valid cd value found at row "+row+".");
    		 
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
     
     public static void exportPatientsXML(HashMap<String, Patient> patientMap, String fileName) 
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
     
     public static void exportNTXML(HashMap<String, ViralIsolate> ntMap, String fileName) 
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
             ConsoleLogger.getInstance().logError("XML generation failed.");
         }
     }
     
     public static Date parseMysqlDate(String date) {
         Date d = null;
         try {
             String [] split = date.split(" ");
             if(split.length<2)
                 return null;
             d = mysqlDateFormat.parse(split[0]);
        } catch (ParseException e) {
            d = null;
        }
        return d;
     }
     
     public static Attribute selectAttribute(String attributeName, List<Attribute> list)
     {
         Attribute toReturn = null;
         
         for(Attribute a : list)
         {
             if(a.getName().equals(attributeName))
             {
                 toReturn = a;
             }
         }
         
         return toReturn;
     }
     
     public static List<Attribute> prepareRegaDBAttributes()
     {
         RegaDBSettings.getInstance().initProxySettings();
         
         FileProvider fp = new FileProvider();
         List<Attribute> list = null;
         File attributesFile = null;
         try {
             attributesFile = File.createTempFile("attributes", "xml");
         } catch (IOException e1) {
             e1.printStackTrace();
         }
         try 
         {
             fp.getFile("regadb-attributes", "attributes.xml", attributesFile);
         }
         catch (RemoteException e) 
         {
             e.printStackTrace();
         }
         final ImportFromXML imp = new ImportFromXML();
         try 
         {
             imp.loadDatabaseObjects(null);
             list = imp.readAttributes(new InputSource(new FileReader(attributesFile)), null);
         }
         catch(SAXException saxe)
         {
             saxe.printStackTrace();
         }
         catch(IOException ioex)
         {
             ioex.printStackTrace();
         }
         
         return list;
     }
     
     public static List<DrugCommercial> prepareRegaDrugCommercials()
     {
    	 try 
         {
	    	 ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
	    	 
	    	 return imDrug.getCommercialDrugs();
         }
	     catch(Exception e)
	     {
	    	 ConsoleLogger.getInstance().logError("Cannot retrieve drug list.");
	     }
	     
	     return null;
     } 
     
     public static List<DrugGeneric> prepareRegaDrugGenerics()
     {
    	 try 
         {
	    	 ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
	    	 
	    	 return imDrug.getGenericDrugs();
         }
	     catch(Exception e)
	     {
	    	 ConsoleLogger.getInstance().logError("Cannot retrieve drug list.");
	     }
	     
	     return null;
     } 
     
     public static String[] convertKeysToStringArray(Set<String> keys)
     {
    	 String[] array = new String[keys.size()];
    	
    	 int count = 0;
    	 
    	 for(Iterator it = keys.iterator(); it.hasNext();) 
    	 {
    		 array[count] = ((String)it.next()).toLowerCase();
    		 
    		 count++;
    	 }
    	 
    	 return array;
     }
     
     public static String[] convertValuesToStringArray(Collection<String> values)
     {
    	 String[] array = new String[values.size()];
    	
    	 int count = 0;
    	 
    	 for(Iterator it = values.iterator(); it.hasNext();) 
    	 {
    		 array[count] = (String)it.next();
    		 
    		 count++;
    	 }
    	 
    	 return array;
     }
     
     public static String clearNucleotides(String nucleotides) 
     {
         StringBuffer toReturn = new StringBuffer();
         for(char c : nucleotides.toCharArray()) 
         {
             if(Character.isLetter(c)) {
                 toReturn.append(c);
             }
         }
         return toReturn.toString();
     }
     
     //TODO: Use more sophisticated algorithms to find a string match...
     public static double findCountryMatch(String value, String compareWithValue)
     {
    	 double match = 0.000d;
    	 
    	 double inc = 0.000d;
    		 
    	 inc = (100d / compareWithValue.length()) / 100d;
    	 
    	 value = value.toLowerCase();
    	 compareWithValue = compareWithValue.toLowerCase();
    	 
    	 for(int i = 0; i < value.length(); i++)
    	 {
    		 if(match >= 0.70)
    		 {
    			 return match;
    		 }
    		 
    		 if(value.charAt(i) == compareWithValue.charAt(i))
    		 {
    			 match += inc;
    		 }
    		 else
    		 {
    			 return match;
    		 }
    	 }
    	 
    	 return -1;
     }
     
     public static String checkDrugsWithRepos(String drug, List<DrugGeneric> regaDrugGenerics, Mappings mappings)
     {
    	 boolean foundDrug = false;
         
         for(int j = 0; j < regaDrugGenerics.size(); j++)
     	{
         	DrugGeneric genDrug = regaDrugGenerics.get(j);
         	
         	if(genDrug.getGenericId().equals(drug.toUpperCase()))
         	{
         		ConsoleLogger.getInstance().logInfo("Found drug "+drug.toUpperCase()+" in Rega list");
         		
         		foundDrug = true;
         		
         		break;
         	}
     	}
         
         if(!foundDrug) {
             String mapping = mappings.getMapping("generic_drugs.mapping", drug);
             if(mapping==null) {
                 ConsoleLogger.getInstance().logWarning("Generic Drug "+drug+" not found in RegaDB repository and no mapping was avaialable.");
             }
             return mapping;
         }
         else {
             return drug;
         }
     }
     
     public static HashMap<String, String> translationFileToHashMap(Table t) {
    	 HashMap<String, String> values = new HashMap<String, String>();
    	 
    	 for(int i = 1; i < t.numRows(); i++)
     	 {
             String val1 = t.valueAt(0, i);
             String val2 = t.valueAt(1, i);
             
             if(!"".equals(val1) && !"".equals(val2))
             {
            	 values.put(val1, val2);
             }
             else
             {
            	 ConsoleLogger.getInstance().logWarning("Values in row "+i+" not present.");
             }
     	 }
    	 
    	 return values;
     }
     
     public static void handlePatientAttributeValue(NominalAttribute na, String value, Patient p) {
        AttributeNominalValue anv = na.nominalValueMap.get(value);
        
         if (anv != null)
         {
             PatientAttributeValue v = p.createPatientAttributeValue(na.attribute);
             v.setAttributeNominalValue(anv);
         }
         else 
         {
             ConsoleLogger.getInstance().logWarning("Unsupported attribute value (" + na.attribute.getName() + "): "+value);
         }
     }
}
