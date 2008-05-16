/*
 * WorkManager.java
 *
 * Copyright 2005 PharmaDM. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * PharmaDM ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with PharmaDM.
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.work;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

/**
 * A class to manage the execution of Work instances, and show their progress to the user.
 * The WorkManager may execute its Works asynchronously, and they can be aborted or paused by the user.
 *
 * WorkManager is thread-safe.
 *
 * A Work may only be added to one WorkManager.
 * A Work may only be added once to a WorkManager; even waiting for completion is not sufficient for adding it again.
 *
 * @author  kdg
 */
public class WorkManager {
    private static boolean debug = false;
    
    // The list implementation must allow null.
    private LinkedList<Work> llist = new LinkedList<Work>();
    
    // the current number of runing threads
    private int nbThreads = 0;
    
    // how many threads are allowed to run simulaneously
    private int preferredNbThreads = 2;
    
    // the number of active works that are waiting for another (at least one) work in the queue.
    private int nbWaitingWorks = 0;
    private boolean prepareToQuit = false;
    private final JPanel panel;
    
    private int guiInitialTreshold = 200;
    private int guiUpdateTreshold = 70;
    
    // To find out which work performing thread to abort or pause
    // To access, synchronize on this WorkManager.
    // The map implementation must allow null.
    private final Map<Work, WorkPerformer> workToWorkPerformer = new HashMap<Work, WorkPerformer>();
    
    // To find out which replacement work to replace
    // To access, synchronize on this WorkManager.
    // The map implementation must allow null.
    private final Map<Work, WorkPerformer> replacementWorkToWorkPerformer = new HashMap<Work, WorkPerformer>();
    
    private final GlobalContinuationArbiter arbiter = new GlobalContinuationArbiter();
    
    /**
     * Creates new WorkManager.
     * If one provides a JPanel (recommended), it will be used to show the progress of the Works that are executed by this WorkManager.
     * If no JPanel is provided, Works will be executed silently.
     * WorkManager must be the sole owner of the JPanel, i.e. no one else may add or remove components.
     *
     * @param infoPanel  a JPanel where the WorkManager can show progress of its Works.
     */
    public WorkManager(JPanel infoPanel) {
        this.panel = infoPanel;
        if (panel != null) {
            panel.setLayout(new java.awt.GridBagLayout());
        }
    }
    
    /**
     * Is this WorkManager has a JPanel to show the progress of its Works.
     * If there is no panel, no progress is shown.
     */
    public boolean isGUIEnabled() {
        return panel != null;
    }
    
    /**
     * Aborts the Work ASAP if it is known to this WorkManager.
     * Any foreign thread may request the abort.
     * If the work is not yet executing, it is removed from the queue.
     * If the work's execution has already been completed or the work has not
     * been submitted, this method has no effect.
     *
     * @return true if the work was possibly not yet completed because of the abort.
     *         Note that, even if true is returned, the work might still have run to completion.
     *         If false is returned, the method is certain to have had no effect at all.
     */
    public synchronized boolean abort(Work work) {
        WorkPerformer performer = workToWorkPerformer.get(work);
        if (performer != null) {
            performer.abort(work);
            return true;
        } else {
            return llist.remove(work);
        }
    }
    
