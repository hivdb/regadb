package net.sf.regadb.browser.ui;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import net.sf.regadb.browser.tomcat.TomcatControl;
import net.sf.regadb.util.file.FileListener;

public class StartRegaDB {
	public static void main(String [] args) {
		String regadbConfDir = System.getenv("REGADB_CONF_DIR");
		//TODO only replace last occurrence!!!
		regadbConfDir = regadbConfDir.replace("conf", "tomcat");
		regadbConfDir += File.separatorChar ;
		
		final String tomcatDir = regadbConfDir;
		
		final Browser b = showBrowser(tomcatDir);
		
		final String logFileName = regadbConfDir + File.separatorChar + "logs" + File.separatorChar + "regadb-start-log.txt";
        
        File logFile = new File(logFileName);
        //make sure the old log file is deleted
        //make sure a new empty file is created so the filelistener will never crash
        logFile.delete();
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
	
		Thread t = new Thread() {
		@Override
		public void run() {
			TomcatControl.getInstance().runTomcatAntFile(tomcatDir, "tomcat-start", logFileName);
			}
	      };
	      t.start();
	      
	      FileListener listener = new FileListener(logFile, 500){
			@Override
			public void newLineNotification(String line) {
				if(line.contains("Server startup in")) {
					stopTailing();
					b.initWebBrowser();
				}
			}
	      };
	      listener.start();
	}
	
	public static Browser showBrowser(final String tomcatDir) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        JFrame frame = new JFrame("RegaDB HIV Data and Analysis Management Software");

        frame.getContentPane().setLayout(new GridLayout(1, 1));
        Browser b = new Browser();
        frame.getContentPane().add(b);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
            	final String logFile = tomcatDir + File.separatorChar + "logs" + File.separatorChar + "regadb-stop-log.txt";
            	TomcatControl.getInstance().runTomcatAntFile(tomcatDir, "tomcat-stop", logFile);
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);
        
        return b;
	}
}