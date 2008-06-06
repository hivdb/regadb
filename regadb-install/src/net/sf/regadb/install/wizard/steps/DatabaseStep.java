package net.sf.regadb.install.wizard.steps;

import java.awt.GridBagLayout;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.swing.JLabel;

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
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
		// Check if all needed is filled in
		
		if ( getTextFieldByName("psql_url").getText().length() == 0
				|| getTextFieldByName("psql_adminUser").getText().length() == 0
				|| getTextFieldByName("db_databaseName").getText().length() == 0
				|| getTextFieldByName("db_roleUser").getText().length() == 0
 				|| getTextFieldByName("db_rolePass").getText().length() == 0
				) {
			
			setProblem(tr("db_EnterDetails"));
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		// Check password retype
		
		if ( !getTextFieldByName("db_rolePass").getText().equals( getTextFieldByName("db_rolePassRetype").getText() ) ) {
			setProblem(tr("account_PasswordMismatch"));
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		// Check psql admin login
		
		try {
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
			}
			
			// Check if database exists
			
			boolean exists = true;
			try {
				Class.forName("org.postgresql.Driver");
				String url = "jdbc:postgresql://" + getTextFieldByName("psql_url").getText() + "/" + getTextFieldByName("db_databaseName").getText();
				Properties props = new Properties();
				props.setProperty("user", getTextFieldByName("db_roleUser").getText());
				props.setProperty("password", getTextFieldByName("db_rolePass").getText());
				DriverManager.getConnection(url, props).close();
			} catch ( SQLException e ) {
				if ( e.getLocalizedMessage().contains("database \"" + getTextFieldByName("db_databaseName").getText() + "\" does not exist") ) {
					exists = false;
				} else {
					setProblem(e.getLocalizedMessage());
					return WizardPanelNavResult.REMAIN_ON_PAGE;
				}
			}
			
			if ( exists ) {
				setProblem(tr("db_databaseExists"));
				return WizardPanelNavResult.REMAIN_ON_PAGE;
			}
			
			// Check if user role exists
			
			exists = true;
			try {
				Class.forName("org.postgresql.Driver");
				String url = "jdbc:postgresql://" + getTextFieldByName("psql_url").getText() + "/template1";
				Properties props = new Properties();
				props.setProperty("user", getTextFieldByName("db_roleUser").getText());
				props.setProperty("password", getTextFieldByName("db_rolePass").getText());
				DriverManager.getConnection(url, props).close();
			} catch ( SQLException e ) {
				if ( e.getLocalizedMessage().contains("role \"" + getTextFieldByName("db_roleUser").getText() + "\" does not exist") ) {
					exists = false;
				} else {
					setProblem(e.getLocalizedMessage());
					return WizardPanelNavResult.REMAIN_ON_PAGE;
				}
			}
			
			if ( exists ) {
				setProblem(tr("db_roleExists"));
				return WizardPanelNavResult.REMAIN_ON_PAGE;
			}
		} catch ( ClassNotFoundException e ) {
			setProblem(tr("db_DriverNotFound") + ": " + e.getLocalizedMessage());
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		return WizardPanelNavResult.PROCEED;
	}
	
	public static String getDescription() {
		return tr("stepDatabase");
	}
}
