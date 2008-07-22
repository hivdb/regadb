package com.pharmadm.custom.rega.queryeditor.port.jdbc;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.pharmadm.custom.rega.queryeditor.port.QueryResult;
import com.pharmadm.custom.rega.queryeditor.port.QueryStatement;
import com.pharmadm.custom.rega.queryeditor.port.ScrollableQueryResult;

public class JDBCStatement implements QueryStatement {
	private java.sql.Statement statement;
	
	public JDBCStatement(Statement s) {
		this.statement = s;
	}
	
	public void cancel() {
		if (exists()) {
			try {
				statement.cancel();
			}
			catch (SQLException e) {}
		}
	}

	public void close() {
        if (exists()) {
            try {
                statement.close();
            }
            catch (SQLException sqle) {
                System.err.println("Problem while trying to close query statement.");
            } 
            catch (NullPointerException npe) {
                System.err.println("Nullpointer problem while trying to close query statement - probably harmless.");
            }
        }
	}

	public ScrollableQueryResult executeScrollableQuery(String query, HashMap<String, Object>  preparedConstantMap) {
		if (exists()) {
			try {
				return new JDBCResult(statement.executeQuery(query));
			}
			catch (SQLException e) {}
		}
		return null;
	}

	public void setFetchSize(int size) {
		if (exists()) {
			try {
				statement.setFetchSize(size);
			}
			catch (SQLException e) {}
		}
	}
	
	public boolean exists() {
		return statement != null;
	}

	public QueryResult executeQuery(String query, HashMap<String, Object>  preparedConstantMap) {
		if (exists()) {
			try {
				return new JDBCResult(statement.executeQuery(query));
			}
			catch (SQLException e) {}
		}
		return null;
	}
}
