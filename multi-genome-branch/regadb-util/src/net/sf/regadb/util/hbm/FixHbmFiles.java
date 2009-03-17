package net.sf.regadb.util.hbm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.regadb.util.pair.Pair;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class FixHbmFiles 
{
	public static class FilterDef{
		public String name;
		public List<Pair<String,String>> params = new ArrayList<Pair<String,String>>();
		
		public FilterDef(String name){
			this.name = name;
		}
		
		public void addParam(String name, String type){
			params.add(new Pair<String,String>(name,type));
		}
	}
	public static class FilterInst{
		public FilterDef filter;
		
		public String setName = null;
		public String condition;
		
		public FilterInst(FilterDef filter, String condition){
			this.filter = filter;
			this.condition = condition;
		}
		public FilterInst(FilterDef filter, String condition, String setName){
			this.filter = filter;
			this.condition = condition;
			this.setName = setName;
		}
	}
	
	@SuppressWarnings("serial")
	public static class MultiMap<K, V> extends HashMap<K, ArrayList<V>>{
		public void put(K key, V value){
			ArrayList<V> values = get(key);
			if(values == null){
				values = new ArrayList<V>();
				put(key, values);
			}
			values.add(value);
		}
	}
	private static MultiMap<String, FilterDef> filterDefs = new MultiMap<String, FilterDef>();
	private static MultiMap<String, FilterInst> filterInsts = new MultiMap<String, FilterInst>();
	
    public static void main(String [] args)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
      //search/replace of
      //inverse="true" to inverse="false" cascade="all"
      //but not: datasets in PatientImpl (?)
      //put inverse true for PatientImpl >> patientattributevalues
        Object o;
        Element toRemoveGeneratorFrom = null;
        
        FilterDef fd = new FilterDef("attributeFilter");
        fd.addParam("attribute_ii_list", "integer");
        filterDefs.put("net.sf.regadb.db.Attribute",fd);
        
        FilterInst fi = new FilterInst(fd, "attribute_ii NOT IN (:attribute_ii_list)");
        filterInsts.put("net.sf.regadb.db.Attribute", fi);
        
        fi = new FilterInst(fd, "attribute_ii IN (:attribute_ii_list)", "patientAttributeValues");
        filterInsts.put("net.sf.regadb.db.PatientImpl", fi);
        
        
        for(Map.Entry<String, Element> a : interpreter.classHbms_.entrySet())
        {
        	insertFilters(a.getKey());
            cascadeAllManyToOne(a.getKey());
            
           for(Iterator i = a.getValue().getDescendants(); i.hasNext();)
            {
                o = i.next();
                if(o instanceof Element)
                {
                    Element e = (Element)o;
                    if(e.getName().equals("set") )
                    {
                        //this if discribes the exception for PatientImpl datasets
                        if(!(e.getAttributeValue("name").equals("datasets")&& a.getKey().equals("net.sf.regadb.db.PatientImpl")))
                        {
                        e.getAttribute("inverse").setValue("false");
                        e.setAttribute(new Attribute("cascade", "all"));
                        }
                        if((e.getAttributeValue("name").equals("patientAttributeValues")&& a.getKey().equals("net.sf.regadb.db.PatientImpl")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("patientEventValues")&& a.getKey().equals("net.sf.regadb.db.PatientImpl")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("patientDatasets")&& a.getKey().equals("net.sf.regadb.db.PatientImpl")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("testResults")&& a.getKey().equals("net.sf.regadb.db.PatientImpl")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("viralIsolates")&& a.getKey().equals("net.sf.regadb.db.PatientImpl")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("therapies")&& a.getKey().equals("net.sf.regadb.db.PatientImpl")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("therapyCommercials")&& a.getKey().equals("net.sf.regadb.db.Therapy")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("therapyGenerics")&& a.getKey().equals("net.sf.regadb.db.Therapy")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("attributeNominalValues")&& a.getKey().equals("net.sf.regadb.db.Attribute")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("eventNominalValues")&& a.getKey().equals("net.sf.regadb.db.Event")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("testNominalValues")&& a.getKey().equals("net.sf.regadb.db.TestType")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("ntSequences")&& a.getKey().equals("net.sf.regadb.db.ViralIsolate")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("aaSequences")&& a.getKey().equals("net.sf.regadb.db.NtSequence")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("aaInsertions")&& a.getKey().equals("net.sf.regadb.db.AaSequence")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("aaMutations")&& a.getKey().equals("net.sf.regadb.db.AaSequence")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("testResults")&& a.getKey().equals("net.sf.regadb.db.NtSequence")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("queryDefinitionParameters")&& a.getKey().equals("net.sf.regadb.db.QueryDefinition")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("analysisDatas")&& a.getKey().equals("net.sf.regadb.db.Analysis")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("datasetAccesses")&& a.getKey().equals("net.sf.regadb.db.SettingsUser")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("datasetAccesses")&& a.getKey().equals("net.sf.regadb.db.Dataset")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                        if((e.getAttributeValue("name").equals("drugGenerics")&& a.getKey().equals("net.sf.regadb.db.Genome")))
                        {
                            e.getAttribute("inverse").setValue("true");
                        }
                    }
                    if(e.getName().equals("id") )
                    {
                        if(e.getAttribute("name").getValue().equals("uid") && ((Element)e.getParent()).getAttribute("name").getValue().equals("net.sf.regadb.db.SettingsUser"))
                        {
                            toRemoveGeneratorFrom = e;
                        }
                    }
                }
            }
           addIndexOnForeignKeys(a.getKey());
        }
        
        toRemoveGeneratorFrom.removeChild("generator");
        
        //change the composite fields from key-property to key-many-to-one
        changeKeyPropToKeyManyToMany("AaMutation.hbm.xml", "aaSequenceII", "aaSequence", "net.sf.regadb.db.AaSequence");
        changeKeyPropToKeyManyToMany("AaInsertion.hbm.xml", "aaSequenceII", "aaSequence", "net.sf.regadb.db.AaSequence");
        
        changeKeyPropToKeyManyToMany("DatasetAccess.hbm.xml", "uid", "settingsUser", "net.sf.regadb.db.SettingsUser");
        changeKeyPropToKeyManyToMany("DatasetAccess.hbm.xml", "datasetIi", "dataset", "net.sf.regadb.db.Dataset");
        
        changeKeyPropToKeyManyToMany("PatientAttributeValue.hbm.xml", "patientIi", "patient", "net.sf.regadb.db.PatientImpl");
        changeKeyPropToKeyManyToMany("PatientAttributeValue.hbm.xml", "attributeIi", "attribute", "net.sf.regadb.db.Attribute");
        
        changeKeyPropToKeyManyToMany("PatientEventValue.hbm.xml", "patientIi", "patient", "net.sf.regadb.db.PatientImpl");
        changeKeyPropToKeyManyToMany("PatientEventValue.hbm.xml", "eventIi", "event", "net.sf.regadb.db.Event");
        
        changeKeyPropToKeyManyToMany("TherapyCommercial.hbm.xml", "therapyIi", "therapy", "net.sf.regadb.db.Therapy");
        changeKeyPropToKeyManyToMany("TherapyCommercial.hbm.xml", "commercialIi", "drugCommercial", "net.sf.regadb.db.DrugCommercial");
        
        changeKeyPropToKeyManyToMany("TherapyGeneric.hbm.xml", "therapyIi", "therapy", "net.sf.regadb.db.Therapy");
        changeKeyPropToKeyManyToMany("TherapyGeneric.hbm.xml", "genericIi", "drugGeneric", "net.sf.regadb.db.DrugGeneric");
        
        changeKeyPropToKeyManyToMany("PatientDataset.hbm.xml", "datasetIi", "dataset", "net.sf.regadb.db.Dataset");
        changeKeyPropToKeyManyToMany("PatientDataset.hbm.xml", "patientIi", "patient", "net.sf.regadb.db.PatientImpl");
        
        //if there is a key-many-to-one (in the composite-id)
        //the corresponding many-to-one definition in the class definition should be removed
        removeManyToOneIfThereIsAKeyAlready();
        
        //remove the dates from the hbm xml files (easier for the versioning control system)
        removeDateFromHbmXmlFiles();
        
        //writing the new versions of the hbm xml files
        for(Map.Entry<String, Element> a : interpreter.classHbms_.entrySet())
        {
            String key = a.getKey();
            interpreter.hbmsFiles_.get(key);
            
            XMLOutputter out = new XMLOutputter();
            java.io.FileWriter writer = null;
            try 
            {
                writer = new java.io.FileWriter(interpreter.hbmsFiles_.get(key));
                out.setFormat(Format.getPrettyFormat());
                out.output(interpreter.xmlDocs_.get(key), writer);
                writer.flush();
                writer.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
        
        System.out.println("FixHbmFiles has finished");
    }
    
    private static void insertFilters(String className) {
    	InterpreteHbm interpreter = InterpreteHbm.getInstance();
        Element e = interpreter.classHbms_.get(className);
        System.out.println(e.getName());
        
        ArrayList<FilterDef> defs = filterDefs.get(className);
        if(defs != null){
        	for(FilterDef d : defs){
        		Element de = new Element("filter-def");
        		de.setAttribute("name",d.name);
        		e.getParentElement().addContent(de);
        		
        		for(Pair<String,String> p : d.params){
        			Element pe = new Element("filter-param");
        			pe.setAttribute("name",p.getKey());
        			pe.setAttribute("type",p.getValue());
        			de.addContent(pe);
        		}
        	}
        }
        
        ArrayList<FilterInst> insts = filterInsts.get(className);
		if(insts != null){
			for(FilterInst i : insts){
				Element fe = new Element("filter");
				fe.setAttribute("name",i.filter.name);
				fe.setAttribute("condition",i.condition);
				
				if(i.setName == null){
					e.addContent(fe);
				}
				else{
					for(Object o : e.getChildren("set")){
						Element ee = (Element)o;
						if(i.setName.equals(ee.getAttributeValue("name"))){
							ee.addContent(fe);
							break;
						}
					}
				}
			}
		}
        
	}

    private static void removeManyToOneIfThereIsAKeyAlready()
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
        ArrayList<Pair<String, String>> al = new ArrayList<Pair<String, String>>();
        
        for(Map.Entry<String, Element> a : interpreter.classHbms_.entrySet())
        {
            Object o;
            Element el;
            
            for(Iterator i = a.getValue().getDescendants(); i.hasNext();)
            {
                o = i.next();
                if(o instanceof Element)
                {
                    el = (Element)o;
                    if(el.getName().equals("key-many-to-one"))
                    {
                        al.add(new Pair<String, String>(a.getKey(), el.getAttributeValue("name")));
                    }
                }
            }
        }
        
        for(Pair<String, String> p : al)
        {
            removeCorrespondingManyToOne(p.getKey(), p.getValue());
        }
    }
    
    private static void removeCorrespondingManyToOne(String className, String keyName)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
        Object o;
        Element el;
        Element root = interpreter.classHbms_.get(className);
        
        for(Iterator i = root.getDescendants(); i.hasNext();)
        {
            o = i.next();
            if(o instanceof Element)
            {
                el = (Element)o;
                if(el.getName().equals("many-to-one"))
                {
                    if(el.getAttribute("name").getValue().equals(keyName))
                    {
                        //el.getParent().removeContent(el);
                        i.remove();
                    }
                }
            }
        }
    }
    
    private static String getClassNameForFileName(String fileName)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
        for(Map.Entry<String, File> a : interpreter.hbmsFiles_.entrySet())
        {
            if(a.getValue().getAbsolutePath().endsWith(fileName))
            {
                return a.getKey();
            }
        }
        
        return null;
    }
    
    private static void removeDateFromHbmXmlFiles()
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
        for(Map.Entry<String, Document> a : interpreter.xmlDocs_.entrySet())
        {
            Object o;
            Comment comment;
            for(Iterator i = a.getValue().getDescendants(); i.hasNext();)
            {
                o = i.next();
                if(o instanceof Comment)
                {
                    comment = (Comment)o;
                    comment.getParent().removeContent(comment);
                }
            }
        }
    }
    
    private static void changeKeyPropToKeyManyToMany(String hbmXmlName, String formerName, String newName, String newClass)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        String className = getClassNameForFileName(hbmXmlName);
        Element e = interpreter.classHbms_.get(className);
        
        Object o;
        Element el;
        for(Iterator i = e.getDescendants(); i.hasNext();)
        {
            o = i.next();
            if(o instanceof Element)
            {
                el = (Element)o;
                if(el.getName().equals("key-property")&&el.getAttribute("name")!=null&&el.getAttribute("name").getValue().toLowerCase().equals(formerName.toLowerCase()))
                {
                    el.setName("key-many-to-one");
                    Attribute a = el.getAttribute("name");
                    a.setValue(newName);
                    el.removeAttribute("type");
                    el.setAttribute(new Attribute("class", newClass));
                }
            }
        }
    }
    
    private static void cascadeAllManyToOne(String className)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        //String className = getClassNameForFileName(hbmXmlName);
        Element e = interpreter.classHbms_.get(className);
        
        Object o;
        Element el;
        for(Iterator i = e.getDescendants(); i.hasNext();)
        {
            o = i.next();
            if(o instanceof Element)
            {
                el = (Element)o;
                if(el.getName().equals("many-to-one"))
                {
                    el.setAttribute(new Attribute("cascade", "save-update"));
                }
            }
        }
    }
    
    private static void addIndexOnForeignKeys(String className)
    {
    	InterpreteHbm interpreter = InterpreteHbm.getInstance();
        Element e = interpreter.classHbms_.get(className);
        
        Object o;
        Element el;
        for(Iterator i = e.getDescendants(); i.hasNext();)
        {
            o = i.next();
            if(o instanceof Element)
            {
                el = (Element)o;
                
                //skip keys, they're already indexed
                if(el.getName().startsWith("key"))
                	continue;
                
                String foreignClassName = el.getAttributeValue("class");
                if(foreignClassName == null)
                	continue;
                
                Element fkCol = el.getChild("column");
                if(fkCol == null)
                	continue;
                
        		String fkName = fkCol.getAttributeValue("name");
        		if(fkName == null)
        			continue;
        		
        		el.setAttribute(new Attribute("index", e.getAttributeValue("table") +"_"+ fkName +"_idx"));
            }
        }
    }
}
