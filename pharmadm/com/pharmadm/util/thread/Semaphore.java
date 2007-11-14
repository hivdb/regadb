/*
 * Semaphore.java
 *
 * Created on November 7, 2002, 4:44 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.thread;

/**
 * The semaphore synchronization primitive.
 * Since Java 1.5, you can use java.util.concurrent.Semaphore instead.
 *
 * @author  kdg
 */
public class Semaphore {
    
    private int value;
    
    /** 
     * Creates a new instance of Semaphore 
     *
     * @pre initialValue must be >= 0.
     */
    public Semaphore(int initialValue) {
        this.value = initialValue;
    }
    
    /**
     * This method is also known as:
     *         Tanenbaum   down()
     * original Dijkstra   P() 
     *             POSIX   sem_wait() 
     *      Silberschatz   wait() 
     */
    public synchronized void down() {
        try {
            while (value <= 0) {
                wait();
            }; 
            value--;
        } catch (InterruptedException ie) {
            System.err.println("ERROR: Unexpected interruption during semaphore wait!!!");
            ie.printStackTrace();
        }
    }
    
    /**
     * This method is also known as:
     *         Tanenbaum   up()
     * original Dijkstra   V() 
     *             POSIX   sem_post() 
     *      Silberschatz   signal() 
     */
    public synchronized void up() {
        value++;
        notify();
    }    

    /**
     * Same effect as calling up() n times, except that only one notify is fired.
     * Thus, only one thread will be woken up.
     *
     * @param n The value with wich to increase the semaphore value with.
     * @pre (n > 0)
     */
    public synchronized void up(int n) {
        value += n;
        notify();
    }    
}
