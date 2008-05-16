/*
 * DirtinessListener.java
 *
 * Created on December 15, 2003, 2:48 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.savable;

import java.util.EventListener;

/**
 *
 * @author  kdg
 */
public interface DirtinessListener extends EventListener {
    
    public void dirtinessChanged(DirtinessEvent de);
}
