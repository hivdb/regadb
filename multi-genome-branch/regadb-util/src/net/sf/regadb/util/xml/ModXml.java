package net.sf.regadb.util.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sf.regadb.util.frequency.TickCounterTimer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class ModXml extends DefaultHandler {

    public static void main(String[] args) {
        ModXml mss = new ModXml(args[0]);
        mss.run();
    }    

    private TickCounterTimer counter;

    private File xmlFile;
    private XMLOutputter xmlout;
    private PrintStream out;
    private PrintStream outVi;

    private Set<String> testsToRemove = new HashSet<String>();
    private Map<String, String> testsToReplace = new HashMap<String, String>();
    private Map<String, String> testTypesToReplace = new HashMap<String, String>();    

    private Map<String, Element> valueTypesToMove = new HashMap<String, Element>();
    private Map<String, Element> testObjectsToMove = new HashMap<String, Element>();
    
    private Stack<Element> stack = new Stack<Element>();
    private Element root;
    private StringBuilder tmpChars = new StringBuilder();

    public ModXml(String fileName) {
        testsToRemove.add("REGA v7.1");
        testsToRemove.add("ANRS 2006.07");
        testsToRemove.add("HIVDB 4.2.9");
        testsToRemove.add("REGA v6.4.1");
        testsToRemove.add("Rega HIV Type Tool");
        testsToRemove.add("Rega HIV-1 Subtype Tool");

        testTypesToReplace.put("HIV-1 Seroconversion", "Seroconversion");
        testsToReplace.put("HIV-1 Seroconversion", "Seroconversion (generic)");

        testTypesToReplace.put("HIV-1 Viral Load (copies/ml)", "Viral Load (copies/ml)");
        testsToReplace.put("HIV-1 Viral Load (generic)", "Viral Load (copies/ml) (generic)");

        xmlFile = new File(fileName);
        
        counter = new TickCounterTimer(System.err, 2000);
    }

    public void run() {
        counter.start();

        try {
            
            out = new PrintStream(new FileOutputStream(xmlFile.getAbsolutePath().replace(".xml", ".mod.xml")));
            outVi = new PrintStream(new FileOutputStream(xmlFile.getAbsolutePath().replace(".xml", ".vi.xml")));

            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<patients>");

            outVi.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            outVi.println("<viralIsolates>");
            
            xmlout = new XMLOutputter();
            xmlout.setFormat(Format.getPrettyFormat());

            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setContentHandler(this);
            reader.parse(xmlFile.toString());

            outVi.println("</viralIsolates>");
            out.println("</patients>");

            outVi.close();
            out.close();
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        counter.stop();
        counter.print();
    }
    
    public void startElement(String nsURI, String strippedName, String tagName, Attributes attributes) {
        Element el = new Element(tagName);
        
        for(int i=0; i<attributes.getLength(); ++i)
            el.setAttribute(attributes.getLocalName(i), attributes.getValue(i));
        
        if(tagName.equals("patients-el")){
            root = el;
            stack.push(el);
        }
        else{
            if(stack.size() > 0){
                stack.peek().addContent(el);
                stack.push(el);
            }
        }

        tmpChars.setLength(0);
    }

    public void characters(char[] ch, int start, int length) {
        for (int i = 0; i < length; ++i)
            tmpChars.append(ch[start + i]);
    }

    public void endElement(String uri, String localName, String qName) {
        if(stack.size()>0){
            String content = tmpChars.toString().trim();
            if (content.length() > 0) {
                tmpChars.setLength(0);
                
                stack.peek().setText(content);
            }
    
            stack.pop();
            
            if(localName.equals("patients-el")){
                processPatient(root);
                stack.clear();
                root = null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public List<Element> getElements(Document doc, String path){
        try{
            XPath xpath = XPath.newInstance(path);
            return xpath.selectNodes(doc);
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
        return replace(testTypesToReplace, s);
    }
    protected String replaceTest(String s){
        return replace(testsToReplace, s);
    }
    protected String replace(Map<String,String> map, String s){
        String r = map.get(s);
        if(r == null)
            r = s;
        
        return r;        
    }
    
    protected void addGenome(Element e, String genome){
        Element g = new Element("genome");
        g.setText(genome);
        e.addContent(g);
    }
    
    private void removeTestResult(Element test){
        Element testResult = test.getParentElement();
        Element testResultsEl = testResult.getParentElement();
        Element testResults = testResultsEl.getParentElement();
        
        testResults.removeContent(testResultsEl);
    }
    
    private void processPatient(Element root){
        Document doc = new Document();
        doc.setRootElement(root);
        
        List<Element> es;
        
        es = getElements(doc, "//test");
        for(Element e : es){
            Element t = e.getChild("Test");
            
            if(t != null){
                Element td = t.getChild("description");
                String tdval = td.getValue();
                
                Element tt = t.getChild("testType").getChild("TestType");

                boolean remove = testsToRemove.contains(tdval);
                
                if(tt != null){
                    String ref = tt.getChild("testObject").getChildText("reference");
                    
                    if(!remove){
                        if(tdval.contains("HIV-1"))
                            addGenome(tt, "HIV-1");
                        
                        Element ttd = tt.getChild("description");
                        String ttdval = ttd.getValue();
                        ttd.setText(replaceTestType(ttdval));
                        
                        //move removed TestObject definition
                        Element testObject = testObjectsToMove.get(ref);
                        if(testObject != null){
                            tt.getChild("testObject").addContent(testObject);
                            testObjectsToMove.remove(ref);
                        }
                    }
                    else{
                        //store ValueType and TestObject definitions which will be removed
                        Element vt = tt.getChild("valueType").getChild("ValueType");
                        
                        if(vt != null){
                            String vtRef = tt.getChild("valueType").getChildText("reference");
                            vt.getParent().removeContent(vt);
                            valueTypesToMove.put(vtRef,vt);
                        }
                        
                        Element testObject = tt.getChild("testObject").getChild("TestObject");
                        if(testObject != null){
                            testObject.getParent().removeContent(testObject);
                            testObjectsToMove.put(ref,testObject);
                        }                        
                    }
                }
                
                if(remove){
                    testsToRemove.add(e.getChild("reference").getText());
                    removeTestResult(e);
                }
                else{
                    td.setText(replaceTest(tdval));
                }
            }
            else{
                if(testsToRemove.contains(e.getChild("reference").getText()))
                    removeTestResult(e);
            }
        }
        
        //remove AaSequences
        es = getElements(doc, "//NtSequence");
        for(Element e : es){
            e.removeChildren("aaSequences");
        }
        
        //move removed ValueType definitions
        if(valueTypesToMove.size() > 0){
            es = getElements(doc, "//valueType");
            for(Element e : es){
                if(e.getChild("ValueType") == null){
                    String ref = e.getChildText("reference");
                    Element def = valueTypesToMove.get(ref);               
                    if(def != null){
                        e.addContent(def);
                        valueTypesToMove.remove(ref);
                    }
                }
            }
        }

        //export viral isolates to a viral isolate xml file
        es = getElements(doc, "//viralIsolates-el");
        try {
            for(Element e : es){
                xmlout.output(e, outVi);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        try{
            xmlout.output(root, out);
        }
        catch(Exception e){
            log("Error.");
            e.printStackTrace();
        }
        
        counter.tick();
    }
}
