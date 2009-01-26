package net.sf.regadb.install.wizard.steps;

import java.awt.GridBagLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

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
		
		if ( getDbHost().length() == 0
				|| getAdminUser().length() == 0
				|| getDbName().length() == 0
				|| getRoleUser().length() == 0
 				|| getRolePass().length() == 0
				) {
			
			setProblem(tr("db_EnterDetails"));
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		// Check password retype
		
		if ( !getRolePass().equals( getTextFieldByName("db_rolePassRetype").getText() ) ) {
			setProblem(tr("account_PasswordMismatch"));
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		// Check psql admin login
		
		try {
			try {
				Connection conn = getConnection(
						getAdminUser(),
						getAdminPass(),
						"template1");
				conn.close();
			} catch ( SQLException e ) {
				setProblem(e.getLocalizedMessage());
				return WizardPanelNavResult.REMAIN_ON_PAGE;
			}
			
			// Check if database exists
			
			boolean exists = true;
			try {
				Connection conn = getConnection(
						getAdminUser(),
						getAdminPass(),
						getDbName());
				conn.close();
			} catch ( SQLException e ) {
				if ( e.getLocalizedMessage().contains("database \"" + getDbName() + "\" does not exist") ) {
					exists = false;
				} else {
					setProblem(e.getLocalizedMessage());
					return WizardPanelNavResult.REMAIN_ON_PAGE;
				}
			}
			
			if ( exists ){
				settings.put("dbExists", true);

				if(JOptionPane.showConfirmDialog(null, tr("warning.dbExists"), tr("warning.title"), JOptionPane.YES_NO_OPTION)
					!= JOptionPane.YES_OPTION){
					setProblem(tr("db_databaseExists"));
					return WizardPanelNavResult.REMAIN_ON_PAGE;
				}
			}
			else{
				settings.put("dbExists", false);
			}
			
			// Check if user role exists
			
			// TODO this doesn't work on postgres on windows
			// postgres on windows returns "password authentication failed" when role doesn't exist
			// needs new method to detect user role existence
			
			exists = true;
			try {
				Connection conn = getConnection(
								getAdminUser(),
								getAdminPass(),
								"template1");
				PreparedStatement st = conn.prepareStatement("select * from pg_user where usename = ?");
				st.setString(1, getRoleUser());
				if(!st.executeQuery().isAfterLast()){
					exists = true;
					settings.put("roleExists", true);
					conn.close();
					conn = getConnection(getRoleUser(), getRolePass(), "template1");
				}
				else
					settings.put("roleExists", false);
				
				conn.close();
			} catch ( SQLException e ) {
				if ( e.getLocalizedMessage().contains("password authentication failed") ) {
					setProblem(tr("db_roleWrongPassword"));
					return WizardPanelNavResult.REMAIN_ON_PAGE;
				} else {
					setProblem(e.getLocalizedMessage());
					return WizardPanelNavResult.REMAIN_ON_PAGE;
				}
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
	
	protected Connection getConnection(String user, String passwd, String db) throws SQLException, ClassNotFoundException{		
		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://" + getDbHost() + "/"+ db;
		Properties props = new Properties();
		props.setProperty("user", user);
		props.setProperty("password", passwd);
		return DriverManager.getConnection(url, props);
	}
	
	protected String getRoleUser(){
		return getTextFieldByName("db_roleUser").getText();
	}
	protected String getRolePass(){
		return getTextFieldByName("db_rolePass").getText();
	}
	
	protected String getAdminUser(){
		return getTextFieldByName("psql_adminUser").getText();
	}
	protected String getAdminPass(){
		return getTextFieldByName("psql_adminPass").getText();
	}
	
	protected String getDbName(){
		return getTextFieldByName("db_databaseName").getText();
	}
	
	protected String getDbHost(){
		return getTextFieldByName("psql_url").getText();
	}
}
