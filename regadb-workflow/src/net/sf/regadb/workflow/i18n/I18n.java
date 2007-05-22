package net.sf.regadb.workflow.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n 
{
    private static ResourceBundle bundle;

    public static String tr(String key) 
    {
        String value = null;
        try 
        {
            value = getResourceBundle().getString(key);
        } 
        catch (MissingResourceException e) 
        {
            System.out.println("java.util.MissingResourceException: Couldn't find value for: " + key);
        }
        if(value == null) 
        {
            value = "Could not find resource: " + key + "  ";
        }
        return value;
    }
    
    private static ResourceBundle getResourceBundle() 
    {
        if(bundle == null) 
        {
            bundle = ResourceBundle.getBundle("net.sf.regadb.workflow.i18n.regadb-workflow");
        }
        return bundle;
    }
}
