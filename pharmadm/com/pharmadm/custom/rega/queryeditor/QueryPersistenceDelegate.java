package com.pharmadm.custom.rega.queryeditor;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;

public class QueryPersistenceDelegate extends DefaultPersistenceDelegate {
	
    protected Expression instantiate(Object oldInstance, Encoder out) {
    	Query query = (Query)oldInstance;
    	System.err.println("query entered");
        return new Expression(query, query.getClass(), "new", new Object[]{query.getRootClause(), query.getSelectList(), query.getUniqueNameContext()});
    }

}
