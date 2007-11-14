/*
 * EventList.java
 *
 * Created on March 7, 2001, 2:10 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.event;

import java.util.ArrayList;

/**
 *
 * @author  kdg
 * @version 2.0
 *
 * @history
 *     1.0  Original version
 *     2.0  Uses Weak References.
 */

class EventList extends ArrayList {
    
    protected boolean m_enabled = true;
    protected String m_name;
    
    public EventList(String name) {
        super();
        m_name = name;
    }
    
    public void setEnabled(boolean enable) {
        m_enabled = enable;
    }
    
    public boolean isEnabled() {
        return m_enabled;
    }
    
    public String getName() {
        return m_name;
    }
    
}