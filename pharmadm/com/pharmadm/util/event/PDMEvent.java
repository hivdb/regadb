/*
 * PDMEvent.java
 *
 * Created on January 22, 2001, 4:57 PM
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
public class PDMEvent extends java.lang.Object {

    protected String name;
    protected String msg;
    
    /**
     * Constructs a new StarEvent object with the specified name and specified message
     *
     * @param	name	the name of the Event used to refer to it
     *		msg	the message used to pass extra info
     *
     * @pre     (name != null) and
     *		(msg  != null)
     *
     * @effect  (getName() = name) and 
     *		(getMsg() = msg)
     */
    public PDMEvent(String name, String msg) {
        this.name = name;
        this.msg = msg;
    }
    
    /**
     * Returns the name of the Event used to refer to it
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the message used to pass extra info
     */
    public String getMsg() {
        return msg;
    }
    
    /**
     * Returns a string representation of the Event
     */
    public String toString() {
        return "PDMEvent["+name+"|"+msg+"]";
    }

}
