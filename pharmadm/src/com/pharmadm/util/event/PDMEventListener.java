/*
 * PDMEventListener.java
 *
 * Created on January 24, 2001, 9:41 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.event;

/**
 *
 * @author  kdg
 * @version 1.0
 */
public interface PDMEventListener {

    
    /**
     * Is called when an event occurs at a certain EventManager and the class 
     * implementing the interface is subscribed there for the type of Event 
     * (Event name).
     *
     * @param	e	the event that caused the method to be called
     */
    public void processEvent(PDMEvent e);
        
}

