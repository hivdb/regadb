package net.sf.regadb.util.hbm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
    public static void main(String [] args)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
      //search/replace of
      //inverse="true" to inverse="false" cascade="all"
      //but not: datasets in PatientImpl (?)
      //put inverse true for PatientImpl >> patientattributevalues
        Object o;
        Element toRemoveGeneratorFrom = null;
        for(Map.Entry<String, Element> a : interpreter.classHbms_.entrySet())
        {
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
        }
        
        toRemoveGeneratorFrom.removeChild("generator");
        
        //change the composite fields from key-property to key-many-to-one
        changeKeyPropToKeyManyToMany("AaMutation.hbm.xml", "aaSequenceII", "aaSequence", "net.sf.regadb.db.AaSequence");
        changeKeyPropToKeyManyToMany("AaInsertion.hbm.xml", "aaSequenceII", "aaSequence", "net.sf.regadb.db.AaSequence");
        
        changeKeyPropToKeyManyToMany("DatasetAccess.hbm.xml", "uid", "settingsUser", "net.sf.regadb.db.SettingsUser");
        changeKeyPropToKeyManyToMany("DatasetAccess.hbm.xml", "datasetIi", "dataset", "net.sf.regadb.db.Dataset");
        
        changeKeyPropToKeyManyToMany("PatientAttributeValue.hbm.xml", "patientIi", "patient", "net.sf.regadb.db.PatientImpl");
        changeKeyPropToKeyManyToMany("PatientAttributeValue.hbm.xml", "attributeIi", "attribute", "net.sf.regadb.db.Attribute");
        
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
    
    class StringPair
    {
        
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
}
