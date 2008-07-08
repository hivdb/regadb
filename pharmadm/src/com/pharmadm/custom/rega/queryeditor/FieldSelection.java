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

import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;

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
    public FieldSelection(DbObject obj) {
        super(obj, obj);
    }
    
    public Object getObject(Object objectSpec) {
        DbObject spec = (DbObject)objectSpec;
        return spec.getField();
    }

	public boolean isValid() {
		DbObject object = (DbObject) getObjectSpec();
		if (object.getTable() == null) {
			return false;
		}
		if (object.getField() == null) {
			return false;
		}
		return true;
	}
}
