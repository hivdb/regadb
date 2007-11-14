/*
 * Valuable.java
 *
 * Created on December 11, 2003, 12:11 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

/**
 *
 * @author  kristof
 */
public interface Valuable {
    
    public Object getValue(DataRow row);
    
}
