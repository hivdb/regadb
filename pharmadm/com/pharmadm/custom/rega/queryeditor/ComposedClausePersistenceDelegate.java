package com.pharmadm.custom.rega.queryeditor;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;

public class ComposedClausePersistenceDelegate extends DefaultPersistenceDelegate {

    protected Expression instantiate(Object oldInstance, Encoder out) {
    	ComposedWhereClause clause = (ComposedWhereClause)oldInstance;
    	System.err.println("enter comp clause");
        return new Expression(clause, clause.getClass(), "new", new Object[]{clause.getChildren()});
    }
}
