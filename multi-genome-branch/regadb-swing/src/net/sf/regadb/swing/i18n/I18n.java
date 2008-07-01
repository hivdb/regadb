package net.sf.regadb.swing.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class I18n 
{
    private static ResourceBundle bundle;
    private static String uri;
    private static Locale lo;
    
    public static void setBundleURI(String uri) {
    	setBundleURI(uri, "en", "US");
    }
    public static void setBundleURI(String uri, String lang, String country) {
    	I18n.uri = uri;
    	lo = new Locale(lang, country);
    }
    
    public static String tr(String key) 
    {
        String value = null;
        try 
        {
            value = getResourceBundle().getString(key);
        } catch (MissingResourceException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return value;
    }
    
    private static ResourceBundle getResourceBundle()
    {
        if(bundle == null) 
        {
            bundle = ResourceBundle.getBundle(uri, lo);
        }
        return bundle;
    }
}
