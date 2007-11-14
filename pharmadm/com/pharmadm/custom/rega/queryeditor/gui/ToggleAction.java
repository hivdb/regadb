/*
 * ToggleAction.java
 *
 * Created on October 30, 2003, 5:42 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui;

import java.util.*;
import javax.swing.AbstractAction;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An action that toggles a boolean value on and off.
 *
 * @author  kdg
 */
public class ToggleAction extends AbstractAction {
    
    private boolean state;
    private List listeners = new ArrayList();
    
    /** Creates a new instance of ToggleAction */
    public ToggleAction(String name, boolean initialState) {
        super(name);
        this.state = initialState;
    }
    
    public boolean getState() {
        return state;
    }
    
    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Toggles the state and notifies all listeners.
     */
    private void toggleState(Object source) {
        state = !state;
        Iterator iterListeners = listeners.iterator();
        if (source == null) {
            source = this;
        }
        ChangeEvent changeEvent = new ChangeEvent(source);
        while (iterListeners.hasNext()) {
            ChangeListener listener = (ChangeListener)iterListeners.next();
            listener.stateChanged(changeEvent);
        }
    }
    
    /**
     * Toggles the state.
     * Subclasses overriding this method should still call super.actionPerformed first.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        toggleState(e.getSource());
    }
}
