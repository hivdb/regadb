
/** Java class "Query.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;
import com.pharmadm.util.work.Work;

/**
 * <p>
 * Represents a query to the database.
 * </p>
 * <p>
 * A query consists of a root WhereClause and a SelectList.
 * </p>
 * <p>
 * The root WhereClause is typically a ComposedWhereClause that is composed
 * of several subclauses. The WhereClauses can be configured, added and
 * removed, until the resulting constraints please the user. These
 * constraints define which tables from the database are relevant for this
 * Query and what rows to retrieve from these tables.
 * </p>
 * <p>
 * The SelectList defines what fields from the relevant tables to retrieve
 * from the database.
 * </p>
 * <p>
 * This class supports xml-encoding.
 * The following new properties are encoded :
 *  rootClause
 *  selectList
 *  uniqueNameContext
 * </p>
 */
public class Query implements Serializable, Cloneable {
    
    ///////////////////////////////////////
    // associations
    
	private transient HashMap<String, Object>  preparedConstantMap = new HashMap<String, Object>();
	
	
    private WhereClause rootClause = new AndClause();
    private SelectionStatusList selectList = new SelectionStatusList(this);
    
    private UniqueNameContext uniqueNameContext = new UniqueNameContext();
    
    ///////////////////////////////////////
    // constructor
    
    public Query() {
    }
    
    public Query(WhereClause whereClause) {
        this.rootClause = whereClause;
    }
    
    public Query(WhereClause whereClause, SelectionStatusList selectList, UniqueNameContext context) {
    	setRootClause(whereClause);
    	setSelectList(selectList);
    	setUniqueNameContext(context);
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public WhereClause getRootClause() {
        return rootClause;
    }
    
    public void setRootClause(WhereClause whereClause) {
        this.rootClause = whereClause;
    }
    
    public SelectionStatusList getSelectList() {
        return selectList;
    }
    
    /**
     * For XMLdecoder only!
     */
    public void setSelectList(SelectionStatusList selectList) {
        this.selectList = selectList;
    }
    
    public UniqueNameContext getUniqueNameContext() {
        return uniqueNameContext;
    }
    
    public void setUniqueNameContext(UniqueNameContext context) {
        this.uniqueNameContext = context;
    }
    ///////////////////////////////////////
    // operations
    
    
    /**
     * <p>
     * Returns a collection of Works that have to be performed before the query clauses
     * (SQL query, query string, ...) can be retrieved.
     * </p>
     *
     * @return a Collection with all Works required to prepare the query.
     */
    public Collection<Work> getPreparationWorks() {
        return rootClause.getQueryPreparationWorks();
    }
    
    // for testing purposes only
    public String getQueryString() throws java.sql.SQLException { 
    	return accept(DatabaseManager.getInstance().getQueryBuilder());
    }
    
    public String accept(QueryVisitor visitor) throws java.sql.SQLException{
    	preparedConstantMap.clear();
        return visitor.visitQuery(this);
   }    
    
    /**
     * <p>
     * Calculates whether the root WhereClause of this Query is valid. Validity
     * is a necessary and sufficient contraint for this Query to be able to
     * generate an equivalent SQL query that can be evaluated.
     * </p>
     * <p>
     *
     * @return whether the root WhereClause of this Query is valid
     * </p>
     */
    public boolean isValid() {
        return getRootClause().isValid() ;
    }
    
    public boolean hasFromVariables() {
    	return countFromVariables(getRootClause()) > 0;
    }
    
    private int countFromVariables(WhereClause clause) {
    	int count = 0;
    	Iterator<WhereClause> it = clause.iterateChildren();
    	while (it.hasNext()) {
    		WhereClause child = it.next();
    		if (child.isAtomic()) {
    			count+= ((AtomicWhereClause)child).getFromVariables().size();
    		}
    		else {
    			count += countFromVariables(child);
    		}
    	}
    	return count;
    }
    
    public void updateSelectList() {
        selectList.update();
    }
    
    /**
     * turn the given string in a parameter for use in
     * a prepared statement
     * @param str
     * @return the name of the parameter
     */
	public String createKey(Object o) {
		String key = "const" + preparedConstantMap.size();
		preparedConstantMap.put(key, o);
		return key;
	}
	
	/**
	 * return all parameters for a 
	 */
	public HashMap<String, Object> getPreparedParameters() {
		return preparedConstantMap;
	}
	
	public Object clone() throws CloneNotSupportedException{
		Query q = new Query();
		q.setRootClause((WhereClause) rootClause.clone());
		q.setSelectList(selectList);
		q.setUniqueNameContext(uniqueNameContext);
		q.preparedConstantMap = preparedConstantMap;
		
		return q;
	}
}



