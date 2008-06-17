package net.sf.regadb.io.db.util.msaccess;

import java.sql.Connection;

public interface IConnectionProvider {
	public Connection getConnection();

	public String getCsvPrefix();
}
