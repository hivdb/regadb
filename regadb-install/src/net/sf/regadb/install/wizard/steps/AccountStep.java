package net.sf.regadb.install.wizard.steps;

import java.awt.GridBagLayout;
import java.util.Map;

import javax.swing.JTextField;

import net.sf.regadb.install.wizard.RegaDBWizardPage;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPanelNavResult;

public class AccountStep extends RegaDBWizardPage {
	private static final long serialVersionUID = -4805814473360115989L;
	JTextField user, pass1, pass2;
	
	public AccountStep() {
		super("account", tr("stepAccount"), tr("account_Description"));
		getContainer().setLayout(new GridBagLayout());
		
		// TODO remove defaults
		user = addLine("Username", "regadb_user", "admin");
		pass1 = addLine("Password", "regadb_pass", "admin");
		pass2 = addLine("Retype", "regadb_pass2", "admin");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
		if ( user.getText().length() == 0 || pass1.getText().length() == 0 )
			setProblem(tr("account_NoUsernamePassword"));
		
		if ( !pass1.getText().equals( pass2.getText() ) )
			setProblem(tr("account_PasswordMismatch"));
		
		return WizardPanelNavResult.PROCEED;
	}
}
