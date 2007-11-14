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
package com.pharmadm.custom.rega.queryeditor;
//import com.pharmadm.custom.rega.plate.gui.PCDLEditorFrame;
import java.sql.Connection;
import javax.swing.*;
import com.pharmadm.util.work.WorkManager;
//import com.pharmadm.custom.rega.datamining.DataMiningExperiment;
import com.pharmadm.custom.rega.queryeditor.gui.DatabaseLoginDialog;
import com.pharmadm.custom.rega.queryeditor.gui.QueryEditorFrame;
import com.pharmadm.util.thread.ThreadManager;
import com.pharmadm.util.thread.WorkManagerThreadManagerAdapter;

/**
 * @pattern singleton
 *
 * @author  kdg
 */
public class QueryEditorApp {
    
    private static QueryEditorApp instance = new QueryEditorApp();
    
    private QueryEditorFrame mainFrame;
    private final JPanel threadsPanel = new JPanel();
    private final WorkManager workManager = new WorkManager(threadsPanel);
    private final ThreadManager threadManager = new WorkManagerThreadManagerAdapter(workManager);
    private boolean debug = false;
//    private DataMiningExperiment dataMiningExperiment;
    
    private String databaseURL;
    private String databaseUser;
    private String databasePassword;
    
    
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
    
//    public DataMiningExperiment getDataMiningExperiment() {
//        if (dataMiningExperiment == null) {
//            dataMiningExperiment = new DataMiningExperiment();
//        }
//        return dataMiningExperiment;
//    }
    
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
    
    private static void setSystemProperties(String codeLocation) {
        final boolean inWindows = System.getProperty("os.name").startsWith("Windows");
        if (inWindows) {
            System.out.println("Windows detected.  ");
        } else {
            System.out.println("Unix detected, congratulations!  ");
        }
        if (inWindows) {
            try {
                System.setProperty("prolog.execlocation", codeLocation + "/prolog/ilprolog/");
                System.setProperty("prolog.root", codeLocation + "/prolog/");
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception elf) {
                System.err.println(elf.getMessage());
            }
        } else {
            System.setProperty("prolog.execlocation", codeLocation + "/prolog/ilprolog/linux/");
            System.setProperty("prolog.root", codeLocation + "/prolog/");
        }
    }
    
    public boolean tryDefaultDBLogin() {
        try {
            JDBCManager.initInstance(databaseURL, databaseUser, databasePassword);
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
        
        System.out.println(app.getProduct() + ' ' + app.getBasicCopyRight());
        // u2026 is an ellipsis (…)
//        com.pharmadm.dmax.SplashWindow.showInstance(app.getProduct(), app.getBasicCopyRight(), app.getSplashImageName());
        RegaSettings.getInstance().load();
        
        boolean openPCDLEditor = false;
        String documentToOpen = null;
        if ((args != null) && (args.length >= 1)) {
            setSystemProperties(args[0]);
            for (int i = 1; i < args.length; i++) {
                String arg = args[i];
                if (arg.equals("--test")) {
                    getInstance().debug = true;
                } else if (arg.equals("--dbuser") && (i+1)<(args.length)) {
                    app.databaseUser = args[++i];
                } else if (arg.equals("--dbpassword") && (i+1)<(args.length)) {
                    app.databasePassword = args[++i];
                } else if (arg.equals("--dbURL") && (i+1)<(args.length)) {
                    app.databaseURL = args[++i];
                } else if (arg.equals("--open") && (i+1)<(args.length)) {
                    documentToOpen = args[++i];
                } else if (arg.equals("--pcdleditor")) {
                    openPCDLEditor = true;
                } else if (arg.equals("--help")) {
                    printHelp();
                }
            }
        } else {
            System.out.println("No code location specified on the command line.  Molecule search will not be available.");
        }
        try {
            if (!app.tryDefaultDBLogin()) {
                DatabaseLoginDialog dld = new DatabaseLoginDialog(null, true);
//                com.pharmadm.dmax.SplashWindow.hideInstance();
                dld.show();
                Connection conn = dld.getConnection();
                if (conn != null) {
                    JDBCManager.initInstance(conn);
                    app.databaseURL = dld.getRDBMSConnectPanel().getURL();
                    app.databaseUser = dld.getRDBMSConnectPanel().getUserName();
                    app.databasePassword = dld.getRDBMSConnectPanel().getPassword();
                } else {
                    System.exit(0); // The user clicked cancel
                }
            }
        } catch (Exception excep) {
//            com.pharmadm.dmax.SplashWindow.hideInstance();
            getInstance().showException(excep, "Could not connect to database");
            System.exit(1);
        }
        
//        if (openPCDLEditor) {
//            // just for testing
//            PCDLEditorFrame.main(new String[] {});
//            com.pharmadm.dmax.SplashWindow.hideInstance();
//            return;
//        }
        
//        com.pharmadm.dmax.SplashWindow.showInstance(app.getProduct() + " - Loading database schema\u2026", app.getBasicCopyRight(), app.getSplashImageName());
        AWCPrototypeCatalog.getInstance();
        //com.pharmadm.dmax.SplashWindow.hideInstance();
//        com.pharmadm.dmax.SplashWindow.showInstance(app.getProduct() + " - Opening main window\u2026", app.getBasicCopyRight(), app.getSplashImageName());
        QueryEditor editor = new QueryEditor(new Query());
//        if (getInstance().debug) {
//            com.pharmadm.custom.rega.chem.search.MoleculeClauseTest.constructDebugQuery(editor);
//        }
        QueryEditorFrame queryEditorFrame = new QueryEditorFrame(editor);
        getInstance().mainFrame = queryEditorFrame;
        queryEditorFrame.show();
//        com.pharmadm.dmax.SplashWindow.hideInstance();
        if (documentToOpen != null) {
            queryEditorFrame.openDocumentAtStartup(documentToOpen);
        }
    }
}
