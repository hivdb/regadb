package net.sf.regadb.install.generateDrugs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.regadb.csv.Table;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class GenerateDrugsXML 
{
    public static void main(String [] args)
    {
        String filesPath = args[0];
        Table classes = null;
        Table generic = null;
        Table commercial = null;
        Table commercial_generic= null;
        try 
        {
            classes = new Table(new BufferedInputStream(new FileInputStream(filesPath+File.separatorChar+"drug_class.csv")), false);
            generic = new Table(new BufferedInputStream(new FileInputStream(filesPath+File.separatorChar+"drug_generic.csv")), false);
            commercial = new Table(new BufferedInputStream(new FileInputStream(filesPath+File.separatorChar+"drug_commercial.csv")), false);
            commercial_generic = new Table(new BufferedInputStream(new FileInputStream(filesPath+File.separatorChar+"commercial_generic.csv")), false);
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
        
        Element drugClassesEl = new Element("DrugClasses");
        HashMap<Integer, String> classesHM = new HashMap<Integer, String>();
        ArrayList<String> class_ii = classes.getColumn(0);
        ArrayList<String> class_id = classes.getColumn(1);
        ArrayList<String> class_name = classes.getColumn(2);
        for(int i = 1; i < class_ii.size(); i++)
        {
            Element drugClassEl = new Element("DrugClass");
            Element drugClassIdEl = new Element("id");
            Element drugClassNameEl = new Element("name");
            drugClassesEl.addContent(drugClassEl);
            drugClassEl.addContent(drugClassIdEl);
            drugClassIdEl.addContent(new Text(class_id.get(i)));
            drugClassEl.addContent(drugClassNameEl);
            drugClassNameEl.addContent(new Text(class_name.get(i)));
            
            classesHM.put(Integer.parseInt(class_ii.get(i)), class_id.get(i));
        }
        
        writeXMLFile(drugClassesEl, filesPath+File.separatorChar+"DrugClasses.xml");
        
        Element drugGenericsEl = new Element("DrugGenerics");
        HashMap<Integer, String> genericDrugsHM = new HashMap<Integer, String>();
        ArrayList<String> generic_ii = generic.getColumn(0);
        ArrayList<String> generic_id = generic.getColumn(1);
        ArrayList<String> generic_class_ii = generic.getColumn(2);
        ArrayList<String> generic_name = generic.getColumn(3);
        for(int i = 1; i < generic_ii.size(); i++)
        {
            Element drugGenericEl = new Element("DrugGeneric");
            drugGenericsEl.addContent(drugGenericEl);
            
            Element drugGenericIdEl = new Element("id");
            Element drugGenericNameEl = new Element("name");
            Element drugGenericClassEl = new Element("class");
            drugGenericEl.addContent(drugGenericIdEl);
            drugGenericEl.addContent(drugGenericNameEl);
            drugGenericEl.addContent(drugGenericClassEl);
            
            drugGenericIdEl.addContent(new Text(generic_id.get(i)));
            drugGenericNameEl.addContent(new Text(generic_name.get(i)));
            drugGenericClassEl.addContent(new Text(classesHM.get(Integer.parseInt(generic_class_ii.get(i)))));
            
            genericDrugsHM.put(Integer.parseInt(generic_ii.get(i)), generic_id.get(i));
        }
        
        writeXMLFile(drugGenericsEl, filesPath+File.separatorChar+"DrugGenerics.xml");
        
        Element drugCommercialsEl = new Element("DrugCommercials");
        ArrayList<String> commmercial_comb_ii = commercial_generic.getColumn(0);
        ArrayList<String> generic_comb_ii = commercial_generic.getColumn(1);
        
        ArrayList<String> commercial_ii = commercial.getColumn(0);
        ArrayList<String> commercial_name = commercial.getColumn(1);
        
        for(int i = 1; i < commercial_ii.size(); i++)
        {
            Element drugCommercialEl = new Element("DrugCommercial");
            drugCommercialsEl.addContent(drugCommercialEl);
            Element drugCommercialNameEl = new Element("name");
            drugCommercialEl.addContent(drugCommercialNameEl);
            drugCommercialNameEl.addContent(new Text(commercial_name.get(i)));
            Element drugCommercialGenericsEl = new Element("DrugGenerics");
            drugCommercialEl.addContent(drugCommercialGenericsEl);
            for(int j = 1; j < commmercial_comb_ii.size(); j++)
            {
                if(commmercial_comb_ii.get(j).equals(commercial_ii.get(i)))
                {
                    Element genericDrug = new Element("DrugGeneric");
                    drugCommercialGenericsEl.addContent(genericDrug);
                    Element drugGenericIdEl = new Element("id");
                    genericDrug.addContent(drugGenericIdEl);
                    int ii = Integer.parseInt(generic_comb_ii.get(j));
                    String genericId = genericDrugsHM.get(ii);
                    drugGenericIdEl.addContent(new Text(genericId));
                }
            }
        }
        
        writeXMLFile(drugCommercialsEl, filesPath+File.separatorChar+"DrugCommercials.xml");
    }
    
    public static void writeXMLFile(Element root, String fileName)
    {
        Document n = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        try {
            outputter.output(n, System.out);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        java.io.FileWriter writer;
        try {
            writer = new java.io.FileWriter(fileName);
            outputter.output(n, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
