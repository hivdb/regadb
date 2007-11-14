/*
 * ThreadManager.java
 *
 * Created on March 20, 2001, 3:53 PM
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.thread;

import java.awt.event.ActionListener;

import java.util.LinkedList;
import java.util.Vector;

import javax.swing.*;


/**
 * A class to manage the execution of Works.
 * A Work may only be added to one ThreadManager.
 *
 * Deprecated, use com.pharmadm.util.work.WorkManager instead.
 *
 * @deprectated
 *
 * @author  kdg
 */
public class ThreadManager {
    private static boolean debug = false;
    LinkedList llist = new LinkedList();
    
    // the current number of runing threads
    private int nbThreads = 0;
    
    // how many threads are allowed to run simulaneously
    private int preferredNbThreads = 2;
    
    // the number of active works that are waiting for another (at least one) work in the queue.
    private int nbWaitingWorks = 0;
    private boolean prepareToQuit = false;
    private JPanel jPanelInfo;
    private final boolean showProgress;
    
    /** Creates new ThreadManager */
    
    public ThreadManager(JPanel jPanelInfo, boolean showProgress) {
        this.jPanelInfo = jPanelInfo;
        this.showProgress = showProgress;
        if (jPanelInfo != null) {
            jPanelInfo.setLayout(new java.awt.GridBagLayout());
        }
    }
    
    /**
     * A work can ask if it may continue.
     * This allows the ThreadManager to pause and handle the thread if needed.
     */
    public boolean mayContinueLight(Work work) {
        return !(((WorkPerformer) Thread.currentThread()).isAborted() ||
        prepareToQuit);
    }
    
    /**
     * A work can ask if it may continue.
     * This allows the ThreadManager to pause and handle the thread if needed.
     * Note: one unit of work should be done.
     * This method updates the GUI (as opposed to mayContinueLight).
     */
    public boolean mayContinue(Work work) {
        ((WorkPerformer) Thread.currentThread()).updateProgress();
        return mayContinueLight(work);
    }
    
    /**
     * Adds a Work to the queue.
     * The Work will be processed in another thread.
     * This method returns without waiting for the Work to be completed.
     */
    public synchronized void addWork(Work work) {
        work.setThreadManager(this);
        llist.add(work);
        
        if (debug) {
            System.out.println("*** ThreadMan: added work to schedule. " +
            llist.size());
        }
        
        startThreadIfPreferred();
    }
    
    public void addWorkAndWait(Work work) {
        work = new WorkWaitWrapper(work);
        
        if (Thread.currentThread() instanceof WorkPerformer) {
            synchronized (this) {
                nbWaitingWorks++;
            }
        }
        
        addWork(work);
        ((WorkWaitWrapper) work).waitForCompletion();
        
        if (Thread.currentThread() instanceof WorkPerformer) {
            synchronized (this) {
                nbWaitingWorks--;
            }
        }
    }
    
    /**
     * Adds several Works to the queue at once.
     * The Works will be processed using an indetermined number of threads.
     * This method returns without waiting for any of the Works to be completed.
     */
    public synchronized void addWorks(Vector works) {
        for (int i = 0; i < works.size(); i++) {
            ((Work) works.elementAt(i)).setThreadManager(this);
        }
        
        llist.addAll(works);
        startThreadIfPreferred(works.size());
    }
    
    /**
     * Adds several Works to the queue at once.
     * The Works will be processed using an undetermined number of threads.
     * This method waits for all the Works to be completed before it returns.
     */
    // This may NOT be synchonized!  Otherwise, all subseqent calls to addWork would block.
    public void addWorksAndWait(Vector works) {
        for (int i = 0; i < works.size(); i++) {
            works.set(i, new WorkWaitWrapper((Work) works.elementAt(i)));
        }
        
        if (Thread.currentThread() instanceof WorkPerformer) {
            synchronized (this) {
                nbWaitingWorks++;
            }
        }
        
        addWorks(works); // call to synchronized method
        
        for (int i = 0; i < works.size(); i++) {
            ((WorkWaitWrapper) works.elementAt(i)).waitForCompletion();
        }
        
        if (Thread.currentThread() instanceof WorkPerformer) {
            synchronized (this) {
                nbWaitingWorks--;
            }
        }
    }
    
    // Clears the queue, and sends all threads a gentle signal to stop when they are ready to do so.
    // New jobs submissions are ignored.
    public void abortAll() {
        System.out.println(
        "ThreadManager: Sending the stop signal to all running operations.");
        synchronized (this) {
            llist.clear();
            prepareToQuit = true;
        }
    }
    
    public synchronized void systemExitIfIdle() {
        if (nbThreads == 0) {
            System.exit(0);
        }
    }
    
