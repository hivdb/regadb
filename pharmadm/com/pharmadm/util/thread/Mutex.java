/*
 * BinaryMutex.java
 *
 * Created on May 21, 2001, 2:42 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.thread;

/**
 * A binary semaphore (= mutex).
 *
 * Since JDK 1.5, one can also use java.util.concurrent.Semaphore.
 *
 * @author  kdg
 * @version 1.0
 */
public class Mutex extends Object {
    
    private boolean lock = false;
    
    /** Creates new Mutex */
    public Mutex() {
    }
    
    public synchronized void enter() {
        try {
            if (lock) wait();
        } catch (InterruptedException ie) {
            System.err.println("ERROR: Unexpected interruption during mutex wait!!!");
            ie.printStackTrace();
        }
        lock = true;
    }
    
    public synchronized void leave() {
        lock = false;
        notify();
    }
}
