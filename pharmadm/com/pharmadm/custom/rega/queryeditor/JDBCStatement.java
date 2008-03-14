package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.sql.Statement;

public class JDBCStatement implements QueryStatement {
	private java.sql.Statement statement;
	
	public JDBCStatement(Statement s) {
		this.statement = s;
	}
	
	@Override
	public void cancel() {
		if (exists()) {
			try {
				statement.cancel();
			}
			catch (SQLException e) {}
		}
	}

	@Override
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

	@Override
	public QueryResult executeQuery(String query) {
		if (exists()) {
			try {
				return new JDBCResult(statement.executeQuery(query));
			}
			catch (SQLException e) {}
		}
		return null;
	}

	@Override
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
}
