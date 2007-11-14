/*
 * Timer.java
 *
 * Created on May 12, 2005, 2:02 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util;

/**
 * Not threadsafe.
 *
 * @author kdg
 */
public class SimpleTimer {
    
    private boolean running = false;
    private long timeStarted;
    private long timeElapsed;
    private long totalTimeElapsed;
    
    /** Creates a new instance of SimpleTimer */
    public SimpleTimer() {
    }
    
    public SimpleTimer start() {
        running = true;
        timeStarted = System.currentTimeMillis();
        return this;
    }
    
    /**
     * Returns the milliseconds elapsed since the last start.
     */
    public long stop() {
        if (running) {
            long timeStopped = System.currentTimeMillis();
            timeElapsed = timeStopped - timeStarted;
            totalTimeElapsed += timeElapsed;
            running = false;
        }
        return timeElapsed;
    }
    
    /**
     * Gets the time elapsed during the interval that was last stopped,
     * or zero if no interval has been stopped yet.
     */
    public long getIntervalElapsedMilliSeconds() {
        return timeElapsed;
    }
    
    public long getTotalElapsedMilliSeconds() {
        return totalTimeElapsed;
    }
    
    public long getIntervalElapsedSeconds() {
        return timeElapsed / 1000;
    }
    
    public long getTotalElapsedSeconds() {
        return totalTimeElapsed / 1000;
    }
    
    public void reset() {
        running = false;
        timeElapsed = 0;
        totalTimeElapsed = 0;
    }
}