    /**
     * Aborts the Work ASAP if it is known to this WorkManager, and replace it by another work after it has stopped.
     * Any foreign thread may request the replacement.
     * If the obsolete work is not yet executing, it is removed from the queue.
     * If the obsolete work's execution has already been completed or the work has not
     * been submitted, this method has effect on the obsolete Work.
     * The replacement work will not start while the obsolete work is still running.
     *
     * @pre obsoleteWork != replacementWork
     * @pre replacementWork != null
     *
     * @return true iff the replacementWork will be executed (always returns true if addIfObsoleteNotPresent)
     */
    public synchronized boolean abortAndReplace(Work obsoleteWork, Work replacementWork, boolean addIfObsoleteNotPresent) {
        WorkPerformer performer = workToWorkPerformer.get(obsoleteWork);
        if (performer != null) {
            performer.abortAndReplace(obsoleteWork, replacementWork);
            replacementWorkToWorkPerformer.put(replacementWork, performer);
        } else {
            performer = replacementWorkToWorkPerformer.remove(obsoleteWork);
            if (performer != null) {
                replacementWorkToWorkPerformer.put(replacementWork, performer);
                performer.setReplacement(replacementWork);
            } else {
                boolean removed = llist.remove(obsoleteWork);
                if (removed) {
                    execute(replacementWork);
                } else {
                    if (addIfObsoleteNotPresent) {
                        execute(replacementWork);
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Pauses the Work ASAP if it is currently running in this WorkManager.
     * If the work is not yet executing, this method has no effect.
     * If the work's execution has already been completed or the work has not
     * been submitted, this method has no effect.
     */
    public synchronized void pause(Work work) {
        WorkPerformer performer = workToWorkPerformer.get(work);
        if (performer != null) {
            performer.setPaused(true);
        }
    }
    
    /**
     * Unpauses the Work if it is currently paused by this WorkManager.
     * If the work is not in paused status, this method has no effect.
     */
    public synchronized void unpause(Work work) {
        WorkPerformer performer = workToWorkPerformer.get(work);
        if (performer != null) {
            performer.setPaused(false);
        }
    }
    
    /**
     * The GUI will not be updated if less time has passed than guiUpdateTreshold.
     * The treshold is zero or more milliseconds.
     * It is a trade-off between a smooth flowing progress bar and not spending
     * too much CPU time into updating the GUI (as opposed to the real Work).
     */
    public int getGUIUpdateTreshold() {
        return guiUpdateTreshold;
    }
    
    /**
     * The treshold in milliseconds during which the GUI representation is not shown.
     * If a work takes less time than this and there was no progress GUI available yet,
     * the work will not be shown in the GUI at all.
     * The treshold is zero or more.
     * If the treshold is zero, the GUI components will be scheduled on the GUI thread
     * before the work is executed, but there is no guarantee when they will appear.
     */
    public int getInitialGUITreshold() {
        return guiInitialTreshold;
    }
    
    /**
     * Adds a Work to the queue.
     * The Work will be processed in another thread.
     * This method returns without waiting for the Work to be completed.
     * It is forbidden to add a work twice, even after
     * waiting for completion, unless the abortWork method is never called for that Work.
     * A Work may only be added to a single WorkManager.
     *
     * @pre work != null
     */
    public synchronized void execute(Work work) {
        work.setContinuationArbiter(arbiter);
        llist.add(work);
        
        if (debug) {
            System.out.println("*** ThreadMan: added work to schedule. " +
                    llist.size());
        }
        
        startThreadIfPreferred();
    }
    
    public void executeAndWait(Work work) {
        WorkWaitWrapper wrapperWork = new WorkWaitWrapper(work);
        boolean inWorkPerformer = (Thread.currentThread() instanceof WorkPerformer);
        
        if (inWorkPerformer) {
            synchronized (this) {
                nbWaitingWorks++;
            }
        }
        
        execute(wrapperWork);
        wrapperWork.waitForCompletion();
        
        if (inWorkPerformer) {
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
    public synchronized void executeAll(Collection<? extends Work> works) {
        for (Work work : works) {
            work.setContinuationArbiter(arbiter);
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
    public void executeAllAndWait(Collection<? extends Work> works) {
        List<WorkWaitWrapper> wrapperList = new ArrayList<WorkWaitWrapper>();
        boolean inWorkPerformer = (Thread.currentThread() instanceof WorkPerformer);
        for (Work work : works) {
            wrapperList.add(new WorkWaitWrapper(work));
        }
        
        if (inWorkPerformer) {
            synchronized (this) {
                nbWaitingWorks++;
            }
        }
        
        executeAll(wrapperList); // call to synchronized method
        
        for (WorkWaitWrapper wrapper : wrapperList) {
            wrapper.waitForCompletion();
        }
        
        if (inWorkPerformer) {
            synchronized (this) {
                nbWaitingWorks--;
            }
        }
    }
    
    // Clears the queue, and sends all threads a gentle signal to stop when they are ready to do so.
    // New jobs submissions are ignored.
    public void abortAll() {
        System.out.println("WorkManager: Sending the stop signal to all running operations.");
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
    private synchronized Work decideOnWorkOrThreadStop(WorkPerformer performer, Work oldWork) {
        boolean wantThreadStop = ((nbThreads > (preferredNbThreads + nbWaitingWorks)) || prepareToQuit);
        performer.interrupted(); // Clears the interrupt status.
        Work work = performer.getReplacement();
        if (work != null) {
            replacementWorkToWorkPerformer.remove(work);
        }
        if (work == null && !wantThreadStop && !llist.isEmpty()) {
            work = (Work)llist.removeFirst();
        }
        if (oldWork != null) {
            workToWorkPerformer.remove(oldWork);
        }
        if (work == null) {
            nbThreads--;
        } else {
            workToWorkPerformer.put(work, performer);
            performer.resetWorkStatus();
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
    
    private class WorkPerformer extends Thread {
        private volatile boolean paused = false;
        private volatile boolean aborted = false;
        private ThreadPanel threadPanel;   // only to be accessed from the GUI thread
        private JProgressBar progressBar;  // only to be accessed from the GUI thread
        private Work work = null;             // only to be accessed in this WorkPerformer Thread
        private long lastProgressUpdate = 0;  // only to be accessed in this WorkPerformer Thread
        private AbortListener abortListener;  // accessed in both the GUI and the WorkPerformer thread, but WorkPerformer is prudent
        private PauseListener pauseListener;  // accessed in both the GUI and the WorkPerformer thread, but WorkPerformer is prudent
        private Work replacementWork;  // only to be accessed when synchronized on WorkManager.this
        private Timer timer;  // only to be accessed in this WorkPerformer Thread
        private boolean guiCreated = false;  // only to be accessed in the GUI thread
        private volatile Work workShownByUI = null;   // accessed in the GUI thread and this WorkPerformer thread.
        private boolean guiDestroyed = false;  // only to be accessed in the GUI thread
        
        /**
         * Must be called in a synchronized method only because of the nbThreads increase.
         */
        public void start() {
            nbThreads++;
            this.setPriority(Thread.MIN_PRIORITY);
            super.start();
        }
        
        public void run() {
            work = decideOnWorkOrThreadStop(this, null);
            
            while (work != null) {
                if (debug) {
                    System.out.println("*** WorkPerformer: found some work to do.");
                    System.out.println("                   work: " + work.getClass().getName());
                }
                
                setName("Perform " + work.getDescription());
                
                if (isGUIEnabled()) {
                    int initialDelay = getInitialGUITreshold();
                    if (initialDelay == 0) {
                        EventQueue.invokeLater(new UIConstruction(work));
                    } else {
                        timer = new Timer(initialDelay, new UIConstruction(work));
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
                try {
                    work.execute();
                } catch (InterruptedException ie) {
                    // It's aborted.
                } catch (Exception e) {
                    e.printStackTrace();
                    if (isGUIEnabled()) {
                        stopTimer();
                        java.awt.Toolkit.getDefaultToolkit().beep();
                        final String description = work.getDescription();
                        final Work failedWork = work;
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                if (workShownByUI != failedWork) {
                                    new UIConstruction(failedWork).run();
                                }
                                threadPanel.setDescription("***CRASHED*** " + description);
                            }
                        });
                    } else {
                        java.awt.Toolkit.getDefaultToolkit().beep();
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                    }
                }
                // cancel the effort to construct the GUI if it's still not ready
                stopTimer();
                
                work = decideOnWorkOrThreadStop(this, work);
                if (isGUIEnabled()) {
                    if (work != null) {
                        // If they are visible to the current thread already, do it efficiently in the current thread,
                        // else, escalate it to the Swing thread where they are bound to be visible (unless the timer was stopped early).
                        if (abortListener != null && pauseListener != null) {
                            abortListener.setWork(work);
                            pauseListener.setWork(work);
                        } else {
                            final Work newWork = work;
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    if (abortListener != null) {  // either both exist, or neither (if the timer stopped early)
                                        abortListener.setWork(newWork);
                                        pauseListener.setWork(newWork);
                                    }
                                }
                            });
                        }
                    } else {
                        removeThreadPanelLater();
                    }
                }
            }
        }
        
        private void stopTimer() {
            if (timer != null && timer.isRunning()) {
                timer.stop();
                timer = null;
            }
        }
        
        /**
         * Updates the user interface, but only if the minimum threshold for intervals between updates has been reached.
         * Only to be called from this WorkPerformer thread.
         *
         * @pre this WorkPerformer must have a work in progress.
         */
        public void updateUIThrottled() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastProgressUpdate) > guiUpdateTreshold) {
                if (workShownByUI == work) {
                    EventQueue.invokeLater(new UIUpdate(work));
                    lastProgressUpdate = currentTime;
                }
            }
        }
        
        /**
         * Updates the user interface.
         * Only to be called from this WorkPerformer thread.
         *
         * @pre this WorkPerformer must have a work in progress.
         */
        public void updateUI() {
            if (workShownByUI == work) {
                EventQueue.invokeLater(new UIUpdate(work));
                lastProgressUpdate = System.currentTimeMillis();
            } else {
                // don't wait for the timer anymore, we'll do it immediately instead
                stopTimer();
                EventQueue.invokeLater( new Runnable() {
                    public void run() {
                        if (workShownByUI != work) {  // in case the timer-triggered construction was already running before we stopped the timer
                            new UIConstruction(work).run();
                        } else {
                            new UIUpdate(work).run();
                        }
                    }
                });
            }
        }
        
        private void abort(final Work work) {
            aborted = true;
            synchronized (WorkPerformer.this) {  // must synchronize, see contract of notifyAll
                WorkPerformer.this.notifyAll();
            }
            if (work.isInterruptible()) {
                this.interrupt();
            }
        }
        
        private void abortAndReplace(Work original, Work replacementWork) {
            setReplacement(replacementWork);
            abort(original);
        }
        
        private void setReplacement(Work replacementWork) {
            replacementWork.setContinuationArbiter(arbiter);
            this.replacementWork = replacementWork;
        }
        
        private Work getReplacement() {
            return replacementWork;
        }
        
        /**
         * Only to be called by decideOnWorkOrThreadStop(),
         * which assigns a new work while holding the WorkManager's lock.
         */
        private void resetWorkStatus() {
            aborted = false;
            paused = false;
            replacementWork = null;
            workShownByUI = null;
        }
        
        public boolean getAbortedAndWaitIfPaused() {
            if (aborted) {
                return true;
            }
            if (paused) {
                try {
                    synchronized (this) {  // must synchronize, see contract of wait
                        wait();
                        if (isGUIEnabled()) {
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    threadPanel.getToggleButtonPauze().setSelected(false);
                                }
                            });
                        }
                    }
                } catch (InterruptedException ie) {
                    System.out.println(" INTERRUPTED while paused");
                }
            }
            return aborted;
        }
        
        public void setPaused(boolean paused) {
            if (this.paused != paused) {
                if (paused) {
                    this.paused = true;
                } else {
                    synchronized (WorkPerformer.this) {  // must synchronize, see contract of notifyAll
                        this.paused = false;
                        WorkPerformer.this.notifyAll();
                    }
                }
            }
        }
        
        /**
         * @pre isGUIEnabled()
         */
        private void removeThreadPanelLater() {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    if (guiCreated) {
                        // Progress bar must be set to determinate state before removing it due to a flaw in JRE 1.4.
                        // Not doing this might result in an infinite loop of the background thread animating the indeterminate state.
                        JProgressBar progressBar = threadPanel.getProgressBar();
                        progressBar.setMaximum(1);
                        progressBar.setValue(1);
                        progressBar.setIndeterminate(false);
                        panel.remove(threadPanel);
                        panel.revalidate();
                        panel.repaint();
                        guiDestroyed = true;
                        workShownByUI = null;  // release reference for GC
                    }
                }
            });
        }
        
