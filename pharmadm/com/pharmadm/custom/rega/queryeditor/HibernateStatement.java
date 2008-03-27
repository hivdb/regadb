package com.pharmadm.custom.rega.queryeditor;

import org.hibernate.Query;
import net.sf.regadb.db.Transaction;

public class HibernateStatement implements QueryStatement {

	private boolean closed;
	private Transaction transaction;
	
	public HibernateStatement(Transaction t){
		transaction = t;
	}
	
	public void cancel() {
		if (!closed && exists()) {
			transaction.rollback();
		}
	}

	public void close() {}

	public QueryResult executeQuery(String query) {
		Query q = transaction.createQuery(query);
		QueryResult result = new HibernateResult(q.list(), q.getReturnAliases(), q.getReturnTypes());
		transaction.commit();
		closed = true;
		return result;
	}

	public boolean exists() {
		return transaction != null;
	}

	public void setFetchSize(int size) {
	    //TODO implement this method
	}
}
