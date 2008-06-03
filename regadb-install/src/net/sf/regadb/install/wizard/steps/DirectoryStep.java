package net.sf.regadb.install.wizard.steps;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sf.regadb.install.wizard.ChangeListener;
import net.sf.regadb.install.wizard.FilePicker;
import net.sf.regadb.install.wizard.GlobalConf;
import net.sf.regadb.install.wizard.RegaDBWizardPage;

import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPanelNavResult;

public class DirectoryStep extends RegaDBWizardPage {
	private static final long serialVersionUID = -4655600866596021446L;
	private JPanel simplePanel, advPanel;
	private FilePicker installDir, queryPick, logPick, configPick;
	
	public DirectoryStep() {
		super("directory", tr("stepDirectory"), tr("directory_Description"));
		
		// Simple panel
		
		simplePanel = new JPanel();
		simplePanel.setLayout(new GridBagLayout());
		getContainer().add(simplePanel);
		
		installDir = new FilePicker("simpleInstallDir");
		installDir.addListener(new ChangeListener() {
			public void changed() {
				updateDirs();
			}
		});
		installDir.setDirectoryOnly(true);
		simplePanel.add(installDir, getGridBag(0, 0));
		
        JButton toggleAdvanced = new JButton(tr("directory_ToggleAdvanced"));
		GridBagConstraints gbc = getGridBag(0, 1);
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		simplePanel.add(toggleAdvanced, gbc);
		
		// Advanced panel
		
		advPanel = new JPanel();
		advPanel.setLayout(new GridBagLayout());
		advPanel.setVisible(false);
		getContainer().add(advPanel);
		
		advPanel.add(new JLabel(tr("directory_SettingsFile")), getGridBag(0, 0));
		configPick = new FilePicker("configCreateFile", "/home/dluypa0/");
		configPick.setDirectoryOnly(true);
		advPanel.add(configPick, getGridBag(0, 1));
		
		advPanel.add(new JLabel(tr("directory_QueryDir")), getGridBag(0, 2));
		queryPick = new FilePicker("querydir");
		queryPick.setDirectoryOnly(true);
		advPanel.add(queryPick, getGridBag(0, 3));
		
		advPanel.add(new JLabel(tr("directory_LogDir")), getGridBag(0, 4));
		logPick = new FilePicker("logdir");
		logPick.setDirectoryOnly(true);
		advPanel.add(logPick, getGridBag(0, 5));
		
		// Set default installation directories
		
		String defaultDir = "";
		if( System.getProperty("os.name").toLowerCase().startsWith("windows") ) {
        	defaultDir = "C:\\Program Files\\rega_institute\\regadb";
        } else {
        	defaultDir = System.getProperty("user.home") + "/rega_institute/regadb";
        }
		installDir.getTextField().setText(defaultDir);
		updateDirs();
    	
		// Set events
		
		GlobalConf.getInstance().addListener(new ChangeListener() {
			public void changed() {
				queryPick.getTextField().setText(getConf("regadb.query.resultDir"));
				logPick.getTextField().setText(getConf("regadb.log.dir"));
				toggleAdvanced(true);
			}
		});
		
		toggleAdvanced.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				toggleAdvanced();
			}
		});
	}
	
	private void toggleAdvanced() {
		simplePanel.setVisible(!simplePanel.isVisible());
		advPanel.setVisible(advPanel.isVisible());
	}
	
	private void toggleAdvanced(boolean b) {
		simplePanel.setVisible(!b);
		advPanel.setVisible(b);
	}
	
	private void updateDirs() {
		String defDir = installDir.getTextField().getText();
		
		configPick.getTextField().setText(defDir + File.separator + "conf");
		queryPick.getTextField().setText(defDir + File.separator + "querydir");
		logPick.getTextField().setText(defDir + File.separator + "logs");
		
		revalidate();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
		File configDir = new File( configPick.getTextField().getText() );
		File queryDir = new File( queryPick.getTextField().getText() );
		File logDir = new File( logPick.getTextField().getText() );
		
		if ( !configDir.exists() || !queryDir.exists() || !logDir.exists() ) {
			int opt = JOptionPane.showConfirmDialog(this, tr("filepick_okToCreate"), tr("filepick_okToCreateTitle"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if ( opt == JOptionPane.NO_OPTION ) {
				setProblem(tr("filepick_enterValid"));
				return WizardPanelNavResult.REMAIN_ON_PAGE;
			}
		}
		
		return WizardPanelNavResult.PROCEED;
	}
}
