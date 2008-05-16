/*
 * ReadWriteManySemaphore.java
 *
 * Created on May 21, 2001, 5:04 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.thread;

/**
 * A solution to a modified reader/writers problem. 
 *
 * Modification: many writers may write in parallel.
 *
 * The writers must wait until no more readers are inside, and vice versa.
 * Any number of readers or writers may be inside.
 * A new, additional reader may enter, 
 * even if there are writers waiting, and vice versa.
 *
 * @author  kdg
 * @version 1.0
 */
public class ReadWriteManySemaphore extends Object {

    private int readCount;
    private int writeCount;
    private Mutex readMutex = new Mutex();
    private Mutex writeMutex = new Mutex();
    private Mutex dualMutex = new Mutex();

    public void enterRead() throws InterruptedException {
        readMutex.enter();
        readCount++;
        // first reader waits until writers have finished.
        if (readCount == 1) {
            dualMutex.enter();
        }
        readMutex.leave();
    }
    
    public void leaveRead() {
        readMutex.enter();
        readCount--;
        if (readCount == 0) {
            // last reader lets writer in while it leaves.
            dualMutex.leave();
        }
        readMutex.leave();
    }
    
    public void enterWrite() throws InterruptedException {
        writeMutex.enter();
        writeCount++;
        // first writer waits until readers have finished.
        if (writeCount == 1) {
            dualMutex.enter();
        }
        writeMutex.leave();
    }
    
    public void leaveWrite() {
        writeMutex.enter();
        writeCount--;
        if (writeCount == 0) {
            // last writer lets reader in while it leaves.
            dualMutex.leave();
        }
        writeMutex.leave();
    }
}
