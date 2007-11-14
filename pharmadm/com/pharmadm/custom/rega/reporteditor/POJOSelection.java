/*
 * POJOSelection.java
 *
 * Created on November 28, 2003, 6:48 PM
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
 * 
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class POJOSelection extends com.pharmadm.custom.rega.queryeditor.ComposedSelection {
    
    /** Creates a new instance of POJOSelection */
    public POJOSelection(DataOutputVariable ovar) {
        super(ovar);
        if (ovar.hasDomainClassType()) {
            initPropertySelections();
        } else {
            System.err.println("POJOSelection's DataOutputVariable is not of a domain class type. Something is terribly wrong !");
        }
    }
    
    public POJOSelection(DataOutputVariable ovar, boolean selected) {
        this(ovar);
        setSelected(selected);
    }
    
    public Object getObject(Object objectSpec) {
        // the object (a DataOutputVariable) is specified by itself
        return (DataOutputVariable)objectSpec;
    }
    
    
    public String getVariableName() {
        return ((DataOutputVariable)getObject()).getUniqueName();
    }
    
    private void initPropertySelections() {
        ArrayList propertySelections = new ArrayList();
        Iterator propIter = ((DataOutputVariable)getObject()).getProperties().iterator();
        while (propIter.hasNext()) {
            Property property = (Property)propIter.next();
            PropertySelection propertySelection = new PropertySelection(property);
            if (property.isId()) { 
                propertySelection.setSelected(true);
            }
            propertySelections.add(propertySelection);
        }
        setSubSelections(propertySelections);
    }
}
