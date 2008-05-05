
/** Java class "FixedString.java" generated from Poseidon for UML.
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

import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;
import com.pharmadm.custom.rega.reporteditor.DataGroupWord;
import com.pharmadm.custom.rega.reporteditor.DataRow;
import com.pharmadm.custom.rega.reporteditor.OutputReportSeeder;
import com.pharmadm.custom.rega.reporteditor.ValueSpecifier;

/**
 * <p>
 * Represents a fixed, non-user-configurable substring of a query part.
 * Typically, different versions of a Query (SQL, human readable) use
 * different FixedStrings in between the other parts (AWCWords), which are
 * normally common.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  string
 * </p>
 */

public class FixedString implements AWCWord, DataGroupWord, ValueSpecifier, Serializable {

  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents ...
 * </p>
 */
    private String string; 

    /** For xml-encoding purposes only */
    public String getString() {
        return string;
    }
    
    /** For xml-encoding purposes only */
    public void setString(String string) {
        this.string = string;
    }
    
    /** For xml-encoding purposes only */
    public FixedString() {
    }
    
    public FixedString(String string) {
        this.string = string;
    }
    
    /* Implementing ValueSpecifier */
    public Class getValueType() {
        return String.class;
    }
    
    /* Implementing ValueSpecifier */
    public Object getValue(DataRow dataRow) {
        return string;
    }
    
    /* Implementing AWCWord */
    public String acceptWhereClause(QueryVisitor visitor) {
        return string;
    }    
    
    /* Implementing AWCWord & DataGroupWord */
    public String getHumanStringValue() {
        return string;
    }    

    public Object clone() {
        return this;
    }
    
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) {
        return this;
    }
    
    public String getHumanStringValue(OutputReportSeeder context) {
        //context-independent
        return getHumanStringValue();
    }
} // end FixedString



