/*
 * OutputSelection.java
 *
 * Created on September 5, 2003, 10:53 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;

/**
 *
 * @author  kristof
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class OutputSelection extends SimpleSelection implements Serializable {
    
    /** Creates a new instance of OutputSelection */
    public OutputSelection(OutputVariable ovar) {
        super(ovar, ovar.getObject());
    }
    
    /** Creates a new instance of OutputSelection */
    public OutputSelection(OutputVariable ovar, boolean selected) {
        super(ovar, ovar.getObject(), selected);
    }
    
    public Object getObject(Object objectSpec) {
        // the object (an OutputVariable) is specified by itself
        return (OutputVariable)objectSpec;
    }
}
