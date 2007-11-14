/*
 * ContinuationArbiter.java
 *
 * Created on June 13, 2005, 11:48 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.work;

/**
 * An interface for a class designated to decide whether a Work may continue or not.
 * While taking a decision to continue or not, the ContinuationArbiter may inspect 
 * the state of the Work, and it may schedule an update of the UI to display any progress made.
 *
 * @author kdg
 */
public interface ContinuationArbiter {
    
    /**
     * A work must occasionally ask if it may continue.
     * This method is an alternative for mayContinue, but
     * never updates the UI and is therefore faster.
     * When the user has pauzed the Work, this method may take an indefinite amount of time.
     * When the user has aborted the Work, this method returns false.
     * When this method returns false, then the Work must end execution ASAP.
     *
     * This method may only be called by a Work in its execute method,
     * in the Thread that is used to invoke the execution.
     *
     * @see mayContinue
     */
    public boolean mayContinueLight();
    
    /**
     * A work must occasionally ask if it may continue its execution.
     * This allows the WorkManager to pause and handle the thread if needed.
     * When the user has pauzed the Work, this method may take an indefinite amount of time.
     * When the user has aborted the Work, this method returns false.
     * When this method returns false, then the Work must end execution ASAP.
     *
     * Note: if it is certain that the state of the Work has not changed, 
     * it is better to call mayContinueLight for improved efficiency.
     *
     * This method updates the UI (as opposed to mayContinueLight), unless
     * the UI was updated during the last getUIUpdateTreshold milliseconds, or unless
     * there were less than about getInitialUITreshold milliseconds elapsed
     * since the work started execution.
     *
     * This method may only be called by a Work in its execute method,
     * in the Thread that is used to invoke the execution.
     */
    public boolean mayContinue();
    
    /**
     * Force the UI to represent the current state of the work, regardless of
     * how long ago it was updated or how long the Work has been executing.
     * This also forces the executing Work to be visible in the UI if it wasn't yet.
     *
     * Note that although the UI update is guaranteed to occur and to display
     * the current state of the Work, it is scheduled in the event queue
     * and the method may (and often will) return before the update is on-screen.
     *
     * This method may only be called by a Work in its execute method,
     * in the Thread that is used to invoke the execution.
     */
    public void forceUIUpdate();
}
