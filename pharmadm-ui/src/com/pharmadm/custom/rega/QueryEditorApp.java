/*
 * QueryEditorApp.java
 *
 * Created on September 23, 2003, 3:55 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega;
import javax.swing.*;
import com.pharmadm.util.work.WorkManager;
import com.pharmadm.custom.rega.queryeditor.AWCPrototypeCatalog;
import com.pharmadm.custom.rega.queryeditor.FrontEnd;
import com.pharmadm.custom.rega.queryeditor.FrontEndManager;
import com.pharmadm.custom.rega.queryeditor.JDBCManager;
import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.QueryVisitor;
import com.pharmadm.custom.rega.queryeditor.RegaSettings;
import com.pharmadm.custom.rega.queryeditor.SqlQuery;
import com.pharmadm.util.thread.ThreadManager;
import com.pharmadm.util.thread.WorkManagerThreadManagerAdapter;

/**
 * @pattern singleton
 *
 * @author  kdg
 */
public class QueryEditorApp implements FrontEnd{
    
    private static QueryEditorApp instance = new QueryEditorApp();
    
    private QueryEditorFrame mainFrame;
    private final JPanel threadsPanel = new JPanel();
    private final WorkManager workManager = new WorkManager(threadsPanel);
    private final ThreadManager threadManager = new WorkManagerThreadManagerAdapter(workManager);
    
    private String databaseURL = "jdbc:postgresql://localhost:5432/regadb";
    private String databaseUser = "freek";
    private String databasePassword = "freek";
    private QueryVisitor defaultQueryLanguage = new SqlQuery();
    
    
    /** Creates a new instance of QueryEditorApp */
    private QueryEditorApp() {
    }
    
    public static QueryEditorApp getInstance() {
        return instance;
    }
    
    //***************************** PharmaDMGUIApplication implementation begins
    
    public String getProduct() {
        return "ViroDM";
    }
    
    public String getVersion() {
        return "3.2.1";  // beta == \u00df
    }
    
    public String getBasicCopyRight() {
        return "\u00a9 2003-2006 PharmaDM nv.  All rights reserved.";
    }
    
    public String getSplashImageName() {
        return "ViroDMSplashScreen.jpg";
    }
    
    public WorkManager getWorkManager() {
        return workManager;
    }
    
    /**
     * Legacy support for DMax components.
     * @deprecated
     */
    public ThreadManager getThreadManager() {
        return threadManager;
    }
    
    //***************************** PharmaDMGUIApplication implementation ends
    
    
    public QueryEditorFrame getMainFrame() {
        return this.mainFrame;
    }
  
    public JPanel getThreadsPanel() {
        return threadsPanel;
    }
    
    public String getDatabaseURL() {
        return databaseURL;
    }
    
    public String getDatabaseUser() {
        return databaseUser;
    }
    
    public String getDatabasePassword() {
        return databasePassword;
    }
    
    
    
    public void showException(Exception e, String title) {
        java.io.StringWriter writer = new java.io.StringWriter();
        if (e != null) {
            e.printStackTrace(new java.io.PrintWriter(writer));
        }
        JOptionPane.showMessageDialog(mainFrame, title+":\n"+writer.toString(), title, JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean tryDefaultDBLogin() {
        try {
            JDBCManager.initInstance(databaseURL, databaseUser, databasePassword, defaultQueryLanguage);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static void printHelp() {
        System.out.println();
        System.out.println("Usage: ");
        System.out.println("   java [-Xmx300m] -jar QueryEditor.jar /location/on/filesystem ");
        System.out.println("               [--test]");
        System.out.println("               [--dbuser user --dbpassword password --dbURL url]");
        System.out.println("               [--open query]");
        System.out.println("               [--help]");
        System.out.println();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        QueryEditorApp app = getInstance();
        FrontEndManager.getInstance().setFrontEnd(app);
        
        System.out.println(app.getProduct() + ' ' + app.getBasicCopyRight());
        // u2026 is an ellipsis (…)
        RegaSettings.getInstance().load();
        
        String documentToOpen = null;
        try {
            if (!app.tryDefaultDBLogin()) {
            	System.err.println("login failed");
            }
        } catch (Exception excep) {
            getInstance().showException(excep, "Could not connect to database");
            System.exit(1);
        }
        
        
        AWCPrototypeCatalog.getInstance();
        QueryEditor editor = new QueryEditor(new Query());
        QueryEditorFrame queryEditorFrame = new QueryEditorFrame(editor);
        getInstance().mainFrame = queryEditorFrame;
        queryEditorFrame.setVisible(true);
        if (documentToOpen != null) {
            queryEditorFrame.openDocumentAtStartup(documentToOpen);
        }
    }
}
