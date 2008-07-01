/*
 * Created on Dec 14, 2006
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.db.session;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml + from regadb conf xml file
            Configuration conf = getConfiguration();
            sessionFactory = conf.buildSessionFactory(); 
        } catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static Configuration getConfiguration() {
        Configuration conf = new Configuration().configure();
        
        setProperty("hibernate.connection.driver_class", conf);
        setProperty("hibernate.connection.password", conf);
        setProperty("hibernate.connection.url", conf);
        setProperty("hibernate.connection.username", conf);
        setProperty("hibernate.dialect", conf);
        
        return conf;
    }
    
    public static Session getEditedSession(File tempTableMappings) {
        Configuration conf = getConfiguration();
        conf.addFile(tempTableMappings);
        return conf.buildSessionFactory().openSession();
    }
    
    public static void setProperty(String name, Configuration conf)
    {
        String value = RegaDBSettings.getInstance().getPropertyValue(name);
        
        System.err.println("Settings:"+ " name"+ name +" val"+value);
        if(value!=null)
        conf.setProperty(name, value);
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static Connection getJDBCConnection() {
        try {
            Class.forName(RegaDBSettings.getInstance().getPropertyValue("hibernate.connection.driver_class"));
            String url = RegaDBSettings.getInstance().getPropertyValue("hibernate.connection.url");
            String userName = RegaDBSettings.getInstance().getPropertyValue("hibernate.connection.username");
            String password = RegaDBSettings.getInstance().getPropertyValue("hibernate.connection.password");
            Connection conn = DriverManager.getConnection(url, userName, password);
            return conn;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (SQLException e) {
            return null;
        }
    }
}
