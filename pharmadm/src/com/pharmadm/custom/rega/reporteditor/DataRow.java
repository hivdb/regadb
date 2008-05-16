/*
 * DataRow.java
 *
 * Created on November 19, 2003, 1:41 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.HashMap;

/**
 *
 * @author  kristof
 */
public class DataRow {
    
    private HashMap valueMap = new HashMap();
    private Report report; 
    int index;
    
    /** Creates a new instance of DataRow */
    public DataRow(Report report, int idx) { 
        this.report = report;
        this.index = idx;
    }
    
    public int getIndex() {
        return index;
    }
    
    public Report getReport() {
        return report;
    }
    
    public Object getValue(DataOutputVariable ovar) {
        return valueMap.get(ovar);
    }
    
    public void setValue(DataOutputVariable ovar, Object value) {
        valueMap.put(ovar, value);
    }

}
