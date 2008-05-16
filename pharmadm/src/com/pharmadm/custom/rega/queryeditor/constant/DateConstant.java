
/** Java class "DateConstant.java" generated from Poseidon for UML.
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

import java.util.Date;
import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;

import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

/**
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class DateConstant extends Constant implements Serializable{

	
	public DateConstant(SuggestedValues suggestedValues) {
		super(suggestedValues);
	}
    private static final Format DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    public Class getValueType() {
        return Date.class;
    }
    
    // formats are not threadsafe!
    public Format getFormat() {
        return DATE_FORMAT;
    }

    public DateConstant() {
        super.setValue(new java.sql.Date(new Date().getTime()));
    }
    
    public Object getHumanValue() {
    	return getValue();
    }
    
    public DateConstant(String dateString) {
        Date date;
        try {
            date = ((SimpleDateFormat)DATE_FORMAT).parse(dateString);
        } catch (java.text.ParseException pe) {
            date = new Date();
        }
        super.setValue(new java.sql.Date(date.getTime()));
    }
    
    public String acceptWhereClause(QueryVisitor visitor) {
        return visitor.visitWhereClauseDateConstant(this);
    }
    
    public Object clone() throws CloneNotSupportedException {
        Constant clone = (Constant)super.clone();
        if (getValue() != null) {
            clone.setValue(((Date)getValue()).clone());
        }
        return clone;
    }

	@Override
	public String getValueTypeString() {
		return "Date";
	}
}
