/*
 * HibernateViroDB.java
 *
 * Created on September 4, 2003, 6:48 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import net.sf.hibernate.*;
import net.sf.hibernate.cfg.Configuration;
import com.pharmadm.util.gui.FrameShower;
import com.pharmadm.util.thread.Semaphore;
import com.pharmadm.util.work.Work;
import com.pharmadm.util.work.WorkAdapter;
import java.sql.SQLException;

/**
 * Uses Singleton pattern.
 *
 * @author  kdg
 */

public class HibernateViroDB {
    
    private static HibernateViroDB instance;
    private static final Semaphore semaphore = new Semaphore(1);
    
    private SessionFactory sessionFactory;
    private Session session;
    private final HashMap nameToPersistentClassMap = new HashMap();
    
    private static final String PACKAGE_NAME = "com.pharmadm.custom.rega.persistent";
    private static final String CLASS_LIST_RESOURCE = "/com/pharmadm/custom/rega/persistent/classlist";
    
    private HibernateViroDB() {
        initPersistentClassList();
        initSessionFactory();
    }
    
    public static HibernateViroDB getInstance() {
        if (instance == null) {
            instance = new HibernateViroDB();
        }
        return instance;
    }
    
    public Session getDefaultSession() {
        if (session == null) {
            try {
                session = createNewSession();
            } catch (HibernateException he) {
                QueryEditorApp.getInstance().showException(he, "Hibernate Exception");
            }
        }
        return session;
    }
    
    /**
     * Aquires a lock on the default session.
     */
    public Session aquireDefaultSession() {
        semaphore.down();
        return getDefaultSession();
    }
    
    /**
     * Only call this method after aquireDefaultSession().
     * Releases the default session lock.
     */
    public void destroyDefaultSession() {
        if (session != null) {
            try {
                session.close();
            } catch (HibernateException he) {
                System.err.println("Uh-oh.  Closing session failed:");
                he.printStackTrace();
            }
            session = null;
        }
        semaphore.up();
    }
    
    private void initPersistentClassList() {
        try {
            //  PACKAGE_NAME + '.' + CLASS_LIST_NAME)
            BufferedReader r = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(CLASS_LIST_RESOURCE)));
            String line = r.readLine();
            while (line != null) {
                try {
                    final String fullyQualifiedName = PACKAGE_NAME + '.' + line;
                    Class persistentClass = Class.forName(fullyQualifiedName);
                    nameToPersistentClassMap.put(fullyQualifiedName, persistentClass);
                } catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                    System.err.println("Persistent class not found: " +line);
                }
                line = r.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void initSessionFactory() {
        Configuration cfg = new Configuration();
        Iterator classes = nameToPersistentClassMap.values().iterator();
        try {
            
            while (classes.hasNext()) {
                Class persistentClass = (Class)classes.next();
                if (persistentClass != null) {
                    System.err.println("Adding class: " + persistentClass.getName());
                    try {
                        cfg.addClass(persistentClass);
                    } catch (MappingException me) {
                        System.err.println("prob at class: " + persistentClass.getName());
                        me.printStackTrace();
                    }
                }
            }
            Properties props = new Properties();
            
            // for debugging only!!
            //props.setProperty("hibernate.cglib.use_reflection_optimizer","false");
            
            QueryEditorApp qea = QueryEditorApp.getInstance();
            
            props.setProperty("hibernate.connection.url",qea.getDatabaseURL());
            props.setProperty("hibernate.connection.driver_class","oracle.jdbc.driver.OracleDriver");
            props.setProperty("hibernate.connection.username",qea.getDatabaseUser());
            props.setProperty("hibernate.connection.password",qea.getDatabasePassword());
            props.setProperty("hibernate.connection.pool_size","5");
            props.setProperty("hibernate.statement_cache","10");
            props.setProperty("hibernate.dialect","net.sf.hibernate.dialect.Oracle9Dialect");
            props.setProperty("hibernate.show_sql","true");
            cfg.setProperties(props);
            sessionFactory = cfg.buildSessionFactory();
        } catch (HibernateException he) {
        	he.printStackTrace();
        }
        
    }
    
    public boolean isPersistentClass(Class aClass) {
        return nameToPersistentClassMap.containsKey(aClass.getName());
    }
    
    private Session createNewSession() throws HibernateException {
        return sessionFactory.openSession();
    }
    
