/*
 * DirtinessEvent.java
 *
 * Created on December 15, 2003, 2:49 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.savable;

/**
 *
 * @author  kdg
 */
public class DirtinessEvent {
    
    private final Savable savable;
    
    /** Creates a new instance of DirtinessEvent */
    public DirtinessEvent(Savable savable) {
        this.savable = savable;
    }
    
    public Savable getSavable() {
        return savable;
    }
}
