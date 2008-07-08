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
public class POJOListSelection extends com.pharmadm.custom.rega.queryeditor.ComposedSelection {
    
    /** Creates a new instance of POJOSelection */
    public POJOListSelection(ListDataOutputVariable ovar) {
        super(ovar, ovar.getObject());
        ElementDataOutputVariable ovar0 = ovar.getElement();
        if (ovar0.hasDomainClassType()) {
            initPropertySelections();
        } else {
            System.err.println("POJOListSelection's DataOutputVariable Element is not of a domain class type. Something is terribly wrong !");
        }
    }
    
    public POJOListSelection(ListDataOutputVariable ovar, boolean selected) {
        this(ovar);
        setSelected(selected);
    }
    
    public Object getObject(Object objectSpec) {
        // the object (a DataOutputVariable) is specified by itself
        return (DataOutputVariable)objectSpec;
    }
    
    
    //public String getVariableName() {
    //    return ((DataOutputVariable)getObject()).getUniqueName();
    //}
    
    // property selections apply to an entire list of element DataOutputVariables;
    // which properties are relevant is decided based on reflection on the template element
    private void initPropertySelections() {
        ArrayList propertySelections = new ArrayList();
        Iterator propIter = ((ListDataOutputVariable)getObject()).getElement().getProperties().iterator();
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

	@Override
	public boolean isValid() {
		return true;
	}
}