        /**
         * @pre EventQueue.isDispatchThread()
         * @pre isGUIEnabled()
         */
        private void addThreadPanelNow(ThreadPanel tPanel) {
            java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 2);
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.weightx = 1.0;
            panel.add(tPanel, gridBagConstraints);
            panel.revalidate();
            panel.repaint();
        }
        
        private class UIConstruction implements Runnable, ActionListener {
            private Work work;
            private Runnable progressUpdate;
            
            public UIConstruction(Work work) {
                this.work = work;
                this.progressUpdate = new UIUpdate(work);
            }
            
            /**
             * @pre EventQueue.isDispatchThread()
             * @pre isGUIEnabled()
             */
            public void actionPerformed(ActionEvent e) {
                run();
            }
            
            /**
             * @pre EventQueue.isDispatchThread()
             * @pre isGUIEnabled()
             */
            public void run() {
                if (!guiDestroyed) {
                    if (workShownByUI != work) {
                        if (!guiCreated) {
                            // The following causes a deadlock if not performed in the GUI thread.
                            // (E.g. the ThreadPanel constructor enters a monitor.)
                            threadPanel = new ThreadPanel();
                            pauseListener = new PauseListener(threadPanel.getToggleButtonPauze(), work);
                            abortListener = new AbortListener(work);
                            threadPanel.getToggleButtonPauze().addActionListener(pauseListener);
                            threadPanel.getButtonAbort().addActionListener(abortListener);
                            progressBar = threadPanel.getProgressBar();
                            addThreadPanelNow(threadPanel);
                            guiCreated = true;
                        }
                        threadPanel.setAbortEnabled(work.isAbortable());
                        threadPanel.setPauseEnabled(work.isPausable());
                        workShownByUI = work;
                    }
                    progressUpdate.run();
                }
            }
        }
        
        /**
         * A Runnable that, when run, updates the UI with the state of the Work as it was when the UIUpdate was constructed.
         * The UI must have been constructed and not been destroyed,
         * and no such destruction may have been scheduled on the Event Queue when this Runnable is scheduled.
         */
        private class UIUpdate implements Runnable {
            private int totalAmt;
            private int amtDone;
            private String desc;
            
            public UIUpdate(Work work) {
                this.totalAmt = work.getTotalAmount();
                this.amtDone = work.getAmountDone();
                this.desc = work.getDescription();
            }
            
            public void run() {
                boolean oldIndeterminate = progressBar.isIndeterminate();
                boolean newIndeterminate = ((totalAmt == -1) || (amtDone == -1));
                if (oldIndeterminate != newIndeterminate) {
                    progressBar.setIndeterminate(newIndeterminate);
                }
                if (!newIndeterminate) {
                    progressBar.setMaximum(totalAmt);
                    progressBar.setValue(amtDone);
                }
                progressBar.setString(desc);
            }
        }
    }
    
    private class PauseListener implements ActionListener {
        private AbstractButton toggleButton;
        private volatile Work work;
        
        public PauseListener(AbstractButton toggleButton, Work work) {
            this.toggleButton = toggleButton;
            this.work = work;
        }
        
        public void setWork(Work work) {
            this.work = work;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (toggleButton.isSelected()) {
                pause(work);
            } else {
                unpause(work);
            }
        }
    }
    
    private class AbortListener implements ActionListener {
        private volatile Work work;
        public AbortListener(Work work) {
            this.work = work;
        }
        
        public void setWork(Work work) {
            this.work = work;
        }
        
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            abort(work);
        }
    }
    
    /**
     * A ContinuationArbiter that can arbitrate for all Works managed by this WorkManager.
     */
    private class GlobalContinuationArbiter implements ContinuationArbiter {
        /**
         * A work must occasionally ask if it may continue.
         * This method is an alternative for mayContinue, but
         * never updates the GUI and is therefore faster.
         * When the user has pauzed the Work, this method may take an indefinite amount of time.
         * When the user has aborted the Work, this method returns false.
         * When this method returns false, then the Work must end execution ASAP.
         *
         * This method may only be called by a Work in its execute method,
         * in the Thread that the WorkManager uses to invoke the execution.
         *
         * @see mayContinue
         */
        public boolean mayContinueLight() {
            return !(((WorkPerformer) Thread.currentThread()).getAbortedAndWaitIfPaused() || prepareToQuit);
        }
        
        /**
         * A work must occasionally ask if it may continue its execution.
         * This allows the WorkManager to pause and handle the thread if needed.
         * When the user has pauzed the Work, this method may take an indefinite amount of time.
         * When the user has aborted the Work, this method returns false.
         * When this method returns false, then the Work must end execution ASAP.
         *
         * Note: one unit of work should be done.
         * This method updates the GUI (as opposed to mayContinueLight), unless
         * the GUI was updated during the last getGUIUpdateTreshold milliseconds, or unless
         * there were less than about getInitialGUITreshold milliseconds elapsed
         * since the work started execution.
         *
         * This method may only be called by a Work in its execute method,
         * in the Thread that the WorkManager uses to invoke the execution.
         */
        public boolean mayContinue() {
            ((WorkPerformer) Thread.currentThread()).updateUIThrottled();
            return mayContinueLight();
        }
        
        /**
         * Force the GUI to represent the current state of the work, regardless of
         * how long ago it was updated or how long the Work has been executing.
         * This also forces the executing Work to be visible in the GUI if it wasn't yet.
         *
         * Note that although the GUI update is guaranteed to occur and to display
         * the current state of the Work, it is scheduled in the event queue
         * and the method may (and often will) return before the update is on-screen.
         *
         * This method may only be called by a Work in its execute method,
         * in the Thread that the WorkManager uses to invoke the execution.
         */
        public void forceUIUpdate() {
            ((WorkPerformer) Thread.currentThread()).updateUI();
        }
    }
}