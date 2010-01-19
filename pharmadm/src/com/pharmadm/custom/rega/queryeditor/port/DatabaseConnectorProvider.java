package com.pharmadm.custom.rega.queryeditor.port;

public interface DatabaseConnectorProvider {
	public DatabaseConnector createConnector();
	public void closeConnector(DatabaseConnector connector);
}
