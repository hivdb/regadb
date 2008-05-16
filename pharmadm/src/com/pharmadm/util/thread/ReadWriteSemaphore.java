/*
 * ReadWriteLock.java
 *
 * Created on May 21, 2001, 2:52 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.thread;

/**
 * A standard solution to the reader/writers problem.
 * The writer must wait until no more readers are inside.
 * Only one writer may be inside.
 * Any number of readers may get inside. A new, additional reader may enter,
 * even if there's a writer waiting.
 *
 * More sophisticated locks can now be found in the Java standard library, such as java.util.concurrent.locks.ReentrantReadWriteLock
 *
 * @author  kdg
 * @version 1.0
 */
public class ReadWriteSemaphore {
    
    private int readCount;
    private Mutex mutex = new Mutex();
    private Mutex rwMutex = new Mutex();
    
    public void enterRead() {
        mutex.enter();
        readCount++;
        // first reader waits until writer has finished.
        if (readCount == 1) {
            rwMutex.enter();
        }
        mutex.leave();
    }
    
    public void leaveRead() {
        mutex.enter();
        readCount--;
        if (readCount == 0) {
            // last reader lets writer in while it leaves.
            rwMutex.leave();
        }
        mutex.leave();
    }
    
    public void enterWrite() throws InterruptedException {
        rwMutex.enter();
    }
    
    public void leaveWrite() {
        rwMutex.leave();
    }
}
