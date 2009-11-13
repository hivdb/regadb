package net.sf.regadb.util.mapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XmlMapper {
    @SuppressWarnings("serial")
    public static class MapperParseException extends Exception{
        public MapperParseException(){
            super();
        }
        public MapperParseException(String msg){
            super(msg);
        }
    }
    
    private File file;
    
    private DefaultMapper<TestMapping> testMapper = new DefaultMapper<TestMapping>();
    private HashMap<String, AttributeMapping> attributeMapper = new HashMap<String,AttributeMapping>();
    
    public XmlMapper(File file) throws MapperParseException{
        this.file = file;
        parseXml();
    }
    
    public File getFile(){
        return file;
    }
    
    protected void parseXml() throws MapperParseException{
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(getFile());

            Element root = doc.getRootElement();
            
            for(Object o : root.getChildren()){
                Element e = (Element)o;
                if(e.getName().equals("attributes"))
                    parseAttributes(e);
                else if(e.getName().equals("drugs"))
                    parseDrugs(e);
                else if(e.getName().equals("events"))
                    parseEvents(e);
                else if(e.getName().equals("tests"))
                    parseTests(e);
                else
                    logErr("Unknown element '"+ e.getName() +"' in mapping file: "+ getFile().getAbsolutePath());
            }
        } catch (JDOMException e) {
            e.printStackTrace();
            throw new MapperParseException("Invalid mapping xml file.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new MapperParseException("Invalid mapping xml file.");
        }
    }
    
    protected void logErr(String msg){
        System.err.println(msg);
    }
    
    protected void parseAttributes(Element e) throws MapperParseException{
        for(Object o : e.getChildren()){
            AttributeMapping am = new AttributeMapping();
            am.parseXml((Element)o);
            attributeMapper.put(am.getName(), am);
        }
    }
    
    protected void parseDrugs(Element e){
        
    }
    
    protected void parseEvents(Element e){
        
    }

    protected void parseTests(Element e) throws MapperParseException{
        for(Object o : e.getChildren()){
            TestMapping tm = new TestMapping();
            tm.parseXml((Element)o);
            testMapper.add(tm);
        }
    }
    
    public TestMapping getTest(Map<String,String> variables){
        return testMapper.get(variables);
    }
    
    public ValueMapping getTestResult(Map<String,String> variables){
        TestMapping tm = getTest(variables);
        if(tm != null)
            return tm.get(variables);
        return null;
    }
    
    public AttributeMapping getAttribute(Map<String,String> variables){
        return attributeMapper.get(variables.get("name"));
    }
    public ValueMapping getAttributeValue(Map<String,String> variables){
        AttributeMapping am = getAttribute(variables);
        if(am != null)
            return am.get(variables);
        return null;
    }
    
    public static void main(String args[]){
        try {
            XmlMapper mapper = new XmlMapper(new File("src/net/sf/regadb/util/mapper/example-mapping.xml"));
            
            Map<String,String> vars = new HashMap<String,String>();
            
            vars.put("description", "HIV viral load log");
            vars.put("value", "5");
            
            System.out.print(mapper.getTest(vars));
            System.out.println(mapper.getTestResult(vars));
            
            vars.put("description", "Pregnant");
            vars.put("value", "Y");
            
            System.out.print(mapper.getTest(vars) +" = ");
            System.out.println(mapper.getTestResult(vars));
            
            vars.clear();
            vars.put("name", "Gender");
            vars.put("value", "m");
            System.out.println(mapper.getAttributeValue(vars));

        } catch (MapperParseException e) {
            e.printStackTrace();
        }
        
    }
}
