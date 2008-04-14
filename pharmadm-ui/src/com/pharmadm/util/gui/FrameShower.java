/*
 * FrameShower.java
 *
 * Created on December 10, 2003, 10:20 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui;

import java.awt.EventQueue;
import java.awt.Frame;

/**
 * Static utility class.
 *
 * It is highly recommended to use showFrameLater for showing jframes 
 * from outside the event dispatching thread.
 *
 * @see http://java.sun.com/developer/TechTips/txtarchive/2003/Dec03_JohnZ.txt
 *
 * @author  kdg
 */
public class FrameShower {
    
    /**
     * Instantiation is not needed.
     */
    private FrameShower() {
    }
    
    /**
     * Shows the frame on the event dispatcher thread.
     */
    public static void showFrameLater(Frame frame) {
        FrameShowerRunnable frameShowerRunnable = new FrameShowerRunnable(frame);
        EventQueue.invokeLater(frameShowerRunnable);
    }
    
    private static class FrameShowerRunnable implements Runnable {
        private final Frame frame;
        
        public FrameShowerRunnable(Frame frame) {
            this.frame = frame;
        }
        
        public void run() {
            frame.show();
        }
    }
}
