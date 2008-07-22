package com.pharmadm.custom.rega.queryeditor.port.hibernate;

import java.util.HashMap;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;

import com.pharmadm.custom.rega.queryeditor.port.QueryResult;
import com.pharmadm.custom.rega.queryeditor.port.QueryStatement;
import com.pharmadm.custom.rega.queryeditor.port.Result;
import com.pharmadm.custom.rega.queryeditor.port.ScrollableQueryResult;

import net.sf.regadb.db.Transaction;

public class HibernateStatement implements QueryStatement {

	private boolean closed;
	private Transaction transaction;
	private Result result;
	private int fetchSize = 50;
	
	public HibernateStatement(Transaction t){
		transaction = t;
	}
	
	public void cancel() {
		if (!closed && exists()) {
			result.close();
		}
		closed = true;
	}

	public void close() {
		if (!closed && exists()) {
			result.close();
			transaction.commit();
		}
		closed = true;
	}

	public ScrollableQueryResult executeScrollableQuery(String query, HashMap<String, Object>  preparedConstantMap) {
		Query q = prepareQuery(query, preparedConstantMap);
		q.setCacheMode(CacheMode.IGNORE);
		result = new HibernateScrollableResult(q.scroll(ScrollMode.FORWARD_ONLY), q.getReturnAliases(), q.getReturnTypes());
		closed = false;
		return (HibernateScrollableResult) result;
	}

	public boolean exists() {
		return transaction != null && result != null;
	}

	public void setFetchSize(int size) {
		this.fetchSize = size;
	}

	public QueryResult executeQuery(String query, HashMap<String, Object>  preparedConstantMap) {
		Query q = prepareQuery(query, preparedConstantMap);
		result = new HibernateResult(q.scroll(), q.getReturnAliases(), q.getReturnTypes());
		closed = false;
		return (QueryResult) result;
	}
	
	private Query prepareQuery(String queryString, HashMap<String, Object>  preparedConstantMap) {
		Query q = transaction.createQuery(queryString);
		q.setReadOnly(true);
		q.setFetchSize(fetchSize);
		
		if (preparedConstantMap != null) {
			for (String key : preparedConstantMap.keySet()) {
				q.setParameter(key, preparedConstantMap.get(key));
			}
		}
		return q;
		
	}
}
