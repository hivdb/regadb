package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.db.session.Login;

public class HibernateConnector implements DatabaseConnector {

	Login login;
	
	public HibernateConnector(String user, String pwd) throws WrongUidException, WrongPasswordException, DisabledUserException {
		login = Login.authenticate(user, pwd);
	}
	
	public QueryStatement createScrollableReadOnlyStatement() throws SQLException {
		Transaction t = login.createTransaction();
		return new HibernateStatement(t);
	}

	public QueryResult executeQuery(String query) throws SQLException {
		Transaction transaction = login.createTransaction();
		Query q = transaction.createQuery(query);
		QueryResult result = new HibernateResult(q.list(), q.getReturnAliases(), q.getReturnTypes());
		transaction.commit();
		return result;
	}

	public List<String> getColumnNames(String tableName) {
		String entityName = tableName;
		
		List<String> list= new ArrayList<String>();
		ClassMetadata cmd = HibernateUtil.getSessionFactory().getClassMetadata(entityName);

		String idString = cmd.getIdentifierPropertyName();
		if (idString != null) {
			list.add(idString);
		}
		String[] cols = cmd.getPropertyNames();
		for (int i = 0 ; i < cols.length ; i++) {
			String name = cols[i];
			Type t = cmd.getPropertyType(name);
			if (!t.isAssociationType() && i != cmd.getVersionProperty()) {
				list.add(cols[i]);
			}
		}
		
		
		return list;
	}

	public String getColumnType(String tableName, String columnName) {
		ClassMetadata cmd = HibernateUtil.getSessionFactory().getClassMetadata(tableName);
		SessionFactoryImplementor sfi = (SessionFactoryImplementor) HibernateUtil.getSessionFactory();
		if(getColumnNames(tableName).contains(columnName)) {
			Type t = cmd.getPropertyType(columnName);
			int[] types = t.sqlTypes(sfi);
			return "" + types[0];
		}
		else {
			return null;
		}
	}

	public String getCommentForColumn(String tableName, String columnName) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCommentForTable(String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getPrimaryKeys(String tableName) {
		List<String> list= new ArrayList<String>();

		ClassMetadata cmd = HibernateUtil.getSessionFactory().getClassMetadata(tableName);
		String idString = cmd.getIdentifierPropertyName();
		if (idString != null) {
			list.add(idString);
		}
		
		return list;
	}

	@Override
	public List<String> getTableNames() {
		List<String> list= new ArrayList<String>();
		Map<String, Object> map = HibernateUtil.getSessionFactory().getAllClassMetadata();
		Set<String> s = map.keySet();
		Iterator<String> it = s.iterator();
		while (it.hasNext()) {
			String name = (String) it.next();
			list.add(name);
		}
		return list;
	}
}
