package net.sf.regadb.build.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class EclipseParseTools
{
    public static ArrayList<String> getDependenciesFromClasspathFile(String projectDir)
    {
        ArrayList<String> dependencies;
        SAXBuilder builder;
        List classpathentries;
        
        dependencies = new ArrayList<String>();
        builder = new SAXBuilder();
        
        try
        {
            Document doc = builder.build(new File(projectDir+File.separatorChar+".classpath"));
            Element root = doc.getRootElement();
            classpathentries = root.getChildren("classpathentry");
            Iterator itr = classpathentries.iterator();
            
            while(itr.hasNext())
            {
                Element el = (Element)itr.next();
                dependencies.add(el.getAttributeValue("path"));
            }
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return dependencies; 
    }
}
