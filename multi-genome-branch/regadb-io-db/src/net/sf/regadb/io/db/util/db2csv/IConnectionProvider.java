package net.sf.regadb.io.db.util.db2csv;

import java.sql.Connection;

public interface IConnectionProvider {
	public Connection getConnection();

	public String getCsvPrefix();
}
