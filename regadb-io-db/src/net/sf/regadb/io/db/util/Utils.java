package net.sf.regadb.io.db.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.EventNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientAttributeValueId;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.io.exportXML.ExportToXML;
import net.sf.regadb.io.importXML.ImportFromXML;
import net.sf.regadb.io.util.StandardObjects;
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
    
    private static String possibleNucleotides  = "ACGTMRWSYKVHDBN";
    
    public static Table readTable(String filename, String charset, char delim) {
        try {
        return Table.readTable(filename, Charset.defaultCharset().name(), delim);
        } catch(FileNotFoundException e) {
            ConsoleLogger.getInstance().logError("File '"+filename+"' not found.");
        } catch (UnsupportedEncodingException e) {
            ConsoleLogger.getInstance().logError("Unsupport charset '"+charset+"'");
        }
        return null;
    }
    
    public static Table readTable(String filename, String charset) {
        return readTable(filename, charset, ',');
    }
    
    public static Table readTable(String filename) {
        return readTable(filename, Charset.defaultCharset().name());
    }
    
    public static Table readTable(String filename, char delim) {
        return readTable(filename, Charset.defaultCharset().name(), delim);
    }
    
    public static int findColumn(Table t, String name) {
        int column = t.findColumn(name);
        
        if (column == -1) {
            ConsoleLogger.getInstance().logError("Could not find column " + name);
        }
        
        return column;
    }
    
    public static final String getMappingsilePath()
	{
		return "/mappings/";
	}
    
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
            
            //Be careful, the calendar starts with january = 0
            cal.set(year, month-1, day);
            
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
    
    public static Date parseEnglishAccessDate(String date) {
        if("".equals(date))
            return null;
        
        String dateNoTime = date.split(" ")[0];
        String [] dateTokens = dateNoTime.split("-");
       
        try {
            return Utils.createDate(dateTokens[0], dateTokens[1], dateTokens[2]);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Date parseBresciaSeqDate(String date) {
        if("".equals(date))
            return null;
        
        String [] dateTokens = date.split("/");
        
        try {
        return Utils.createDate(dateTokens[2], dateTokens[1], dateTokens[0]);
        } catch (Exception e) {
            return null;
        }
    }
    
     public static boolean checkColumnValueForExistance(String columnName, String value, int row, String patientID)
     {
    	 if(value != null)
    		 return true;
    	 else
    	 {
    		 if(ConsoleLogger.getInstance().isUninitializedValueEnabled())
    			 ConsoleLogger.getInstance().logWarning("Uninitialized value ("+value+") found at column "+columnName+" in row "+row+" for patient "+patientID+".");
    		 
    		 return false;
    	 }
     }
     
     public static boolean checkColumnValueForEmptiness(String columnName, String value, int row, String patientID)
     {
    	 if(!"".equals(value))
    		 return true;
    	 else
    	 {
    		 ConsoleLogger.getInstance().logWarning("No valid value ("+value+") for column "+columnName+" found in row "+row+" for patient "+patientID+"");
    		 
    		 return false;
    	 }
     }
     
     public static boolean checkSequence(String value, int row, String patientID)
     {
    	 if(!"".equals(value))
    		 return true;
    	 else
    	 {
    		 ConsoleLogger.getInstance().logWarning("No sequence found for patient "+patientID+" at row "+row+".");
    		 
    		 return false;
    	 }
     }
     
     public static boolean checkDrugValue(String value, int row, String patientID)
     {
    	 if(value.equals("1"))
    		 return true;
    	 else if(value.equals("1.0"))
    		 return true;
    	 else if(value.equals("0.5"))
    		 return true;
    	 else if(value.equals("1.5"))
    		 return true;
    	 else if(value.equals("0.0"))
    		 return true;
    	 //For Rome only: A bug in their system according to Iuri's knowledge
    	 else if(value.equals("-1"))
    		 return true;
    	 else if(value.equals("0"))
    		 return false;
    	 else
    	 {
    		 ConsoleLogger.getInstance().logWarning("No valid drug value ("+value+") found at row "+row+" for patient "+patientID+".");
    		 
    		 return false;
    	 }
     }
     
     public static boolean checkCDValue(String value, int row, String patientID) {
         try {
             double d = Double.parseDouble(value);
             if(d!=0.0) {
                 return true;
             } else {
                 ConsoleLogger.getInstance().logWarning(patientID, "No valid CD4 value found for patient "+patientID + " for value " + value);
                 return false;
             }
         } catch(NumberFormatException nfe) {
             ConsoleLogger.getInstance().logWarning(patientID, "No valid CD4 value found for patient "+patientID + " for value " + value);
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
     
     public static void exportPatientsXMLI(Map<Integer, Patient> patientMap, String fileName) {
         Map<String, Patient> patientMapS = new HashMap<String, Patient>();
         for (Integer patientId:patientMap.keySet()) {
             patientMapS.put(patientId+"", patientMap.get(patientId));
         }
         
         exportPatientsXML(patientMapS, fileName);
     }
     
     public static void exportPatientsXML(Map<String, Patient> patientMap, String fileName) 
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
             e.printStackTrace();
             ConsoleLogger.getInstance().logError("XML generation failed.");
         }
     }
     
     public static void exportNTXML(Map<String, ViralIsolate> ntMap, String fileName) 
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
     
     public static void exportNTXMLFromPatients(Map<String, Patient> patientMap, String fileName) 
     {
        try
        {
            ExportToXML l = new ExportToXML();
            Element root = new Element("viralIsolates");
            
            for (String patientSampleId:patientMap.keySet()) {
                Patient p = patientMap.get(patientSampleId);
                if(p.getViralIsolates().size()>0) {
                    Element viralIsolateE = new Element("viralIsolates-el");
                    root.addContent(viralIsolateE);
        
                    for(ViralIsolate vi : p.getViralIsolates()) {
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
         for(Attribute a : list)
         {
             if(a.getName().equals(attributeName))
             {
                 return a;
             }
         }
         
         return null;
     }
     
     public static Event selectEvent(String eventName, List<Event> list)
     {
         for(Event e : list)
         {
             if(e.getName().equals(eventName))
             {
                 return e;
             }
         }
         return null;
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
     
     public static List<Event> prepareRegaDBEvents()
     {
         //TODO retrieve event list with FileProvider
         List<Event> list = new ArrayList<Event>();
         
         Event e = new Event();
         e.setValueType(StandardObjects.getNominalValueType());
         e.setName("Aids defining illness");
         
         e.getEventNominalValues().add(new EventNominalValue(e,"Bacillary angiomatosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Candidiasis of bronchi, trachea, or lungs"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Candidiasis, esophageal"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Candidiasis, oropharyngeal (thrush)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Candidiasis, vulvovaginal; persistent, frequent, or poorly responsive to therapy"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cervical cancer, invasive"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cervical dysplasia (moderate or severe)/cervical carcinoma in situ"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Coccidioidomycosis, disseminated or extrapulmonary"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Constitutional symptoms, such as fever (38.5 C) or diarrhea lasting greater than 1 month"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cryptococcosis, extrapulmonary"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cryptosporidiosis, chronic intestinal (greater than 1 month's duration)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cytomegalovirus disease (other than liver, spleen, or nodes)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cytomegalovirus retinitis (with loss of vision)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Encephalopathy, HIV-related"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Hairy leukoplakia, oral"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Herpes simplex: chronic ulcer(s) (greater than 1 month's duration); or bronchitis, pneumonitis, or esophagitis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Herpes zoster (shingles), involving at least two distinct episodes or more than one dermatome"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Histoplasmosis, disseminated or extrapulmonary"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Idiopathic thrombocytopenic purpura"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Isosporiasis, chronic intestinal (greater than 1 month's duration)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Kaposi's sarcoma"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Listeriosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Lymphoma, Burkitt's (or equivalent term)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Lymphoma, immunoblastic (or equivalent term)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Lymphoma, primary, of brain"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Mycobacterium avium complex or M. kansasii, disseminated or extrapulmonary"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Mycobacterium tuberculosis, any site (pulmonary or extrapulmonary)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Mycobacterium, other species or unidentified species, disseminated or extrapulmonary"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Pelvic inflammatory disease, particularly if complicated by tubo-ovarian abscess"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Peripheral neuropathy"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Pneumocystis carinii pneumonia"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Pneumonia, recurrent"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Progressive multifocal leukoencephalopathy"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Salmonella septicemia, recurrent"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Toxoplasmosis of brain"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Wasting syndrome due to HIV"));
         e.getEventNominalValues().add(new EventNominalValue(e,"AIDS dementia complex"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cervical carcinoma"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Deep candidosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Disseminated CMV infection"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Retinic CMV infection"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cryptococcal infection"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cryptosporidiosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Chronic HSV infection"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Isosporiasis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Visceral leishmaniosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Progressive Multifocal leukoencephalopathy"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Non-Hodgkin lymphoma"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Non-Hodgkin lymphoma (B cells)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Non-Hodgkin lymphoma (cerebral)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Non-Hodgkin lymphoma (immunoblastic)"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Non-tubercular mycobacteriosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Cerebral toxoplasmosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Recurrent bacterial pneumonias"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Pneumocystis carinii pneumonia"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Kaposi sarcoma"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Disseminated salmonellosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Disseminated strongyloidiasis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Extra-pulmunar tuberculosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Pulmunar tuberculosis"));
         e.getEventNominalValues().add(new EventNominalValue(e,"Wasting sindrome"));

         list.add(e);
    
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
             if(possibleNucleotides.contains(Character.toUpperCase(c)+"")) {
                 toReturn.append(c);
             }
         }
         return toReturn.toString().toLowerCase();
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
                 ConsoleLogger.getInstance().logWarning("Generic Drug "+drug+" not found in RegaDB repository and no mapping was available.");
             }
             else
             {
            	 ConsoleLogger.getInstance().logInfo("Found drug "+drug.toUpperCase()+" in mapping file");
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
        	 if(!"".equals(value))
        		 ConsoleLogger.getInstance().logWarning("Unsupported attribute value (" + na.attribute.getName() + "): "+value);
         }
     }
     
     public static void handlePatientEventValue(NominalEvent ne, String value, Date startDate, Date endDate, Patient p) {
         EventNominalValue env = ne.nominalValueMap.get(value);
         
          if (env != null)
          {
              PatientEventValue v = p.createPatientEventValue(ne.event);
              v.setEventNominalValue(env);
              v.setStartDate(startDate);
              if(endDate != null)
            	  v.setEndDate(endDate);
          }
          else 
          {
              ConsoleLogger.getInstance().logWarning("Unsupported event value (" + ne.event.getName() + "): "+value);
          }
      }
     
     public static PatientEventValue handlePatientEventValue(NominalEvent ne, String value, Date startDate, Date endDate) {
         EventNominalValue env = ne.nominalValueMap.get(value);
         PatientEventValue v = null;
         
         if (env != null)
         {
             v = new PatientEventValue();
             v.setEvent(ne.event);
             v.setEventNominalValue(env);
             v.setStartDate(startDate);
             v.setEndDate(endDate);
         }
         else 
         {
             ConsoleLogger.getInstance().logWarning("Unsupported event value (" + ne.event.getName() + "): "+value);
         }
         
         return v;
     }
     
     public static TestNominalValue getNominalValue(TestType tt, String str){
         for(TestNominalValue tnv : tt.getTestNominalValues()){
             if(tnv.getTestType().equals(tt) && tnv.getValue().equals(str)){
                 return tnv;
             }
         }
         return null;
     }
     
     public static PatientAttributeValue createPatientAttributeValue(Attribute attribute, String value){
         PatientAttributeValue pav = createPatientAttributeValue(attribute);
         pav.setValue(value);
         return pav;
     }

     public static PatientAttributeValue createPatientAttributeValue(Attribute attribute, AttributeNominalValue anv){
         PatientAttributeValue pav = createPatientAttributeValue(attribute);
         pav.setAttributeNominalValue(anv);
         return pav;
     }

     public static PatientAttributeValue createPatientAttributeValue(Attribute attribute){
         PatientAttributeValue pav = new PatientAttributeValue();
         PatientAttributeValueId pavId = new PatientAttributeValueId();
         pavId.setAttribute(attribute);
         
         return pav;
     }
     
     public static PatientEventValue createPatientEventValue(Event event, String value){
         PatientEventValue pev = createPatientEventValue(event);
         pev.setValue(value);
         return pev;
     }

     public static PatientEventValue createPatientEventValue(Event event, EventNominalValue env){
         PatientEventValue pev = createPatientEventValue(event);
         pev.setEventNominalValue(env);
         return pev;
     }

     public static PatientEventValue createPatientEventValue(Event event){
         PatientEventValue pev = new PatientEventValue();
         pev.setEvent(event);
         
         return pev;
     }
     
     public static PatientAttributeValue getAttributeValue(Attribute attribute, Patient p){
         return getAttributeValue(attribute.getName(),p);
     }
     
     public static PatientAttributeValue getAttributeValue(String attributeName, Patient p){
         Set<PatientAttributeValue> pavs = p.getPatientAttributeValues();
         
         for(PatientAttributeValue i: pavs){
             if(i.getAttribute().getName().equals(attributeName))
                 return i;
         }
         return null;
     }
     
     public static void createPAV(NominalAttribute na, String nominalVal, Patient p) {
         AttributeNominalValue gnv = na.nominalValueMap.get(nominalVal);
         if (gnv != null) {
             PatientAttributeValue v = p.createPatientAttributeValue(na.attribute);
             v.setAttributeNominalValue(gnv);
         } else {
             ConsoleLogger.getInstance().logError("No mapping for nominal value: " + na.attribute.getName() + " -> " + nominalVal);
         }
     }
     
     public static boolean addCountryOrGeographicOrigin(NominalAttribute countryNA, NominalAttribute geographicNA, String val, Patient p) {
         AttributeNominalValue cnv = countryNA.nominalValueMap.get(val);
         AttributeNominalValue gnv = geographicNA.nominalValueMap.get(val);
         if(cnv!=null) {
             PatientAttributeValue v = p.createPatientAttributeValue(countryNA.attribute);
             v.setAttributeNominalValue(cnv);
             return true;
         }
         if(gnv!=null) {
             PatientAttributeValue v = p.createPatientAttributeValue(geographicNA.attribute);
             v.setAttributeNominalValue(gnv);
             return true;
         }
         ConsoleLogger.getInstance().logError("No mapping for nominal value: " + val);
         return false;
     }
}
