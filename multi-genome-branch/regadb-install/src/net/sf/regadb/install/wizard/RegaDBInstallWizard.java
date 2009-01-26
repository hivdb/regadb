package net.sf.regadb.install.wizard;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.sf.regadb.install.wizard.steps.AccountStep;
import net.sf.regadb.install.wizard.steps.DatabaseStep;
import net.sf.regadb.install.wizard.steps.DeploymentStep;
import net.sf.regadb.install.wizard.steps.DirectoryStep;
import net.sf.regadb.install.wizard.steps.ProxyStep;
import net.sf.regadb.swing.i18n.I18n;
import net.sf.regadb.util.pair.Pair;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardPage;

public class RegaDBInstallWizard extends JFrame {
	private static final long serialVersionUID = 1647354401478788957L;
	private ArrayList<Pair<String, String>> locales = new ArrayList<Pair<String, String>>();
	private JComboBox langs;
	
	public static void main(String[]args) {
		new RegaDBInstallWizard();
	}
	
	public RegaDBInstallWizard() {
		int width = 350, height = 80;
		setSize(width, height);
		
		setLocation((getToolkit().getScreenSize().width - width) / 2,
				(getToolkit().getScreenSize().height - height) / 2);
		
		setResizable(false);
		setLayout(new FlowLayout());
		
		add(new JLabel("Select a language to continue installing RegaDB."));
		
		locales.add(new Pair<String, String>("en-us", "English"));
		
		langs = new JComboBox();
		for( Pair<String, String> p : locales ) {
			langs.addItem(p.getValue());
		}
		add(langs);
		
		JButton ok = new JButton("continue");
		add(ok);
		
		ok.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				I18n.setBundleURI("net.sf.regadb.install.wizard.I18n.resource");
				
				setVisible(false);
				
				Wizard wiz = WizardPage.createWizard(
						new Class[] {
								DatabaseStep.class,
								AccountStep.class,
								DirectoryStep.class,
								ProxyStep.class,
								DeploymentStep.class,
								}, new RegaDBInstallHandler());
				
				int width = 800;
				WizardDisplayer.showWizard(wiz, new Rectangle(width, (int)(width / 1.61803399)));
				
				dispose();
			}
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
}
