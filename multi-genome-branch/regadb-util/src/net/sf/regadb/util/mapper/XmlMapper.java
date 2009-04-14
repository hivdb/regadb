package net.sf.regadb.util.mapper;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XmlMapper {
    private File file;
    
    private DefaultMapper<TestMapping> testMapper = new DefaultMapper<TestMapping>();
    
    public XmlMapper(File file){
        this.file = file;
    }
    
    public File getFile(){
        return file;
    }
    
    public void parseXml(){
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected void logErr(String msg){
        System.err.println(msg);
    }
    
    protected void parseAttributes(Element e){
        
    }
    
    protected void parseDrugs(Element e){
        
    }
    
    protected void parseEvents(Element e){
        
    }

    protected void parseTests(Element e){
        for(Object o : e.getChildren()){
            Element ee = (Element)o;
            
            TestMapping tm = new TestMapping();
            tm.parseXml(ee);
            testMapper.add(tm);
        }
    }
    
    public TestMapping getTest(String description){
        return testMapper.get(description);
    }
    
    public TestResultMapping getTestResult(String description, String value){
        TestMapping tm = testMapper.get(description);
        if(tm != null)
            return tm.get(value);
        return null;
    }
    
    public static void main(String args[]){
        XmlMapper mapper = new XmlMapper(new File("src/net/sf/regadb/util/mapper/example-mapping.xml"));
        mapper.parseXml();
        
        System.out.println(mapper.getTest("HIV viral load"));
        System.out.print(mapper.getTest("Pregnant") +" = ");
        System.out.println(mapper.getTestResult("Pregnant","Y"));
    }
}