    /**
     * Returns a Work if there is one available for the requesting thread.
     * If null is returned, the requesting Thread must stop (the number of threads active is already updated).
     */
    private synchronized Work decideOnWorkOrThreadStop() {
        boolean wantThreadStop = ((nbThreads > (preferredNbThreads + nbWaitingWorks)) ||
        prepareToQuit);
        Work work = null;
        if (!wantThreadStop && !llist.isEmpty()) {
            work = (Work)llist.removeFirst();
        }
        if (work == null) {
            nbThreads--;
        }
        return work;
    }
    
    private synchronized void startThreadIfPreferred() {
        if ((nbThreads < (preferredNbThreads + nbWaitingWorks)) &&
        !prepareToQuit) {
            WorkPerformer newThread = new WorkPerformer();
            
            if (debug) {
                System.out.println("*** ThreadMan: starting new thread.");
            }
            
            newThread.start();
        }
    }
    
    private synchronized void startThreadIfPreferred(int maxNbNewStarted) {
        if (!prepareToQuit) {
            for (int i = 0;
            (i < maxNbNewStarted) &&
            (nbThreads < (preferredNbThreads + nbWaitingWorks)); i++) {
                WorkPerformer newThread = new WorkPerformer();
                newThread.start();
            }
        }
    }
    
    private synchronized void decreaseNbThreads() {
        nbThreads--;
    }
    
    private synchronized void increaseNbThreads() {
        nbThreads++;
    }
    
    private void addGUIWidgets(JComponent[] widgets) {
        SwingUtilities.invokeLater(new WidgetsAddition(widgets));
    }
    
    private void addGUIWidgetsNow(JComponent[] widgets) {
        new WidgetsAddition(widgets).run();
    }
    
    private void removeGUIWidgets(JComponent[] widgets) {
        SwingUtilities.invokeLater(new WidgetsRemoval(widgets));
    }
    
    private class WidgetsAddition implements Runnable {
        private JComponent[] widgets;
        
        public WidgetsAddition(JComponent[] widgets) {
            this.widgets = widgets;
        }
        
        public void run() {
            synchronized (ThreadManager.this) {
                for (int i = 0; i < widgets.length; i++) {
                    if (showProgress) {
                        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
                        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
                        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
                        gridBagConstraints.weightx = 1.0;
                        jPanelInfo.add(widgets[i], gridBagConstraints);
                        jPanelInfo.revalidate();
                        jPanelInfo.repaint();
                    }
                }
            }
        }
    }
    
    private class WidgetsRemoval implements Runnable {
        private JComponent[] widgets;
        
        public WidgetsRemoval(JComponent[] widgets) {
            this.widgets = widgets;
        }
        
        public void run() {
            synchronized (ThreadManager.this) {
                // Progress bar must be set to determinate state before removing it due to a flaw in JRE 1.4.
                // Not doing this might result in an infinite loop of the background thread animating the indeterminate state.
                JProgressBar progressBar = ((ThreadPanel)widgets[0]).getProgressBar();
                progressBar.setMaximum(1);
                progressBar.setValue(1);
                progressBar.setIndeterminate(false);
                for (int i = 0; i < widgets.length; i++) {
                    if (jPanelInfo != null) {
                        jPanelInfo.remove(widgets[i]);
                        jPanelInfo.revalidate();
                        jPanelInfo.repaint();
                    }
                    
                    if (debug) {
                        System.out.println("*** ThreadMan: widget removed.");
                    }
                }
            }
        }
    }
    
    private class WorkWaitWrapper implements Work {
        private Work work;
        private boolean done = false;
        
        public WorkWaitWrapper(Work work) {
            this.work = work;
        }
        
        public void execute() {
            work.execute();
            signalCompletion();
        }
        
        public int getTotalAmount() {
            return work.getTotalAmount();
        }
        
        public void setThreadManager(ThreadManager manager) {
            work.setThreadManager(manager);
        }
        
        // It is important for these methods to be synchronized.
        private synchronized void signalCompletion() {
            done = true;
            notifyAll();
        }
        
        public synchronized void waitForCompletion() {
            while (!done) {
                try {
                    wait();
                } catch (InterruptedException ie) {
                }
            }
        }
        
        public int getAmountDone() {
            return work.getAmountDone();
        }
        
        public String getDescription() {
            return work.getDescription();
        }
        
        public boolean canAbort() {
            return work.canAbort();
        }
    }
    
    private class WorkPerformer extends Thread {
        private boolean pauzed = false;
        private boolean aborted = false;
        private int totalAmount = 0;
        private ThreadPanel threadPanel;
        private JProgressBar progressBar;
        private Work work = null;
        
        public void start() {
            increaseNbThreads();
            this.setPriority(Thread.MIN_PRIORITY);
            super.start();
        }
        
