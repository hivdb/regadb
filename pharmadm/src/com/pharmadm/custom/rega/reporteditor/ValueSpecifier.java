/*
 * ValueSpecifier.java
 *
 * Created on November 18, 2003, 8:06 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;

/**
 *
 * @author  kristof
 */
public interface ValueSpecifier {
    
    public Class getValueType();
    
    public Object getValue(DataRow dataRow); 
    
    /* obtain a clone for this ValueSpecifier :
     * if it has been cloned already in the current context, get the existing clone from originalToCloneMap
     * (this is the case for DataInputVariables, ObjectListVariables, and Constants);
     * otherwise (for MethodSpecifiers and SQLSpecifiers) make a new clone;
     * for FixedStrings no cloning at all is required
     */
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) throws CloneNotSupportedException;
    
    public Object clone() throws CloneNotSupportedException;
}
