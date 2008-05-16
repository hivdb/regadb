/*
 * Progress.java
 *
 * Created on September 19, 2003, 6:44 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.thread;

/**
 *
 * @author  kdg
 */
public interface Progress {
    
    public static final int UNDETERMINED_AMOUNT = -1;
        
    /**
     * Returns the total amount of work to be done.
     * Must return a positive integer value or UNDETERMINED_AMOUNT
     */
    public int getTotalAmount();

    /**
     * Returns the amount of Work that has been done already.
     */
    public int getAmountDone();
 
    /**
     * Return a description of the units indicated in getAmountDone and getTotalAmount.
     * Plural form.
     */
    public String getUnits();
}
