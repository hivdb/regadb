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
import java.util.Map;

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

        for(Map.Entry<String, String> prop : RegaDBSettings.getInstance().getHibernateConfig().getProperties().entrySet()){
	        System.err.println("Hibernate setting: '"+ prop.getKey() +"'='"+ prop.getValue() +'\'');
	        if(prop.getValue()!=null)
	        	conf.setProperty(prop.getKey(), prop.getValue());
        }
        
        return conf;
    }
    
    public static Session getEditedSession(File tempTableMappings) {
        Configuration conf = getConfiguration();
        conf.addFile(tempTableMappings);
        return conf.buildSessionFactory().openSession();
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static Connection getJDBCConnection() {
        try {
            Class.forName(RegaDBSettings.getInstance().getHibernateConfig().getDriverClass());
            String url = RegaDBSettings.getInstance().getHibernateConfig().getUrl();
            String userName = RegaDBSettings.getInstance().getHibernateConfig().getUsername();
            String password = RegaDBSettings.getInstance().getHibernateConfig().getPassword();
            Connection conn = DriverManager.getConnection(url, userName, password);
            return conn;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (SQLException e) {
            return null;
        }
    }
}
