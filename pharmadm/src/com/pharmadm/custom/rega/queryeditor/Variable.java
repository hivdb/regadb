
/** Java class "Variable.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
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
 * <p>
 * Represents an item that has a type.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  variableType
 * </p>
 */
public abstract class Variable implements Serializable {
    
    ///////////////////////////////////////
    // associations
    

    private DbObject object;
    
    /** For xml-encoding purposes only */
    public Variable() {
    }
    
    public Variable(DbObject object) {
        this.setObject(object);
    }

	private void setObject(DbObject object) {
		this.object = object;
	}

	public DbObject getObject() {
		return object;
	}

    
} // end Variable



