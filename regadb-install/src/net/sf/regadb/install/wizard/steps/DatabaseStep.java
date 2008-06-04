package net.sf.regadb.install.wizard.steps;

import java.awt.GridBagLayout;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import net.sf.regadb.install.wizard.ChangeListener;
import net.sf.regadb.install.wizard.GlobalConf;
import net.sf.regadb.install.wizard.RegaDBWizardPage;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPanelNavResult;

public class DatabaseStep extends RegaDBWizardPage {
	private static final long serialVersionUID = -3997952792118936850L;
	
	public DatabaseStep() {
		super("data", tr("stepDatabase"), tr("db_Description"));
		
		getContainer().setLayout(new GridBagLayout());
		
		getContainer().add(new JLabel(tr("db_PSql")), getGridBag(-1, lineNr++));
		addLine(tr("db_URL"), "psql_url", "localhost:5432");
		addLine(tr("db_PSqlUsername"), "psql_adminUser", "postgres");
		addLine(tr("db_PSqlPassword"), "psql_adminPass");
		
		getContainer().add(new JLabel(tr("db_RegaDB")), getGridBag(0, lineNr++));
		addLine(tr("db_databaseName"), "db_databaseName", "regadb");
		addLine(tr("db_RegaDBUsername"), "db_roleUser", "regadb_user");
		addLine(tr("db_RegaDBPassword"), "db_rolePass");
		addLine(tr("db_PassRetype"), "db_rolePassRetype");
		
		JCheckBox initData = new JCheckBox(tr("db_InitData"));
		initData.setName("initData");
		initData.setSelected(true);
		getContainer().add(initData, getGridBag(0, lineNr++));
		
		GlobalConf.getInstance().addListener(new ChangeListener() {
			public void changed() {
				String url = getConf("hibernate.connection.url");
				url = url.substring(url.indexOf("//") + 2);
				
				String dbName = url.substring(url.indexOf("/") + 1);
				url = url.substring(0, url.indexOf("/"));
				
				getTextFieldByName("psql_url").setText(url);
				getTextFieldByName("db_databaseName").setText(dbName);
				getTextFieldByName("db_roleUser").setText(getConf("hibernate.connection.username"));
				getTextFieldByName("db_rolePass").setText(getConf("hibernate.connection.password"));
				getTextFieldByName("db_rolePassRetype").setText(getConf("hibernate.connection.password"));
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
		if ( getTextFieldByName("psql_url").getText().length() == 0
				|| getTextFieldByName("psql_adminUser").getText().length() == 0
				|| getTextFieldByName("db_databaseName").getText().length() == 0
				|| getTextFieldByName("db_roleUser").getText().length() == 0
//				|| getTextFieldByName("db_rolePass").getText().length() == 0
				) {
			
			setProblem(tr("db_EnterDetails"));
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://" + getTextFieldByName("psql_url").getText() + "/template1";
			Properties props = new Properties();
			props.setProperty("user", getTextFieldByName("psql_adminUser").getText());
			props.setProperty("password", getTextFieldByName("psql_adminPass").getText());
			DriverManager.getConnection(url, props).close();
		} catch ( SQLException e ) {
			setProblem(e.getLocalizedMessage());
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		} catch ( ClassNotFoundException e ) {
			setProblem(tr("db_DriverNotFound") + ": " + e.getLocalizedMessage());
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		boolean dbExists = true;
		try {
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://" + getTextFieldByName("psql_url").getText() + "/" + getTextFieldByName("db_databaseName").getText();
			Properties props = new Properties();
			props.setProperty("user", getTextFieldByName("db_roleUser").getText());
			props.setProperty("password", getTextFieldByName("db_rolePass").getText());
			DriverManager.getConnection(url, props).close();
		} catch ( SQLException e ) {
			if ( e.getLocalizedMessage().contains("does not exist") ) {
				dbExists = false;
			} else {
				setProblem(e.getLocalizedMessage());
				return WizardPanelNavResult.REMAIN_ON_PAGE;
			}
		} catch ( ClassNotFoundException e ) {
			setProblem(tr("db_DriverNotFound") + ": " + e.getLocalizedMessage());
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		if ( dbExists ) {
			setProblem(tr("db_databaseExists"));
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		return WizardPanelNavResult.PROCEED;		
	}
}
