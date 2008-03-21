package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.db.session.Login;

import org.hibernate.Query;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;

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
		List<String> list= new ArrayList<String>();
		ClassMetadata cmd = HibernateUtil.getSessionFactory().getClassMetadata(tableName);

		if (cmd != null) {
			list.addAll(getPrimaryKeys(tableName));

			// add normal properties
			String[] cols = cmd.getPropertyNames();
			for (int i = 0 ; i < cols.length ; i++) {
				String name = cols[i];
				Type t = cmd.getPropertyType(name);
				if (isValidType(t) && i != cmd.getVersionProperty()) {
					list.add(cols[i]);
				}
			}
		}
		else {
			System.err.println("metadata for table " + tableName + " not found");
			System.exit(1);
		}
		
		return list;
	}

	public String getColumnType(String tableName, String columnName) {
		ClassMetadata cmd = HibernateUtil.getSessionFactory().getClassMetadata(tableName);
		if (cmd != null) {
			SessionFactoryImplementor sfi = (SessionFactoryImplementor) HibernateUtil.getSessionFactory();
			if (getTableNames().contains(tableName) && getColumnNames(tableName).contains(columnName)) {
				Type t = cmd.getPropertyType(columnName);
				if (t != null) {
					int[] types = t.sqlTypes(sfi);
					return "" + types[0];
				}
				else if (hasComposedIdentifier(cmd)) {
					t = cmd.getIdentifierType();
					ComponentType ct = (ComponentType)t;
					Type[] types = ct.getSubtypes();
					String[] names = ct.getPropertyNames();
					for (int i = 0 ; i < types.length ; i++) {
						if (("id." + names[i]).equalsIgnoreCase(columnName)) {
							return "" + types[i].sqlTypes(sfi)[0];
						}
					}
				}
			}
		}
		else {
			System.err.println("metadata for table " + tableName + " not found");
			System.exit(1);
		}
		return null;
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
		if (cmd != null) {
			if (hasComposedIdentifier(cmd)) {
				list.addAll(getComposedIdentifierProperties(cmd));
			}
			else {
				String idString = cmd.getIdentifierPropertyName();
				if (idString != null) {
					list.add(idString);
				}
			}
		}
		return list;
	}
	
	private boolean hasComposedIdentifier(ClassMetadata cmd) {
		Type t = cmd.getIdentifierType();
		return t.isComponentType();
//		if (t instanceof ComponentType) {
//			return true;
//		}
//		return false;
	}
	
	private boolean isValidType(Type t) {
		return (!t.isAssociationType() && !t.isCollectionType() && !t.isComponentType());
	}
	
	private List<String> getComposedIdentifierProperties(ClassMetadata cmd) {
		List<String> list= new ArrayList<String>();
		
		ComponentType ct = (ComponentType) cmd.getIdentifierType();
		Type[] types = ct.getSubtypes();
		String names[] = ct.getPropertyNames();
		for (int i = 0 ; i < names.length ; i++) {
			if (isValidType(types[i])) {
				list.add("id." + names[i]);
			}
		}
		
		return list;
	}
	
	@Override
	public List<String> getTableNames() {
		List<String> list= new ArrayList<String>();
		Set<String> set = HibernateUtil.getSessionFactory().getAllClassMetadata().keySet();
		for (String name : set) {
			list.add(name);
		}
		return list;
	}
}
