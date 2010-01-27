package com.pharmadm.custom.rega.gui;

import net.sf.regadb.db.session.Login;

import com.pharmadm.custom.rega.queryeditor.port.DatabaseConnector;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseConnectorProvider;
import com.pharmadm.custom.rega.queryeditor.port.hibernate.HibernateConnector;

public class SwingDatabaseConnectorProvider implements DatabaseConnectorProvider {
	private Login login;
	
	public SwingDatabaseConnectorProvider(Login login) {
		this.login = login;
	}
	
	@Override
	public void closeConnector(DatabaseConnector connector) {
		connector.close();
	}

	@Override
	public DatabaseConnector createConnector() {
		return new HibernateConnector(login);
	}
}
