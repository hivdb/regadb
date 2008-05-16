/*
 * DefaultProgress.java
 *
 * Created on September 22, 2003, 3:08 PM
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
public class DefaultProgress implements Progress {
    
    private String units;
    private int amountDone;
    private int totalAmount;
    
    /** Creates a new instance of DefaultProgress */
    public DefaultProgress(int totalAmount, String units) {
        this.totalAmount = totalAmount;
        this.units = units;
    }
    
    public int getAmountDone() {
        return amountDone;
    }
    
    public int getTotalAmount() {
        return totalAmount;
    }
    
    public String getUnits() {
        return units;
    }
    
}
