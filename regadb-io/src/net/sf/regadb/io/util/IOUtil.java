package net.sf.regadb.io.util;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public class IOUtil 
{
    public static String getStringFromDoc(Element root)
    {
        XMLOutputter outputter = new XMLOutputter();
        
        return outputter.outputString(root);
    }
}
