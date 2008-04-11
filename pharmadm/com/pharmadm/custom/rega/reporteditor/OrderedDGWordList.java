/*
 * OrderedDGWordList.java
 *
 * Created on November 18, 2003, 7:41 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import com.pharmadm.custom.rega.queryeditor.*;
import java.util.*;

/**
 *
 * @author  kristof
 */

/**
 * <p>
 * An OrderedDGWordList can properly concatenate the String representation of
 * its DataGroupWords into a clause in a particular format.
 * </p>
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 * 
 */
public class OrderedDGWordList extends OrderedConfigurableWordList {
    
    /** For xml-encoding purposes only */
    public OrderedDGWordList() {
    }
    
    /** Creates a new instance of OrderedDGWordList */
    public OrderedDGWordList(DataGroup owner) {
        super(owner);
    }
     
    public String getHumanStringValue(QueryOutputReportSeeder context) {
        StringBuffer sb = new StringBuffer();
        Iterator<ConfigurableWord> iterWords = getWords().iterator();
        while (iterWords.hasNext()) {
            DataGroupWord word = (DataGroupWord)iterWords.next();
            sb.append(word.getHumanStringValue(context));
            sb.append(" ");
        }
        return sb.toString();
    }
    
    public void addConstant(Constant constant) {
        getWords().add(constant);
        if (hasOwner()) {
            ((DataGroup)getOwner()).addConstant(constant);
        }
    }
    public void addFixedString(FixedString fString) {
        getWords().add(fString);
    }
    public void addDataOutputVariable(DataOutputVariable outputVar) {
        getWords().add(outputVar);
        if (hasOwner()) {
            ((DataGroup)getOwner()).addDataOutputVariable(outputVar);
        }
    }
    public void addDataInputVariable(DataInputVariable inputVar) {
        getWords().add(inputVar);
        if (hasOwner()) {
            ((DataGroup)getOwner()).addDataInputVariable(inputVar);
        }
    }
    public void addObjectListVariable(ObjectListVariable obListVar) {
        getWords().add(obListVar);
        if (hasOwner()) {
            ((DataGroup)getOwner()).addObjectListVariable(obListVar);
        }
    }
    
}
