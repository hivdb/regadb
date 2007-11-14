
/** Java class "VariableType.java" generated from Poseidon for UML.
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

import java.util.*;

/**
 * <p>
 * A VariableType determines the meaningfulness of associations between
 * Variables: only Variables that are of compatible types can be associated
 * with each other.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  name
 * </p>
 */
public class VariableType {
    
    private static boolean typeStringEquivalent(String type1, String type2) {
        if ((type1 == null) || (type2 == null)) {
            return (type1 == type2);
        } else {
            return normalize(type1).equals(normalize(type2));
        }
    }
    
    private static String normalize(String origString) {
        return origString.toUpperCase().replaceAll("_", "");
    }
    
    private String name;
    
    /**
     * @pre name != null
     */
    public VariableType(String name) {
        this.name = name;
    }
    
    /* For xml-encoding purposes only */
    public VariableType() {
    }
    
    public String getName() {
        return name;
    }
    
    /* For xml-encoding purposes only */
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (obj instanceof VariableType) {
            return typeStringEquivalent(this.getName(), ((VariableType)obj).getName());
        } else {
            return false;
        }
    }
    
    ///////////////////////////////////////
    // operations
    
    
    /**
     * <p>
     * Determines whether an OutputVariable of the given type is compatible
     * with an InputVariable of this VariableType.
     * </p>
     * <p>
     *
     * @param outputType The type of the OutputVariable
     * </p>
     * <p>
     * @return true iff an OutputVariable of the parameter type is compatible
     * with an InputVariable of this VariableType.
     * </p>
     */
    public boolean isCompatibleType(VariableType outputType) {
        return equals(outputType);
    }
    
    /*
     * return the Class associated with this VariableType
     */
    public Class getValueType() {
        if (name.equalsIgnoreCase("Date")) {
            return java.util.Date.class;
        } else if (name.equalsIgnoreCase("String")) {
            return java.lang.String.class;
        } else {
            try {
                return Class.forName("com.pharmadm.custom.rega.domainclasses." + name);
            } catch (Exception e) {
                return null;
            }
        }
    }
    
} // end VariableType




