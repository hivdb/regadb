/*
 * PropertySelection.java
 *
 * Created on November 28, 2003, 6:49 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

/**
 * @author  kristof
 * 
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 * 
 */
public class PropertySelection extends com.pharmadm.custom.rega.queryeditor.SimpleSelection {
    
    /** Creates a new instance of PropertySelection */
    public PropertySelection(Property property) {
        super(new Object[]{property.getName(), property.getOutputVariable()});
    }
    
    public PropertySelection(Property property, boolean selected) {
        this(property);
        setSelected(selected);
    }
    
    public Object getObject(Object objectSpec) {
        Object[] spec = (Object[])objectSpec;
        return ((DataOutputVariable)spec[1]).getProperty((String)spec[0]);
    }
    
}
