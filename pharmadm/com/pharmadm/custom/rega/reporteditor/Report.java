/*
 * Report.java
 *
 * Created on November 21, 2003, 3:37 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;

/**
 *
 * @author  kristof
 */
public class Report {
    
    private ReportFormat format;
    private List dataRows = null;
    private int dataSize = -1;
    private HashMap listAssignments = new HashMap();
    
    /** Creates a new instance of Report */
    public Report() {
        format = new ReportFormat();
    }
 
    public ReportFormat getFormat() {
        return this.format;
    }
    
    public void setFormat(ReportFormat format) {
        this.format = format;
    }
    
    public int getDataSize() {
        return dataSize;
    }
    
    public List getDataRows() {
        return dataRows;
    }
    
    public List getList(ObjectListVariable olvar) {
        return (List)listAssignments.get(olvar);
    }
    
    public boolean isSeeded() {
        Iterator oLVarIt = getFormat().getObjectListVariables().iterator();
        while (oLVarIt.hasNext()) {
            if (getList((ObjectListVariable)oLVarIt.next()) == null) { 
                return false;
            }
        }
        return true;
    }
    
    public void seedObjectList(List objectList, ObjectListVariable objListVar) {
        if (dataSize == -1) {
            listAssignments.put(objListVar, objectList);
            dataSize = objectList.size();
        } else if (dataSize == objectList.size()) {
            listAssignments.put(objListVar, objectList);
        } else {
            System.err.println("Error : Trying to assign object lists of different lengths (was : " +  dataSize + ", now : " + objectList.size() + ") to columns of the same report.");
        }
    }
    
    public void reset() {
        listAssignments.clear();
        dataSize = -1;
    }
    
    public void calculateRows() {
        if (! isSeeded()) {
            System.err.println("Can not calculate report, not all inputs have been specified.");
            return;
        }
        dataRows = new ArrayList();
        for (int i = 0; i < getDataSize(); i++) {
            dataRows.add(new DataRow(this, i));
        }
        Iterator iter = getFormat().getDataGroups().iterator();
        while (iter.hasNext()) {
            ((DataGroup)iter.next()).updateRows(dataRows);
        }
    }
    
    
    
}
