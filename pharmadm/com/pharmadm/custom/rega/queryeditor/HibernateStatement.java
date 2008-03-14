package com.pharmadm.custom.rega.queryeditor;

import org.hibernate.Query;
import net.sf.regadb.db.Transaction;

public class HibernateStatement implements QueryStatement {

	private boolean closed;
	private Transaction transaction;
	
	public HibernateStatement(Transaction t){
		transaction = t;
	}
	
	@Override
	public void cancel() {
		if (!closed && exists()) {
			transaction.rollback();
		}
	}

	@Override
	public void close() {}

	@Override
	public QueryResult executeQuery(String query) {
		Query q = transaction.createQuery(query);
		QueryResult result = new HibernateResult(q.list(), q.getReturnAliases(), q.getReturnTypes());
		transaction.commit();
		closed = true;
		return result;
	}

	@Override
	public boolean exists() {
		return transaction != null;
	}

	@Override
	public void setFetchSize(int size) {}
}
