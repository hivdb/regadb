package net.sf.regadb.install.wizard.steps;

import java.awt.GridBagLayout;
import java.util.Map;

import net.sf.regadb.install.wizard.RegaDBWizardPage;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPanelNavResult;

public class AccountStep extends RegaDBWizardPage {
	private static final long serialVersionUID = -4805814473360115989L;
	
	public AccountStep() {
		super("account", tr("stepAccount"), tr("account_Description"));
		getContainer().setLayout(new GridBagLayout());
		
		addLine(tr("account_Uid"), "account_Uid", "admin");
		addLine(tr("account_Password"), "account_Pass");
		addLine(tr("account_PasswordRetype"), "account_Pass2");
		addLine(tr("account_Email"), "account_Email");
		addLine(tr("account_FirstName"), "account_FirstName");
		addLine(tr("account_LastName"), "account_LastName");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
		if ( getTextFieldByName("account_Uid").getText().length() == 0 ||
				getTextFieldByName("account_Pass").getText().length() == 0 ) {
			setProblem(tr("account_NoUsernamePassword"));
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		if ( !getTextFieldByName("account_Pass").getText().equals( getTextFieldByName("account_Pass2").getText() ) ) {
			setProblem(tr("account_PasswordMismatch"));
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		return WizardPanelNavResult.PROCEED;
	}
	
	public static String getDescription() {
		return tr("stepAccount");
	}
}
