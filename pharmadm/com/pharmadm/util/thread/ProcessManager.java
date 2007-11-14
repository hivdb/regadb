/*
 * ProcessManager.java
 *
 * Created on February 14, 2001, 9:14 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.thread;

import java.io.*;

/**
 * A class to manage running other processes, outside the JVM.
 * @author  kdg
 * @version 1.0
 */
public class ProcessManager {
    
    private static ProcessManager instance = new ProcessManager();
    
    /** Creates new ProcessManager */
    protected ProcessManager() {
    }
    
    public static ProcessManager getInstance() {
        return instance;
    }
    
    public void runWaitForProcess(String command, String description, ThreadManager threadManager) {
        Work work = new ProcessRunWork(command, description);
        System.out.println("# ProcessMan: waiting for command.");
        threadManager.addWorkAndWait(work);
    }
    
    public void runWaitForProcess(String command, String description) {
        runWaitForProcess(command, null, description);
    }
    
    public void runWaitForProcess(String command, String[] environment, String description) {
        Process p = null;
        String s;
        BufferedReader processOutput;
        PrintErrorsThread pet;
        
        try {
            p = Runtime.getRuntime().exec(command, environment);
            processOutput = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
            
            pet=new PrintErrorsThread(p);
            pet.start();
            if (environment != null) {
                System.out.print("Waiting for process [" + command + "] [");
                for (int i = 0; i < environment.length; i++) {
                    System.out.print(environment[i] + ' ');
                }
                System.out.println("] to terminate...");
            } else {
                System.out.println("Waiting for process [" + command + "] to terminate...");
            }
            
            while ((s = processOutput.readLine()) != null) {
                System.out.println("   PROCESS OUTPUT: " + s);
            }
            
            while (pet.isAlive()) {;}
            System.out.println("Process terminated");
            
        } catch (SecurityException se) {
            System.err.println("Could not " + description + " due to security limitations.");
            se.printStackTrace();
        } catch (IOException ioe) {
            System.err.println("Could not " + description + " due to an I/O problem.");
            ioe.printStackTrace();
        }
    }
    
    private class PrintErrorsThread extends Thread {
        BufferedReader processErrors;
        String last = null;
        int nbLast = 0;
        
        PrintErrorsThread(Process p) {
            this.processErrors = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getErrorStream())));
        }
        
        public void run() {
            String s;
            try {
                while ((s = processErrors.readLine()) != null) {
                    if (!s.equals(last)) {
                        if (nbLast > 0) {
                            System.err.println("   PROCESS REPEATED this message " +nbLast+ " more time(s).");
                            nbLast = 0;
                        }
                        System.err.println("   PROCESS MESSAGE: " + s);
                        last = s;
                    } else {
                        nbLast++;
                    }
                }
            } catch (IOException ioe) {
                ;
            }
        }
    }
    
    public class ProcessRunWork implements Work {
        private ThreadManager threadManager;
        private int nbLines = 0;
        private long totalLines = 0;
        private String  command;
        private String description;
        
        public ProcessRunWork(String command, String description) {
            this.command = command;
            this.description = description;
        }
        
        public void execute() {
            System.out.println("# ProcessRun: execution control thread has started.");
            Process p = null;
            String s;
            BufferedReader processOutput;
            PrintErrorsThread pet;
            
            try {
                p = Runtime.getRuntime().exec(command);
                processOutput = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream())));
                
                pet=new PrintErrorsThread(p);
                pet.start();
                System.out.println("Waiting for process [" + command + "] to terminate...");
                
                while ((s = processOutput.readLine()) != null) {
                    System.out.println("   PROCESS OUTPUT: " + s);
                    System.out.print("# ProcessRun: may I continue? ");
                    threadManager.mayContinue(this);
                    System.out.println("yes.");
                    nbLines++;
                }
                
                while (pet.isAlive()) {;}
                System.out.println("Process terminated");
                
            } catch (SecurityException se) {
                System.out.println("Could not " + description + " due to security limitations.");
                se.printStackTrace();
            } catch (IOException ioe) {
                System.out.println("Could not " + description + " due to an I/O problem.");
                ioe.printStackTrace();
            }
        }
        
        public int getAmountDone() {
            return nbLines;
        }
        
        public String getDescription() {
            return description;
        }
        
        public int getTotalAmount() {
            if (nbLines > totalLines) {
                totalLines += nbLines;
            }
            return (int)totalLines;
        }
        
        public void setThreadManager(ThreadManager manager) {
            threadManager = manager;
        }
        
        public boolean canAbort() {
            return false;
        }
        
    }
    
}
