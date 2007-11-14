/*
 * ReportFormat.java
 *
 * Created on November 20, 2003, 8:20 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;
import com.pharmadm.custom.rega.queryeditor.UniqueNameContext;

/**
 *
 * @author  kristof
 */
/*
 * This class supports xml-encoding. The following properties are encoded :
 *  dataGroups
 *  selectionList
 *  uniqueNameContext
 */
public class ReportFormat {
    
    private List dataGroups = new ArrayList(); // of type DataGroup
    private DataSelectionList selectionList;
    private UniqueNameContext uniqueNameContext = new UniqueNameContext();
    
    /** Creates a new instance of ReportFormat */
    public ReportFormat() {
        setSelectionList(new DataSelectionList(this));
    }
    
    public UniqueNameContext getUniqueNameContext() {
        return uniqueNameContext;
    }
    public void setUniqueNameContext(UniqueNameContext context) {
        this.uniqueNameContext = context;
    }
    
    public List getDataGroups() {
        return dataGroups;
    }
    
    /* For xml-encoding purposes */
    public void setDataGroups(List dataGroups) {
        this.dataGroups = dataGroups;
        Iterator iter = dataGroups.iterator();
        while (iter.hasNext()) {
            DataGroup group = (DataGroup)iter.next();
            group.setReportFormat(this);
        }
    }
    
    public void addDataGroup(DataGroup group, int index) {
        dataGroups.add(index, group);
        if (uniqueNameContext != null) {
            uniqueNameContext.assignUniqueNamesToOutputs(group);
        }
        group.setReportFormat(this);
    }
    
    public void addDataGroup(DataGroup group) {
        dataGroups.add(group);
        if (uniqueNameContext != null) {
            uniqueNameContext.assignUniqueNamesToOutputs(group);
        }
        group.setReportFormat(this);
    }
    
    public void removeDataGroup(DataGroup group, int index) {
        if (dataGroups.get(index).equals(group)) {
            dataGroups.remove(index);
        }
        group.setReportFormat(null);
    }
    
    public void removeDataGroup(DataGroup group) {
        dataGroups.remove(group);
        group.setReportFormat(this);
    }
    
    public int getIndexOf(DataGroup group) {
        return dataGroups.indexOf(group);
    }
    
    public Collection getOutputVariablesAvailableForImport(int index) {
        ArrayList res = new ArrayList();
        for (int i = 0; i < index ; i++) {
            Collection tmpRes = ((DataGroup)getDataGroups().get(i)).getDataOutputVariables();
            Iterator iter = tmpRes.iterator();
            while (iter.hasNext()) {
                DataOutputVariable ovar = (DataOutputVariable)iter.next();
                if (ovar instanceof ListDataOutputVariable) {
                    Iterator elemIter = ((ListDataOutputVariable)ovar).getIterator();
                    while (elemIter.hasNext()) {
                        res.add(elemIter.next());
                    }
                } else {
                    res.add(ovar);
                }
            }
            
        }
        return res;
    }
    
    public Collection getDataOutputVariables() {
        ArrayList res = new ArrayList();
        for (int i = 0; i < getDataGroups().size() ; i++) {
            res.addAll(((DataGroup)getDataGroups().get(i)).getDataOutputVariables());
        }
        return res;
    }
    
    public Collection getObjectListVariables() {
        ArrayList res = new ArrayList();
        for (int i = 0; i < dataGroups.size(); i++) {
            res.addAll(((DataGroup)getDataGroups().get(i)).getObjectListVariables());
        }
        return res;
    }
    
    public boolean isValid() {
        for (int i = 0; i < dataGroups.size() ; i++) {
            if (! ((DataGroup)getDataGroups().get(i)).isValid()) {
                return false;
            }
        }
        return true;
    }
    
    public DataSelectionList getSelectionList() {
        return selectionList;
    }
    
    public void setSelectionList(DataSelectionList selectionList) {
        this.selectionList = selectionList;
    }
}
