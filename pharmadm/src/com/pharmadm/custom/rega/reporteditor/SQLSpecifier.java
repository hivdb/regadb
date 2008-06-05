/*
 * SQLSpecifier.java
 *
 * Created on November 25, 2003, 2:11 PM
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
 */
/* 
 * This class supports xml-encoding. The following new properties are encoded :
 *  valueType 
 */
public class SQLSpecifier extends OrderedDGWordList implements ValueSpecifier {
    
    // %$ KVB : THIS CLASS NOT COMPLETELY IMPLEMENTED YET !
    // In particular getValue(DataRow dataRow) must be implemented, using a
    // well-thought-out version of getSQLQuery.
    // The current implementation is just a placeholder...
    
    private Class valueType;
    
    /** Creates a new instance of SQLSpecifier */
    public SQLSpecifier() {
    }
    
    /* Implementing ValueSpecifier */
    /* observe that all words must already have been cloned and put in the originalToCloneMap */
    public ValueSpecifier cloneInContext(java.util.Map originalToCloneMap) throws CloneNotSupportedException {
        SQLSpecifier clone = (SQLSpecifier)cloneInContext(originalToCloneMap, null);
        return clone;
    }
    
    public Object clone() throws CloneNotSupportedException {
        SQLSpecifier clone = (SQLSpecifier)super.clone();
        return clone;
    }
    
    /* Implementing ValueSpecifier */
    public Class getValueTypeClass() {
        return valueType;
    }
    
    /* For xml-encoding purposes */
    public void setValueType(Class valueType) {
        this.valueType = valueType;
    }
    
    /* Implementing ValueSpecifier */
    public Object getValue(DataRow dataRow) {
        // %$ KVB : to be implemented using the result of the SQL query (somehow)
        return null;
    }
    
    public String getSQLQuery() {
        // %$ KVB : very temporary implementation
        StringBuffer sb = new StringBuffer();
        Iterator iterWords = getWords().iterator();
        while (iterWords.hasNext()) {
            DataGroupWord word = (DataGroupWord)iterWords.next();
            sb.append(word.getHumanStringValue());
        }
        return sb.toString();
    }
    
}
