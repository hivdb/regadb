
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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject.ValueType;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

/**
 * <p>
 * This class supports xml-encoding. No new properties are encoded.
 * </p>
 */
public class DateConstant extends Constant implements Serializable{

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    
    public DateConstant() {
    	super();
    }

    public String acceptWhereClause(QueryVisitor visitor) {
        return visitor.visitWhereClauseDateConstant(this);
    }
    
	@Override
	public DbObject getDbObject() {
		return DatabaseManager.getInstance().getAWCCatalog().getObject(ValueType.Date.toString());
	}

	@Override
	public Object getdefaultValue() {
		return DATE_FORMAT.format(new java.sql.Date(new Date().getTime()));
	}

	@Override
	protected String parseObject(Object o) {
		ParsePosition pos = new ParsePosition(0);
		Object result = DATE_FORMAT.parseObject(o.toString(),pos);
		if (result == null || pos.getIndex()< o.toString().length()) {
			return null;
		}
		return o.toString();
	}
}
