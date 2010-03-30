package net.sf.regadb.ui.framework;

import net.sf.regadb.db.session.Login;

import com.pharmadm.custom.rega.queryeditor.port.DatabaseConnector;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseConnectorProvider;
import com.pharmadm.custom.rega.queryeditor.port.hibernate.HibernateConnector;

public class RegaDBConnectorProvider implements DatabaseConnectorProvider {
	private Login login;
	
	public RegaDBConnectorProvider(Login login) {
		this.login = login;
	}
	
	public void closeConnector(DatabaseConnector connector) {
		connector.close();
	}

	public DatabaseConnector createConnector() {
		return new HibernateConnector(login);
	}
}
