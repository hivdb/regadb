
/** Java class "DoubleConstant.java" generated from Poseidon for UML.
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
import java.text.DecimalFormat;


/**
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class DoubleConstant extends Constant implements Serializable{
    
	private static final Format DOUBLE_FORMAT = new DecimalFormat();
    
	public DoubleConstant(){
    	setValue(0);
	}
	
	public DoubleConstant(SuggestedValues suggestedValues) {
		super(suggestedValues);
	}
	
    public Class getValueType() {
        return Number.class;
    }
    
    public Format getFormat() {
        return DOUBLE_FORMAT;
    }

	@Override
	public String getValueTypeString() {
		return "Numeric";
	}
	
	public Object getHumanValue() {
		return 0;
	}
	
}
