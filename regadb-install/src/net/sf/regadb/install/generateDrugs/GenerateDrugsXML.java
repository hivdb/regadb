package net.sf.regadb.install.generateDrugs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.regadb.csv.Table;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class GenerateDrugsXML 
{
	private static class Maps {
		Map<String, HashMap<String,Integer>> resistanceTableOrder;
		Map<Integer, String> classes;
		Map<Integer, String> genericDrugs;
	}
    public static void main(String [] args)
    {
    	Arguments as = new Arguments();
    	PositionalArgument path = as.addPositionalArgument("output-path", true);
    	if(!as.handle(args))
    		return;
    	
    	Element drugClassesEl = new Element("DrugClasses");
    	Element drugGenericsEl = new Element("DrugGenerics");
    	Element drugCommercialsEl = new Element("DrugCommercials");
    	
        String filesPath = path.getValue();
        
        handlePathogen("hiv", drugClassesEl, drugGenericsEl, drugCommercialsEl);

        writeXMLFile(drugClassesEl, filesPath+File.separatorChar+"DrugClasses-genomes.xml");
        writeXMLFile(drugGenericsEl, filesPath+File.separatorChar+"DrugGenerics-genomes.xml");
        writeXMLFile(drugCommercialsEl, filesPath+File.separatorChar+"DrugCommercials-genomes.xml");
    }
    
    private static void handlePathogen(String pathogenPath, Element drugClassesEl, Element drugGenericsEl, Element drugCommercialsEl) {
    	String basePath = "drugs-csv" + File.separatorChar + pathogenPath + File.separatorChar;
    	
        Table classes = null;
        Table generic = null;
        Table commercial = null;
        Table commercial_generic= null;
        try 
        {
            classes = new Table(new BufferedInputStream(new FileInputStream(basePath + "drug_class.csv")), false);
            generic = new Table(new BufferedInputStream(new FileInputStream(basePath + "drug_generic.csv")), false);
            commercial = new Table(new BufferedInputStream(new FileInputStream(basePath + "drug_commercial.csv")), false);
            commercial_generic = new Table(new BufferedInputStream(new FileInputStream(basePath + "commercial_generic.csv")), false);
        } 
        catch (FileNotFoundException e) 
        {
            e.printStackTrace();
        }
        
        Maps maps = new Maps();
        maps.resistanceTableOrder = resistanceTableOrderMap(basePath + "class_generic_resistance_table_order.txt");
        maps.classes = classesMap(classes);
        maps.genericDrugs = genericDrugsMap(generic);
        
        addDrugClassElements(drugClassesEl, classes);
        addDrugGenericElements(drugGenericsEl, generic, maps);
        addDrugCommercialElements(drugCommercialsEl, commercial, commercial_generic, maps);
    }
    
    private static void addDrugCommercialElements(Element root, Table commercial, Table commercial_generic, Maps maps) {
        ArrayList<String> commmercial_comb_ii = commercial_generic.getColumn(0);
        ArrayList<String> generic_comb_ii = commercial_generic.getColumn(1);
        
        ArrayList<String> commercial_ii = commercial.getColumn(0);
        ArrayList<String> commercial_name = commercial.getColumn(1);
        ArrayList<String> commercial_atc_code = commercial.getColumn(2);
        
        for(int i = 1; i < commercial_ii.size(); i++)
        {
            Element drugCommercialEl = new Element("DrugCommercial");
            root.addContent(drugCommercialEl);

            Element drugCommercialNameEl = new Element("name");
            drugCommercialEl.addContent(drugCommercialNameEl);
            drugCommercialNameEl.addContent(new Text(commercial_name.get(i)));

            Element drugCommercialAtcCodeEl = new Element("atcCode");
            drugCommercialEl.addContent(drugCommercialAtcCodeEl);
            drugCommercialAtcCodeEl.addContent(new Text(commercial_atc_code.get(i)));
            
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
                    String genericId = maps.genericDrugs.get(ii);
                    drugGenericIdEl.addContent(new Text(genericId));
                }
            }
        }
    }
    
    private static Map<Integer, String> genericDrugsMap(Table generic) {
    	Map<Integer, String> genericDrugsMap = new HashMap<Integer, String>();
    	
        ArrayList<String> generic_ii = generic.getColumn(0);
        ArrayList<String> generic_id = generic.getColumn(1);
        for(int i = 1; i < generic_ii.size(); i++)
        {
        	genericDrugsMap.put(Integer.parseInt(generic_ii.get(i)), generic_id.get(i));
        }
        
        return genericDrugsMap;
    }
    
    private static void addDrugGenericElements(Element root, Table generic, Maps maps) {
        ArrayList<String> generic_ii = generic.getColumn(0);
        ArrayList<String> generic_id = generic.getColumn(1);
        ArrayList<String> generic_class_ii = generic.getColumn(2);
        ArrayList<String> generic_name = generic.getColumn(3);
        ArrayList<String> generic_atc_code = generic.getColumn(4);
        ArrayList<String> generic_genomes = generic.getColumn(5);

        for(int i = 1; i < generic_ii.size(); i++)
        {
            Element drugGenericEl = new Element("DrugGeneric");
            root.addContent(drugGenericEl);
            
            Element drugGenericIdEl = new Element("id");
            Element drugGenericNameEl = new Element("name");
            Element drugGenericClassEl = new Element("class");
            Element drugGenericAtcCodeEl = new Element("atcCode");
            Element drugClassResistanceTableOrderEl = new Element("resistanceTableOrder");
            Element drugGenomesEl = new Element("genomes");
            drugGenericEl.addContent(drugGenericIdEl);
            drugGenericEl.addContent(drugGenericNameEl);
            drugGenericEl.addContent(drugGenericClassEl);
            drugGenericEl.addContent(drugClassResistanceTableOrderEl);
            drugGenericEl.addContent(drugGenericAtcCodeEl);
            drugGenericEl.addContent(drugGenomesEl);
            
            drugGenericIdEl.addContent(new Text(generic_id.get(i)));
            drugGenericNameEl.addContent(new Text(generic_name.get(i)));
            String className = maps.classes.get(Integer.parseInt(generic_class_ii.get(i)));
            drugGenericClassEl.addContent(new Text(className));
            drugGenericAtcCodeEl.addContent(new Text(generic_atc_code.get(i)));

            Integer order = null;
            if(maps.resistanceTableOrder.get(className.toLowerCase())!=null)
                order = maps.resistanceTableOrder.get(className.toLowerCase()).get(generic_id.get(i).toLowerCase());
            drugClassResistanceTableOrderEl.addContent(new Text(order+""));
            
            StringTokenizer genomeTokenizer = new StringTokenizer(generic_genomes.get(i),"+");
            while(genomeTokenizer.hasMoreTokens()){
                String organismName = genomeTokenizer.nextToken();
                Element drugGenomeEl = new Element("genome");
                drugGenomesEl.addContent(drugGenomeEl);
                drugGenomeEl.addContent(new Text(organismName));
            }
        }
    }
    
    private static Map<Integer, String> classesMap(Table classes) {
        ArrayList<String> class_ii = classes.getColumn(0);
        ArrayList<String> class_id = classes.getColumn(1);
    	Map<Integer, String> classesHM = new HashMap<Integer, String>();
    	for(int i = 1; i < class_ii.size(); i++) {
            classesHM.put(Integer.parseInt(class_ii.get(i)), class_id.get(i));
    	}
    	return classesHM;
    }
    
    private static void addDrugClassElements(Element root, Table classes) {
        ArrayList<String> class_ii = classes.getColumn(0);
        ArrayList<String> class_id = classes.getColumn(1);
        ArrayList<String> class_name = classes.getColumn(2);
        ArrayList<String> resistance_class_order = classes.getColumn(3);
        for(int i = 1; i < class_ii.size(); i++)
        {
            Element drugClassEl = new Element("DrugClass");
            Element drugClassIdEl = new Element("id");
            Element drugClassNameEl = new Element("name");
            Element drugClassResistanceTableOrderEl = new Element("resistanceTableOrder");
            root.addContent(drugClassEl);
            drugClassEl.addContent(drugClassIdEl);
            drugClassIdEl.addContent(new Text(class_id.get(i)));
            drugClassEl.addContent(drugClassNameEl);
            drugClassNameEl.addContent(new Text(class_name.get(i)));
            drugClassEl.addContent(drugClassResistanceTableOrderEl);
            drugClassResistanceTableOrderEl.addContent(new Text(resistance_class_order.get(i)));
        }
    }
    
    private static Map<String, HashMap<String,Integer>> resistanceTableOrderMap(String path) {
        Map<String, HashMap<String,Integer>> resistanceTableOrder = new HashMap<String, HashMap<String,Integer>>();
        
        try 
        {
            BufferedReader in = new BufferedReader(new FileReader(path));
            String str;
            while ((str = in.readLine()) != null) 
            {
                StringTokenizer classTokenizer = new StringTokenizer(str,":");
                String className = classTokenizer.nextToken();
                className = className.trim().toLowerCase();
                String drugList = classTokenizer.nextToken();
                HashMap<String, Integer> drugOrderList = new HashMap<String, Integer>();
                StringTokenizer drugOrderTokenizer = new StringTokenizer(drugList, ",");
                int start = 0;
                System.err.println(drugOrderTokenizer.countTokens());
                while(drugOrderTokenizer.hasMoreTokens())
                {
                    String token = drugOrderTokenizer.nextToken();
                    drugOrderList.put(token.trim().toLowerCase(), start);
                    start++;
                }
                resistanceTableOrder.put(className, drugOrderList);
            }
            in.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        return resistanceTableOrder;
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
