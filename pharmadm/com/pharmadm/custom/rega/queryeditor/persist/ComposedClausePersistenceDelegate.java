package com.pharmadm.custom.rega.queryeditor.persist;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;

import com.pharmadm.custom.rega.queryeditor.ComposedWhereClause;

public class ComposedClausePersistenceDelegate extends DefaultPersistenceDelegate {

    protected Expression instantiate(Object oldInstance, Encoder out) {
    	ComposedWhereClause clause = (ComposedWhereClause)oldInstance;
    	System.err.println("enter comp clause");
        return new Expression(clause, clause.getClass(), "new", new Object[]{clause.getChildren()});
    }
}