        public void run() {
            work = decideOnWorkOrThreadStop();
            
            if (work != null) {
                
                if (debug) {
                    System.out.println("*** Thread: found some work to do.");
                    if (work instanceof WorkWaitWrapper) {
                        System.out.println("work: " + ((WorkWaitWrapper)work).work.getClass().getName());
                    } else {
                        System.out.println("work: "+work.getClass().getName());
                    }
                }
                final JComponent[] widgets = new JComponent[1];
                
                // The following causes a dealock if not performed in the GUI thread.
                // (E.g. the ThreadPanel constructor enters a monitor.)
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        threadPanel = new ThreadPanel();
                        threadPanel.getCheckBoxPauze().addActionListener( new PauzeListener(threadPanel.getCheckBoxPauze()));
                        threadPanel.getMenuItemAbort().addActionListener( new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                abort();
                            }
                        });
                        progressBar = threadPanel.getProgressBar();
                        
                        widgets[0] = threadPanel;
                        addGUIWidgetsNow(widgets);
                    }
                });
                
                while (work != null) {
                    setName("Perform " + work.getDescription());
                    totalAmount = work.getTotalAmount();
                    SwingUtilities.invokeLater(new ShowWorkRunnable(work));
                    
                    if (debug) {
                        System.out.println("*** Thread: executing work.");
                    }
                    
                    try {
                        work.execute();
                    } catch (Exception e) {
                        final String description = work.getDescription();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                threadPanel.setDescription("***CRASHED*** " + description);
                            }
                        });
                        e.printStackTrace();
                        java.awt.Toolkit.getDefaultToolkit().beep();
                        
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ie) {
                        }
                    }
                    
                    work = decideOnWorkOrThreadStop();
                }
                removeGUIWidgets(widgets);
            }
        }
        
        /**
         * @pre this WorkPerformer must have a work in progress.
         */
        public void updateProgress() {
            SwingUtilities.invokeLater(new ProgressUpdate(work, totalAmount));
        }
        
        public JProgressBar getProgressBar() {
            return progressBar;
        }
        
        private void abort() {
            aborted = true;
            synchronized (WorkPerformer.this) {
                WorkPerformer.this.notifyAll();
            }
        }
        
        public boolean isAborted() {
            boolean wasAborted = aborted;
            aborted = false;
            
            if (wasAborted) {
                return true;
            }
            
            if (pauzed) {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException ie) {
                    System.out.println(" INTERRUPTED ");
                }
            }
            
            wasAborted = aborted;
            aborted = false;
            
            return wasAborted;
        }
        
        private class ShowWorkRunnable implements Runnable {
            private Work work;
            
            public ShowWorkRunnable(Work work) {
                this.work = work;
            }
            
            public void run() {
                JProgressBar progressBar = getProgressBar();
                if (progressBar==null) return;
                
                threadPanel.setAbortEnabled(work.canAbort());
                threadPanel.setDescription(work.getDescription());
                
                if (totalAmount != Work.UNDETERMINED_AMOUNT) {
                    progressBar.setMaximum(totalAmount);
                    progressBar.setValue(0);
                } else {
                    progressBar.setIndeterminate(true);
                }
            }
        }
        
        private class ProgressUpdate implements Runnable {
            private Work work;
            private int totalAmount;
            
            public ProgressUpdate(Work work, int totalAmount) {
                this.work = work;
                this.totalAmount = totalAmount;
            }
            
            public void run() {
                int newTotalAmount = work.getTotalAmount();
                
                if (totalAmount != newTotalAmount) {
                    if (newTotalAmount != Work.UNDETERMINED_AMOUNT) {
                        if (totalAmount == Work.UNDETERMINED_AMOUNT) {
                            progressBar.setIndeterminate(false);
                        }
                        
                        progressBar.setMaximum(newTotalAmount);
                    } else {
                        progressBar.setIndeterminate(true);
                    }
                    
                    totalAmount = newTotalAmount;
                }
                if (totalAmount != Work.UNDETERMINED_AMOUNT) {
                    progressBar.setValue(work.getAmountDone());
                }
                
                String currentString = progressBar.getString();
                String newString = work.getDescription();
                if (currentString != null) {
                    if (!currentString.equals(newString)) {
                        progressBar.setString(newString);
                    }
                } else {
                    if (newString != null) {
                        progressBar.setString(newString);
                    }
                }
            }
        }
        
        public class PauzeListener implements ActionListener {
            JCheckBoxMenuItem pauzeBox;
            
            public PauzeListener(JCheckBoxMenuItem box) {
                this.pauzeBox = box;
            }
            
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (pauzeBox.isSelected()) {
                    pauzed = true;
                } else {
                    synchronized (WorkPerformer.this) {
                        pauzed = false;
                        WorkPerformer.this.notifyAll();
                    }
                }
            }
        }
    }
}
