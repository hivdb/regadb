/*
 * Work.java
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

/**
 * An interface that represents supervisable work that has to be done.
 *
 * Supervisable means that the execute() method frequently calls mayContinue()
 * to ask its supervising ContinuationArbiter, designated by the WorkManager,
 * whether it may continue.
 *
 * Note that a Work does not extend Thread or implement Runnable.
 * It is the task of the WorkManager to decide whether to run
 * the Work in a seperate Thread or not.
 *
 * @author  kdg
 */
public interface Work {
    
    public static final int UNDETERMINED_AMOUNT = -1;
    
    /**
     * Calling this method causes the work to be performed, similar to run() in Runnable.
     * The implementation of this method must regularly call mayContinue()
     * on its ContinuationArbiter, and terminate ASAP if it returns false.
     *
     * The Work may receive an InterruptedException if it is aborted during execution.
     * is free to throw an InterruptedException if it is interrupted
     * (wich will be ignored), or it may catch the exception and exit nicely.
     */
    public void execute() throws InterruptedException;
    
    /**
     * The WorkManager will call this method to designate a ContinuationArbiter 
     * to the Work before it executes the Work.  The Work must then use that 
     * arbiter regularly during execution to ask if it  may continue 
     * and to signal progress.
     * The Work must make sure that calls to the arbiter occur only in the execution Thread.
     *
     * @post The Work only calls mayContinue on the designated ContinuationArbiter.
     */
    public void setContinuationArbiter(ContinuationArbiter arbiter);
    
    /**
     * Returns a description of the work (to be shown in the GUI).
     * WorkManager will only call this method on the same thread as execute, 
     * so it is not required to make this method threadsafe with regard to execute.
     */
    public String getDescription();
    
    /**
     * Returns the total amount of Work to be done.
     * Note that the WorkManager expects an amount of work has been done
     * on each call of mayContinue().
     * Must return a positive integer value or UNDETERMINED_AMOUNT.
     * WorkManager will only call this method on the same thread as execute, 
     * so it is not required to make this method threadsafe with regard to execute.
     */
    public int getTotalAmount();
    
    /**
     * Returns the amount of Work that has been done already.
     * Must return a positive integer value or UNDETERMINED_AMOUNT.
     * WorkManager will only call this method on the same thread as execute, 
     * so it is not required to make this method threadsafe with regard to execute.
     */
    public int getAmountDone();
    
    /**
     * Returns whether this work can cope with interrupts when an abort is requested.
     * If false, the Work will not receive interrupts from the WorkManager, and 
     * the only effect of an abort will be that mayContinue returns false.
     */
    public boolean isInterruptible();
    
    /**
     * Returns whether this work can be aborted by the user.
     * This is only reflected in the GUI.  Programs can always (try to) abort a Work, 
     * regardless of the value of the abortable property.
     * Aborting a Work only has effect when the work calls mayContinue()
     * (which will return false) or while it is waiting (it will be interrupted).
     */
    public boolean isAbortable();
    
    /**
     * Returns whether this work can be paused by the user.
     * This is only reflected in the GUI.  Programs can always (try to) pause a Work, 
     * regardless of the value of the pausable property.
     * Aborting a Work only has effect when the work calls mayContinue(),
     * which will not return until the Work is unpaused.
     */
    public boolean isPausable();
}
