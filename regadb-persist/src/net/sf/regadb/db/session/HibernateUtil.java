/*
 * Created on Dec 14, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.session;

import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml + from regadb conf xml file
            Configuration conf = new Configuration().configure();
            setProperty("hibernate.connection.driver_class", conf);
            setProperty("hibernate.connection.password", conf);
            setProperty("hibernate.connection.url", conf);
            setProperty("hibernate.connection.username", conf);
            setProperty("hibernate.dialect", conf);
            sessionFactory = conf.buildSessionFactory(); 
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static void setProperty(String name, Configuration conf)
    {
        String value = RegaDBSettings.getInstance().getPropertyValue(name);
        
        if(value!=null)
        conf.setProperty(name, value);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }    
}
