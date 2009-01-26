package net.sf.regadb.install.wizard;

import java.util.Map;

import javax.swing.JOptionPane;

import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;

public class RegaDBInstallHandler implements WizardPage.WizardResultProducer {
	@SuppressWarnings("unchecked")
	public boolean cancel(Map settings) {
		boolean dialogShouldClose = JOptionPane.showConfirmDialog (null,RegaDBWizardPage.tr("cancelInstall"),
				RegaDBWizardPage.tr("warning.title"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
		
		return dialogShouldClose;
	}
	
	@SuppressWarnings("unchecked")
	public Object finish(Map settings) throws WizardException {
		return new RegaDBDeferredWizardResult();
	}
}
