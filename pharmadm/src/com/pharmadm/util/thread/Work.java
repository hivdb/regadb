/*
 * Work.java
 *
 * Created on March 20, 2001, 3:13 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.thread;

/**
 * An interface that represents supervisable work that has to be done.
 *
 * Supervisable means that the execute() method frequently calls mayContinue()
 * to ask its supervison ThreadManager whether it may continue.
 *
 * Note that a Work does not extend Thread or implement Runnable.
 * It is the task of the ThreadManager to decide whether to run
 * the Work in a seperate Thread or not.
 *
 * Deprecated, use com.pharmadm.util.work.Work instead.
 *
 * @deprecated
 * @author  kdg
 */
public interface Work {
    
    public static final int UNDETERMINED_AMOUNT = -1;
    
    /**
     * Returns a description of the work (to be shown in the GUI).
     */
    public String getDescription();
    
    /**
     * Returns the total amount of Work to be done.
     * Note that the ThreadManager expects an amount of work has been done
     * on each call of mayContinue().
     * Must return a positive integer value or UNDETERMINED_AMOUNT
     */
    public int getTotalAmount();
    
    /**
     * Returns the amount of Work that has been done already.
     */
    public int getAmountDone();
    
    
    /**
     * Calling this method causes the work to be done.
     * The implementation of this method must regularly call mayContinue()
     * on its current ThreadManager.
     */
    public void execute();
    
    /**
     *
     * This method may only be called by a ThreadManager. Calls happen in two circumstances:
     * 1. when the work is first enqueued with a ThreadManager.
     * 2. The ThreadManager may give its work to another TreadManager.  It must notify
     *    the Work using this method.
     *    (A TreadManager will normally do this during a call to mayContinue() because,
     *    at that time it has control and synchronization is easiest.)
     *
     * @post The Work calls mayContinue on the ThreadManager it belongs to.
     */
    public void setThreadManager(ThreadManager manager);
    
    /**
     * Returns whether this work can be aborted.
     */
    public boolean canAbort();
}
