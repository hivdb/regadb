/*
 * NewClass.java
 *
 * Created on June 15, 2005, 2:31 PM
 *
 * (C) PharmaDM n.v.  All rights reserved.
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.work;

/**
 * A decorator for Work that allows to wait for its completion.
 *
 * @pattern decorator
 * @author kdg
 */
class WorkWaitWrapper implements Work {
    private Work work;
    private boolean done = false;
    
    public WorkWaitWrapper(Work work) {
        this.work = work;
    }
    
    public void execute() throws InterruptedException {
        try {
            work.execute();
        } finally {
            signalCompletion();
        }
    }
    
    public int getTotalAmount() {
        return work.getTotalAmount();
    }
    
    public void setContinuationArbiter(ContinuationArbiter arbiter) {
        work.setContinuationArbiter(arbiter);
    }
    
    // It is important for these methods to be synchronized.
    private synchronized void signalCompletion() {  // must synchronize, see contract of notifyAll
        done = true;
        notifyAll();
    }
    
    public synchronized void waitForCompletion() {  // must synchronize, see contract of wait
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
    
    public boolean isInterruptible() {
        return work.isInterruptible();
    }
    
    public boolean isAbortable() {
        return work.isAbortable();
    }
    
    public boolean isPausable() {
        return work.isPausable();
    }
}
