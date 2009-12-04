package net.sf.regadb.io.importXML;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class ImportDrugs 
{
    public static ArrayList<String> importDrugClasses(IDrugTransaction t, File drugClassesXMLFile, boolean simulate)
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
    
   
    public static ArrayList<String> importGenericDrugs(IDrugTransaction t, File genericDrugXMLFile, boolean simulate)
    {
        ArrayList<String> report = new ArrayList<String>();
        
        try 
        {        
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(genericDrugXMLFile);  

            Element root = doc.getRootElement();
            List drugs = root.getChildren("DrugGeneric");
            String id;
            String name;
            String classId;
            String atcCode;
            String resistanceTableOrder;
            Integer resistanceTableOrderI;
            DrugClass dc;
            DrugGeneric dg;
            
            for(Object c : drugs)
            {
                Element drugEl = (Element)c;

                id = drugEl.getChild("id").getTextTrim();
                name = drugEl.getChild("name").getTextTrim();
                classId = drugEl.getChild("class").getTextTrim();
                atcCode = drugEl.getChild("atcCode").getTextTrim();
                resistanceTableOrder = drugEl.getChild("resistanceTableOrder").getTextTrim();
                if("null".equals(resistanceTableOrder))
                {
                    resistanceTableOrderI = null;
                }
                else
                {
                    resistanceTableOrderI = Integer.parseInt(resistanceTableOrder);
                }
                dg = t.getDrugGeneric(id);
                if(dg==null)
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
                        dg.setAtcCode(atcCode);
                        dg.setResistanceTableOrder(resistanceTableOrderI);
                        
                        for(Object el : drugEl.getChild("genomes").getChildren()){
                            Genome g = t.getGenome(((Element)el).getText());
                            if(g != null){
                                g.getDrugGenerics().add(dg);
                                dg.getGenomes().add(g);
                                
                                String msg = "Adding relation: "+ id +" - "+ g.getOrganismName();
                                System.out.println(msg);
                                report.add(msg);
                            }
                        }
                        
                        t.save(dg);
                    }
                }
                else{
                    if(simulate)
                    {
                        report.add("Synchronizing: "+ id + " - " + name);
                    }
                    else{
                    	report.add("Synchronizing: "+ id + " - " + name);

                    	if(!dg.getDrugClass().getClassId().equals(classId)){
                    		dc = t.getDrugClass(classId);
                    		dg.setDrugClass(dc);
                    	}
                    	dg.setGenericName(name);
	                    dg.setAtcCode(atcCode);
	                    dg.setResistanceTableOrder(resistanceTableOrderI);
	                    
	                    Set<String> ori = toSet(dg.getGenomes());
	                    Set<String> nuw = toSet((List<Element>)drugEl.getChild("genomes").getChildren());
	                    
	                    for(String s : nuw){
	                        if(ori.add(s)){
	                            Genome g = t.getGenome(s);
	                            if(g != null){
	                                String msg = "Adding relation: "+ id +" - "+ s;
	                                System.out.println(msg);
	                                report.add(msg);
	                                g.getDrugGenerics().add(dg);
	                                dg.getGenomes().add(g);
	                            }
	                        }
	                    }
	                    for(String s : ori){
	                        if(!nuw.contains(s)){
	                            Genome g = t.getGenome(s);
                                if(g != null){
                                    String msg = "Removing relation: "+ id +" - "+ s;
                                    System.out.println(msg);
                                    report.add(msg);
                                    g.getDrugGenerics().remove(dg);
                                    dg.getGenomes().remove(g);
                                }
	                        }
	                    }
                    }
                    t.save(dg);
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
    
    private static Set<String> toSet(List<Element> els)
    {
        Set<String> sels = new HashSet<String>();
        for(Element el : els){
            sels.add(el.getText());
        }
        return sels;
    }
    private static Set<String> toSet(Collection<Genome> genomes)
    {
        Set<String> ss = new HashSet<String>();
        for(Genome el : genomes){
            ss.add(el.getOrganismName());
        }
        return ss;
    }
    
    public static ArrayList<String> importCommercialDrugs(IDrugTransaction t, File commercialDrugXMLFile, boolean simulate)
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
            String atcCode;
            DrugGeneric dg;
            DrugCommercial dc;
            List generics;
            for(Object c : drugs)
            {
                Element drugEl = (Element)c;

                name = drugEl.getChild("name").getTextTrim();
                atcCode = drugEl.getChild("atcCode").getTextTrim();
               
                generics = drugEl.getChild("DrugGenerics").getChildren("DrugGeneric");
                
                DrugCommercial dcDb = t.getDrugCommercial(name); 
                if(dcDb == null)
                {
                    if(simulate)
                    {
                        report.add("Adding: "+ name);
                    }
                    else
                    { 
                        report.add("Added: "+ name);
                        dc = new DrugCommercial(name);
                        dc.setAtcCode(atcCode);

                        for(Object g : generics)
                        {
                            genericId = ((Element)g).getChild("id").getTextTrim();
                            dg = t.getDrugGeneric(genericId);
                            dc.getDrugGenerics().add(dg);
                        }
                       
                        t.save(dc);
                    }
                }
                else{
                	report.add("Synchronizing: "+ name);
                	if(!simulate)
                	{
                		dcDb.setAtcCode(atcCode);
                		dcDb.getDrugGenerics().clear();
                        for(Object g : generics)
                        {
                            genericId = ((Element)g).getChild("id").getTextTrim();
                            dg = t.getDrugGeneric(genericId);
                            dcDb.getDrugGenerics().add(dg);
                        }
                        t.save(dcDb);
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
