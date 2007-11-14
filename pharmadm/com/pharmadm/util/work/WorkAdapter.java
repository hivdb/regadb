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
 * A abstract adapter for Work with all inspectors implemented as properties.
 * This is a convenience class to allow implementation of the Work interface
 * with fewer lines of code.  It suffices to implement execute() and set the values 
 * of the properties where the default values are not fit for your application, 
 * especially totalAmount.
 *
 * The implementation of execute() is responsible for setting the amountDone
 * property as it achieves milestones , and regularly calling mayContinue().
 *
 * @author  kdg
 */
public abstract class WorkAdapter implements Work {
    
    private ContinuationArbiter arbiter;
    private String description = "Busy\u2026";
    private int totalAmount = Work.UNDETERMINED_AMOUNT;
    private int amountDone = 0;
    private boolean interruptible = true;
    private boolean abortable = true;
    private boolean pausable = true;
    private int initialGUITreshold = 40;
    
    public ContinuationArbiter getContinuationArbiter() {
        return arbiter;
    }
    
    /**
     * The WorkManager will call this method to designate a ContinuationArbiter 
     * to the Work before it executes the Work.  The Work must then use that 
     * arbiter regularly during execution to ask if it  may continue 
     * and to signal progress.
     *
     * @post The Work only calls mayContinue on the designated ContinuationArbiter.
     */
    public void setContinuationArbiter(ContinuationArbiter arbiter) {
        this.arbiter = arbiter;
    }
    
    /**
     * Returns a description of the work (to be shown in the GUI).
     * WorkManager will only call this method on the same thread as execute, 
     * so it is not required to make this method threadsafe with regard to execute.
     *
     * By default, returns the string "Busy…".
     */
    public String getDescription() {
        return description;
    }
    
    protected void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns the total amount of Work to be done.
     *
     * By default, returns UNDETERMINED_AMOUNT.
     */
    public int getTotalAmount() {
        return totalAmount;
    }
    
    /**
     * @pre totalAmount is UNDETERMINED_AMOUNT or a non-negative amount
     */
    protected void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    /**
     * Returns the amount of Work that has been done already.
     * Must return a positive integer value or UNDETERMINED_AMOUNT.
     */
    public int getAmountDone() {
        return amountDone;
    }
    
    /**
     * @pre amountDone is UNDETERMINED_AMOUNT or a non-negative amount
     */
    protected void setAmountDone(int amountDone) {
        this.amountDone = amountDone;
    }
    
    protected void increaseAmountDone() {
        this.amountDone++;
    }
    
    /**
     * Returns whether this work can cope with interrupts when an abort is requested.
     * If false, the Work will not receive interrupts from the WorkManager, and 
     * the only effect of an abort will be that mayContinue returns false.
     *
     * By default, returns true;
     */
    public boolean isInterruptible() {
        return interruptible;
    }
    
    protected void setInterruptible(boolean interruptible) {
        this.interruptible = interruptible;
    }
    
    /**
     * Returns whether this work can be aborted by the user.
     * This is only reflected in the GUI.  Programs can always (try to) abort a Work, 
     * regardless of the value of the abortable property.
     * Aborting a Work only has effect when the work calls mayContinue()
     * (which will return false) or while it is waiting (it will be interrupted).
     *
     * By default, returns true;
     */
    public boolean isAbortable() {
        return abortable;
    }
    
    protected void setAbortable(boolean abortable) {
        this.abortable = abortable;
    }
    
    /**
     * Returns whether this work can be paused by the user.
     * This is only reflected in the GUI.  Programs can always (try to) pause a Work, 
     * regardless of the value of the pausable property.
     * Aborting a Work only has effect when the work calls mayContinue(),
     * which will not return until the Work is unpaused.
     *
     * By default, returns true.
     */
    public boolean isPausable() {
        return pausable;
    }
    
    protected void setPausable(boolean pausable) {
        this.pausable = pausable;
    }
}
