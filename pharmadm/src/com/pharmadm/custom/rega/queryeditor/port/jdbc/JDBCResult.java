package com.pharmadm.custom.rega.queryeditor.port.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import com.pharmadm.custom.rega.queryeditor.port.*;


public class JDBCResult implements QueryResult, ScrollableQueryResult {
	private ResultSet rs;
	
	public JDBCResult(ResultSet result) {
		rs = result;
	}
	
	public void close() {
	    if (rs != null) {
	        try {
	            Statement stmt = rs.getStatement();
	            stmt.close();  // closes resultset too
	        } catch (SQLException sqle) {
	            System.err.println("Problem while trying to close query statement.");
	        } catch (NullPointerException npe) {
	            System.err.println("Nullpointer problem while trying to close query statement - probably harmless.");
	        }
	    }
	}

	public Object get(int x, int y) {
		if (rs != null) {
			try {
	            rs.absolute(x+1);
	            return rs.getObject(y+1);
	        } 
	        catch (SQLException sqle) {
	        	return null;
	        }
		}
		return null;
	}

	public int size() {
		if (rs != null) {
			try {
		        rs.last();
		        return (rs.last() ? rs.getRow() : 0);
			}
			catch (SQLException e) {
				return 0;
			}
		}
		return 0;
	}

	//TODO is className appropriate? maybe better to call this method entity etc
	public String getColumnClassName(int index) {
		String name = "";
		if (rs != null) {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				name = rsmd.getColumnClassName(index + 1);
			}
			catch (SQLException e) {}
		}
		return name;
	}

	public String getColumnName(int index) {
		String name = "";
		if (rs != null) {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				name = rsmd.getColumnName(index + 1);
			}
			catch (SQLException e) {}
		}
		return name;
	}

	public int getColumnCount() {
		if (rs != null) {
			try {
				ResultSetMetaData rsmd = rs.getMetaData();
				return rsmd.getColumnCount();
			}
			catch (SQLException e) {}
		}
		return 0;
	}

	public Object[] get() {
		Object[] results = new Object[getColumnCount()]; 
		try {
			for (int i = 1 ; i <= results.length ; i++) {
				results[i-1] = rs.getObject(i);
			}
			rs.next();
		} catch (SQLException e) {}
		
		return results;
	}

	public boolean isLast() {
		try {
			return rs.isLast();
		} catch (SQLException e) {}
		return true;
	}
}