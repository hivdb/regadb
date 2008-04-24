
/** Java class "StringConstant.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.constant;

import java.io.Serializable;
import java.text.Format;

import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

/**
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class StringConstant extends Constant implements Serializable{
    
	public StringConstant(){}
	
    private static final Format STRING_FORMAT = new StringFormat();

	public StringConstant(SuggestedValues suggestedValues) {
		super(suggestedValues);
	}
    
    public Class getValueType() {
        return String.class;
    }
    
    public Format getFormat() {
        return STRING_FORMAT;
    }
    
    public String acceptWhereClause(QueryVisitor visitor) {
    	return visitor.visitWhereClauseStringConstant(this);
    }
    
    
    /**
     * A Format that accepts anything.  Any String gets parsed into a String.
     */
    private static class StringFormat extends Format {
        
        // FIXME -- what to do with the pos ??
        public StringBuffer format(Object obj, StringBuffer toAppendTo, java.text.FieldPosition pos) {
            if (obj != null) {
                if (! (obj instanceof String)) {
                    System.err.println("Expected String. Got instead: " + obj.getClass());
                    throw new IllegalArgumentException();
                }
                String objString = (String)obj;
                toAppendTo.append(objString);
            } else {
                toAppendTo.append("[unspecified]");
            }
            return toAppendTo;
        }
        
        public Object parseObject(String source, java.text.ParsePosition pos) {
            String result = null;
            if (source != null) {
                int index = pos.getIndex();
                if (index == 0) {
                    result = source;
                } else {
                    result = source.substring(index);
                }
                pos.setIndex(source.length());
            }
            return result;
        }
        
    }


	@Override
	public String getValueTypeString() {
		return "String";
	}
}



