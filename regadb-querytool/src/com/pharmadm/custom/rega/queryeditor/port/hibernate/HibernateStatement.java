package com.pharmadm.custom.rega.queryeditor.port.hibernate;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollMode;

import com.pharmadm.custom.rega.queryeditor.port.QueryStatement;
import com.pharmadm.custom.rega.queryeditor.port.ScrollableQueryResult;

import net.sf.regadb.db.Transaction;

public class HibernateStatement implements QueryStatement {

	private boolean closed;
	private Transaction transaction;
	private HibernateScrollableResult result;
	private int fetchSize = 50;
	
	public HibernateStatement(Transaction t){
		transaction = t;
	}
	
	public void cancel() {
		if (!closed && exists()) {
			result.close();
			transaction.rollback();
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

	public ScrollableQueryResult executeQuery(String query) {
		Query q = transaction.createQuery(query);
		q.setFetchSize(fetchSize);
		q.setReadOnly(true);
		q.setCacheMode(CacheMode.IGNORE);
		result = new HibernateScrollableResult(q.scroll(ScrollMode.FORWARD_ONLY), q.getReturnAliases(), q.getReturnTypes());
		return result;
	}

	public boolean exists() {
		return transaction != null && result != null;
	}

	public void setFetchSize(int size) {
		this.fetchSize = size;
	}
}
