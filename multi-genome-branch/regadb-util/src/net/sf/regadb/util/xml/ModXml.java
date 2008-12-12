package net.sf.regadb.util.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

public class ModXml {
    public static void main(String args[]){
        if(args.length < 1){
            System.err.println("Usage: <xml-file>");
            System.exit(1);
        }
        File xmlFile = new File(args[0]);
        if(!xmlFile.exists()){
            System.err.println("File not found: "+ xmlFile.getAbsolutePath());
            System.exit(1);
        }
        
        ModXml mx = new ModXml(xmlFile);
        mx.run();
    }
    
    private File xmlFile;
    private Document doc;
    private Map<String, String> testReplacements;
    private Map<String, String> testTypeReplacements;
    
    public ModXml(){
        
    }
    
    public ModXml(File xmlfile){
        this.setXmlFile(xmlfile);
        
        try {
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(xmlFile);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        testTypeReplacements = new HashMap<String, String>();        
        testReplacements = new HashMap<String, String>();
        
        testTypeReplacements.put("HIV-1 Seroconversion", "Seroconversion");
        testReplacements.put("HIV-1 Seroconversion", "Seroconversion (generic)");
        
        testTypeReplacements.put("HIV-1 Viral Load (copies/ml)", "Viral Load (copies/ml)");
        testReplacements.put("HIV-1 Viral Load (generic)", "Viral Load (copies/ml) (generic)");        
    }
    
    @SuppressWarnings("unchecked")
    public List<Element> getElements(String path){
        try{
            XPath xpath = XPath.newInstance(path);
            return xpath.selectNodes(getDoc());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new ArrayList<Element>();
    }
    
    protected void log(String s){
        System.err.println(s);
    }
    
    protected String replaceTestType(String s){
        return replace(testTypeReplacements, s);
    }
    protected String replaceTest(String s){
        return replace(testReplacements, s);
    }
    protected String replace(Map<String,String> map, String s){
        String r = map.get(s);
        if(r == null)
            r = s;
        
        log(s +" -> "+ r);
        return r;        
    }
    
    protected void addGenome(Element e, String genome){
        Element g = new Element("genome");
        g.setText(genome);
        e.addContent(g);
    }
    
    public void run(){
        
        List<Element> es;
        
        es = getElements("/patients/patients-el/Patient/testResults/testResults-el/TestResult/test/Test/parent::*");
        for(Element e : es){
            Element t = e.getChild("Test");
            Element td = t.getChild("description");
            String tdval = td.getValue();

            Element tt = t.getChild("testType").getChild("TestType");
            Element ttd = tt.getChild("description");
            String ttdval = ttd.getValue();
                        
            if(tdval.contains("HIV-1"))
                addGenome(tt, "HIV-1");
            
            td.setText(replaceTest(tdval));
            ttd.setText(replaceTestType(ttdval));
        }
        
        es = getElements("//Therapy");
        for(Element e : es){
            addGenome(e, "HIV-1");
        }
        
        log("Writing...");
        try{
            FileOutputStream out = new FileOutputStream(getXmlFile().getAbsolutePath().replace(".xml", ".mod.xml"));
            XMLOutputter outputter = new XMLOutputter();
            outputter.output(getDoc(), out);
            out.close();
            
            log("Done.");
        }
        catch(Exception e){
            log("Error.");
            e.printStackTrace();
        }
    }
    
    private static void listElements(List<Element> es, String indent) {
        for (Element e : es){
            listElement(e, indent);
        }
    }

    @SuppressWarnings("unchecked")
    private static void listElement(Element e, String indent) {
        System.out.println(indent + "*Element, name:" + e.getName() + ", text:"
                + e.getText().trim());

        List<Attribute> as = e.getAttributes();
        listAttributes(as, indent + "\t");

        List<Element> c = e.getChildren();
        listElements(c, indent + "\t");
    }

    private static void listAttributes(List<Attribute> as, String indent) {
        for (Attribute a : as) {
            System.out.println(indent + "*Attribute, name:" + a.getName()
                    + ", value:" + a.getValue());
        }
    }

    public void setXmlFile(File xmlFile) {
        this.xmlFile = xmlFile;
    }

    public File getXmlFile() {
        return xmlFile;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public Document getDoc() {
        return doc;
    }
}
