
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
		configPick = new FilePicker("directory_SettingsFile");
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
		if( isWindows() ) {
        	defaultDir = "C:\\Program Files\\rega_institute\\regadb";
        } else {
        	setAdvanced();
        	defaultDir = System.getProperty("user.home") + "/rega_institute/regadb";
	       	configPick.getTextField().setText("/etc/rega_institute/regadb");
        }
		
    	installDir.getTextField().setText(defaultDir);
    	updateDirs();
		
		// Set events
		
		toggleAdvanced.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				setAdvanced(!isAdvanced());
			}
		});
	}
	
	private void setAdvanced() {
		setAdvanced(true);
	}
	private void setAdvanced(boolean b) {
		simplePanel.setVisible(!b);
		advPanel.setVisible(b);
	}
	private boolean isAdvanced() {
		return advPanel.isVisible();
	}
	
	private void updateDirs() {
		String defDir = installDir.getTextField().getText();
		
		if ( isWindows() ) configPick.getTextField().setText(defDir + File.separator + "conf");
		queryPick.getTextField().setText(defDir + File.separator + "querydir");
		logPick.getTextField().setText(defDir + File.separator + "logs");
		
		revalidate();
	}
	
	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
		File configDir = new File( configPick.getTextField().getText() );
		File queryDir = new File( queryPick.getTextField().getText() );
		File logDir = new File( logPick.getTextField().getText() );
		
		// Check dirs for writeability
		
		if ( !getFirstExistingDir(configDir).canWrite() ) {
			setProblem( tr("directory_Unwritable").replaceAll("\\{DIR\\}", getFirstExistingDir(configDir).getAbsolutePath()) );
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		if ( !getFirstExistingDir(queryDir).canWrite() ) {
			setProblem( tr("directory_Unwritable").replaceAll("\\{DIR\\}", getFirstExistingDir(configDir).getAbsolutePath()) );
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		if ( !getFirstExistingDir(logDir).canWrite() ) {
			setProblem( tr("directory_Unwritable").replaceAll("\\{DIR\\}", getFirstExistingDir(configDir).getAbsolutePath()) );
			return WizardPanelNavResult.REMAIN_ON_PAGE;
		}
		
		String defaultConfDir = System.getProperty("os.name").toLowerCase().contains("windows") ?
				"C:\\Program files\\rega_institute\\regadb":
				"/etc/rega_institute/regadb";
		
		if ( !configDir.getAbsolutePath().equals(defaultConfDir) ) {
			String msg = tr("directory_NonDefaultConfigDir");
			int result = JOptionPane.showConfirmDialog(this, msg, "",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
			if ( result == JOptionPane.CANCEL_OPTION ) {
				configPick.getTextField().setText(defaultConfDir);
				return WizardPanelNavResult.REMAIN_ON_PAGE;
			}
		}
		
		return WizardPanelNavResult.PROCEED;
	}
	
	//TODO may go to FileUtils
	private static File getFirstExistingDir(File dir) {
		if ( dir.exists() ) {
			return dir;
		} else {
			String path = dir.getAbsolutePath();
			return getFirstExistingDir( new File( path.substring(0, path.lastIndexOf( File.separator ) ) ) );
		}
	}
	
	public static String getDescription() {
		return tr("stepDirectory");
	}
}
