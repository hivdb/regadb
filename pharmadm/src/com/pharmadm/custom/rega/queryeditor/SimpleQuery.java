package com.pharmadm.custom.rega.queryeditor;

import java.util.ArrayList;
import java.util.Collection;

import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;
import com.pharmadm.util.work.Work;

public class SimpleQuery extends Query {
	private String query;
	public SimpleQuery(String queryString) {
		this.query = queryString;
	}
	
    public boolean isValid() {
        return getRootClause().isValid();
    }
	
    public String getQueryString() throws java.sql.SQLException { 
    	return query;
    }
    
    public String accept(QueryVisitor visitor) throws java.sql.SQLException{
        return query;
   }    
    
    public Collection<Work> getPreparationWorks() {
        return new ArrayList<Work>();
    }
    
}
