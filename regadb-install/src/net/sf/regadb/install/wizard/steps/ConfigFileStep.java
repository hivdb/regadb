package net.sf.regadb.install.wizard.steps;

import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import net.sf.regadb.install.wizard.CustomFilter;
import net.sf.regadb.install.wizard.FilePicker;
import net.sf.regadb.install.wizard.GlobalConf;
import net.sf.regadb.install.wizard.RegaDBWizardPage;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPanelNavResult;

public class ConfigFileStep extends RegaDBWizardPage {
	private static final long serialVersionUID = 9197379182710924361L;
	private JRadioButton exist, create;
	private FilePicker existingFile;
	private ButtonGroup group;
	
	public ConfigFileStep() {
		super("settings", tr("stepConfigFile"));
		getContainer().setLayout(new GridBagLayout());
		
		create = new JRadioButton(tr("config_newConfig"), true);
		getContainer().add(create, getGridBag(0, 0));
		
		exist = new JRadioButton(tr("config_existingConfig"));
		exist.setName("useExisting");
		getContainer().add(exist, getGridBag(0, 1));
		
		// TODO remove default
		existingFile = new FilePicker("configExistingFile", "/home/dluypa0/rega_institute/regadb/conf/global-conf.xml");
		existingFile.setFileFilter(new CustomFilter("xml"));
	    existingFile.getTextField().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				exist.setSelected(true);
			}
		});
		existingFile.getButton().addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				exist.setSelected(true);
			}
		});
		
		getContainer().add(existingFile, getGridBag(0, 2));
		
		group = new ButtonGroup();
		group.add(exist);
		group.add(create);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
		if ( exist.isSelected() ) {
			if ( !existingFile.getFile().exists() ) {
				setProblem(tr("config_existingFileNotExist"));
				return WizardPanelNavResult.REMAIN_ON_PAGE;
			} else {
				GlobalConf.getInstance().setFile(existingFile.getFile());
			}
		}
		return WizardPanelNavResult.PROCEED;
	}
}
