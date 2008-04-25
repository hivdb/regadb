/*
 * FieldSelection.java
 *
 * Created on September 5, 2003, 10:47 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;

/**
 *
 * @author  kristof
 *
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */

public class FieldSelection extends SimpleSelection implements Serializable{
    
    /** Creates a new instance of FieldSelection */
    public FieldSelection(Field field) {
        super(new String[] {field.getName(), field.getTable().getName()});
    }
    
    public Object getObject(Object objectSpec) {
        // the specification in this case is a String[2] array, containing the name
        // of the field and the name of the table it belongs to; 
        // in general, no new field or table should be created : rather, they should 
        // be looked up in the (currently active) database table catalog
        String[] spec = (String[])objectSpec;
        Table table = DatabaseManager.getInstance().getTableCatalog().getTable(spec[1]);
        Field field = table.getField(spec[0]);
        return field;
    }
}
