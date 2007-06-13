package net.sf.regadb.io.importXML;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Transaction;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class ImportDrugs 
{
    public static ArrayList<String> importDrugClasses(Transaction t, File drugClassesXMLFile, boolean simulate)
    {
        ArrayList<String> report = new ArrayList<String>();
        try 
        {        
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(drugClassesXMLFile);  

            Element root = doc.getRootElement();
            List classes = root.getChildren("DrugClass");
            String id;
            String name;
            String resistanceTableOrder;
            Integer resistanceTableOrderI;
            DrugClass dc;
            for(Object c : classes)
            {
                Element classEl = (Element)c;

                id = classEl.getChild("id").getTextTrim();
                name = classEl.getChild("name").getTextTrim();
                resistanceTableOrder = classEl.getChild("resistanceTableOrder").getTextTrim();
                if("null".equals(resistanceTableOrder))
                {
                    resistanceTableOrderI = null;
                }
                else
                {
                    resistanceTableOrderI = Integer.parseInt(resistanceTableOrder);
                }
                if(t.getDrugClass(id)==null)
                {
                    if(simulate)
                    {
                        report.add("Adding: "+ id + " - " + name);
                    }
                    else
                    {
                        report.add("Added: "+ id + " - " + name);
                        dc = new DrugClass(id, name);
                        dc.setResistanceTableOrder(resistanceTableOrderI);
                        t.save(dc);
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            report.add("Unexpected error"+e.getMessage());
            return report;
        }
        
        return report;
    }
    
    public static ArrayList<String> importGenericDrugs(Transaction t, File genericDrugXMLFile, boolean simulate)
    {
        ArrayList<String> report = new ArrayList<String>();
        try 
        {        
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(genericDrugXMLFile);  

            Element root = doc.getRootElement();
            List classes = root.getChildren("DrugGeneric");
            String id;
            String name;
            String classId;
            String resistanceTableOrder;
            Integer resistanceTableOrderI;
            DrugClass dc;
            DrugGeneric dg;
            for(Object c : classes)
            {
                Element classEl = (Element)c;

                id = classEl.getChild("id").getTextTrim();
                name = classEl.getChild("name").getTextTrim();
                classId = classEl.getChild("class").getTextTrim();
                resistanceTableOrder = classEl.getChild("resistanceTableOrder").getTextTrim();
                if("null".equals(resistanceTableOrder))
                {
                    resistanceTableOrderI = null;
                }
                else
                {
                    resistanceTableOrderI = Integer.parseInt(resistanceTableOrder);
                }
                if(t.getDrugGeneric(id)==null)
                {
                    if(simulate)
                    {
                        report.add("Adding: "+ id + " - " + name);
                    }
                    else
                    {
                        report.add("Added: "+ id + " - " + name);
                        dc = t.getDrugClass(classId);
                        dg = new DrugGeneric(dc, id, name);
                        dg.setResistanceTableOrder(resistanceTableOrderI);
                        t.save(dg);
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            report.add("Unexpected error"+e.getMessage());
            return report;
        }
        
        return report;
    }
    
    public static ArrayList<String> importCommercialDrugs(Transaction t, File commercialDrugXMLFile, boolean simulate)
    {
        ArrayList<String> report = new ArrayList<String>();
        try 
        {        
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(commercialDrugXMLFile);  

            Element root = doc.getRootElement();
            List drugs = root.getChildren("DrugCommercial");
            String name;
            String genericId;
            DrugGeneric dg;
            DrugCommercial dc;
            List generics;
            for(Object c : drugs)
            {
                Element drugEl = (Element)c;

                name = drugEl.getChild("name").getTextTrim();
                
                generics = drugEl.getChild("DrugGenerics").getChildren("DrugGeneric");
                
                if(t.getDrugCommercial(name)==null)
                {
                    if(simulate)
                    {
                        report.add("Adding: "+ name);
                    }
                    else
                    { 
                        report.add("Added: "+ name);
                        dc = new DrugCommercial(name);
                        for(Object g : generics)
                        {
                            genericId = ((Element)g).getChild("id").getTextTrim();
                            dg = t.getDrugGeneric(genericId);
                            dc.getDrugGenerics().add(dg);
                        }
                       
                        t.save(dc);
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            report.add("Unexpected error"+e.getMessage());
            return report;
        }
        
        return report;
    }
}