    public List find(String query) {
        try {
            Session sess = getDefaultSession();
            return sess.find(query);
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return null;
    }
    
//    public static void main(String[] args) throws HibernateException, SQLException, ClassNotFoundException {
//        QueryEditorApp.getInstance().tryDefaultDBLogin();
//        Session session = getInstance().aquireDefaultSession();
//        List drugCompounds = session.find("from DrugCompound drugCompound where drugCompound.drugCompoundName like ?", "HOLA_%", Hibernate.STRING);
//        if (drugCompounds.isEmpty()) {
//            System.out.println("No compound like that.");
//        } else {
//            try {
//                System.out.print("Initializing JDBC... ");
//                // TODO must change -- rewritten JDBCManager to not contain password in source code
//                JDBCManager.initInstance();
//                System.out.println("done.");
//                javax.swing.JFrame frame = new com.pharmadm.custom.rega.drugstockeditor.gui.DrugStockEditorJFrame(drugCompounds);
//                frame.show();
//            } catch (java.sql.SQLException sqle) {
//                sqle.printStackTrace();
//            } catch (ClassNotFoundException cnfe) {
//                cnfe.printStackTrace();
//            }
//        }
//    }
    
    /**
     * Start and show the drug stock editor asynchronously.
     *
     * @pre JDBC must already have been initialized.
     */
    public static void startDrugStockEditor(List moleculeIDs) {
//        if (moleculeIDs == null) {
//            moleculeIDs = new ArrayList(0);
//        }
//        QueryEditorApp.getInstance().getWorkManager().execute(new StartDrugStockEditorWork(moleculeIDs));
    }
    
//    private static class StartDrugStockEditorWork extends WorkAdapter {
//        
//        private List moleculeIDs;        
//        
//        public StartDrugStockEditorWork(List moleculeIDs) {
//            this.moleculeIDs = moleculeIDs;
//            setDescription("Loading transactional framework...");
//        }
//        
//        public void execute() {
//            boolean continueAllowed = true;
//            final Session session = getInstance().aquireDefaultSession();
//            QueryEditorApp app = QueryEditorApp.getInstance();
//            setDescription("Loading compound data...");
//            List compoundNames = new ArrayList();
//            
//            setTotalAmount(moleculeIDs.size());
//            setAmountDone(0);
//            Iterator iterMolIDs = moleculeIDs.iterator();
//            continueAllowed = continueAllowed && getContinuationArbiter().mayContinue();
//            try {
//                net.sf.hibernate.Query q = session.createQuery("select dc.drugCompoundName from DrugCompound as dc where dc.molecule.moleculeid = :molid");
//                q.setMaxResults(1);
//                while (continueAllowed && iterMolIDs.hasNext()) {
//                    Object nextIDObj = iterMolIDs.next();
//                    if (nextIDObj != null) {
//                        String idStr = nextIDObj.toString();
//                        try {
//                            long molID = Long.parseLong(idStr);
//                            q.setLong("molid", molID);
//                            String compoundName = (String)q.uniqueResult();
//                            if (compoundName != null) {
//                                compoundNames.add(compoundName);
//                            } else {
//                                System.err.println("Compound was unexpectedly null.  Skipping id " + idStr + ".");
//                            }
//                        } catch (HibernateException he) {
//                            System.err.println("Skipping compound with id " + nextIDObj + " due to HibernateException.");
//                            he.printStackTrace();
//                        }
//                    }
//                    increaseAmountDone();
//                    continueAllowed = continueAllowed && getContinuationArbiter().mayContinue();
//                }
//            } catch (HibernateException he) {
//                he.printStackTrace();
//                QueryEditorApp.getInstance().showException(he, "Could not compile compound query.");
//            }
//            setAmountDone(Work.UNDETERMINED_AMOUNT);
//            setTotalAmount(Work.UNDETERMINED_AMOUNT);
//            setDescription("Loading drug editor...");
//            continueAllowed = continueAllowed && getContinuationArbiter().mayContinue();
//            if (continueAllowed) {
//                javax.swing.JFrame frame = new com.pharmadm.custom.rega.drugstockeditor.gui.DrugStockEditorJFrame(compoundNames);
//                frame.addWindowListener(new WindowAdapter() {
//                    public void windowClosed(WindowEvent e) {
//                        getInstance().destroyDefaultSession();
//                    }
//                });
//                FrameShower.showFrameLater(frame);
//            } else {
//                getInstance().destroyDefaultSession();
//            }
//        }
//    }
}