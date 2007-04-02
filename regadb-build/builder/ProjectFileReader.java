package builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;


public class ProjectFileReader
{
    private ArrayList<String> dependencies; 
    private SAXBuilder builder;
    private Element projects;
    private List project;
    
    public ProjectFileReader()
    {
        dependencies = new ArrayList<String>();
        builder = new SAXBuilder();
    }

    public ArrayList<String> getDependencies(String filename)
    {
        try
        {
            Document doc = builder.build(new File(filename));
            Element root = doc.getRootElement();
            projects = root.getChild("projects");
            project = projects.getChildren("project");
            Iterator itr = project.iterator();
            
            while(itr.hasNext())
            {
                Element el = (Element)itr.next();
                dependencies.add(el.getText());
            }
        }
        catch (JDOMException e)
        {
            //
        }
        catch (IOException e)
        {
            //
        }
        
        return dependencies; 
    }
}


