
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
public abstract class Variable {
    
    ///////////////////////////////////////
    // associations
    
    /**
     * <p>
     *
     * </p>
     */
    private VariableType variableType;
    
    /** For xml-encoding purposes only */
    public Variable() {
    }
    
    public Variable(VariableType type) {
        this.variableType = type;
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public VariableType getVariableType() {
        return variableType;
    }
    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }
    
} // end Variable



