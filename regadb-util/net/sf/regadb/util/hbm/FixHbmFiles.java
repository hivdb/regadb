package net.sf.regadb.util.hbm;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class FixHbmFiles 
{
    public static void main(String [] args)
    {
        InterpreteHbm interpreter = InterpreteHbm.getInstance();
        
      //  search/replace of
      //  inverse="true"
      //to
      //  inverse="false" cascade="all"

      //but not:
      //   getDatasets in PatientImpl (?)
        Object o;
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
                        if(!(e.getAttributeValue("name").equals("datasets")&& a.getKey().equals("net.sf.regadb.db.PatientImpl")))
                        {
                        e.getAttribute("inverse").setValue("false");
                        e.setAttribute(new Attribute("cascade", "all"));
                        }
                    }
                }
            }
        }        
        
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
                out.output(interpreter.xmlDocs_.get(key), writer);
                writer.flush();
                writer.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
}
