package net.sf.regadb.io.db.util.db2csv;

import java.sql.Connection;

import net.sf.regadb.io.db.util.MysqlDatabase;

public class MysqlConnectionProvider implements IConnectionProvider {
	private MysqlDatabase db_;
	
	public MysqlConnectionProvider(String database, String user, String password) {
		db_ = new MysqlDatabase(database, user, password);
	}
	
	public Connection getConnection() {
		return db_.getConnection();
	}

	public String getCsvPrefix() {
		return "";
	}
}
