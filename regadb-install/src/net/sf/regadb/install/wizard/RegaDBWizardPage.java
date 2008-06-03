package net.sf.regadb.install.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.sf.regadb.swing.i18n.I18n;

import org.netbeans.spi.wizard.WizardPage;

public class RegaDBWizardPage extends WizardPage {
	private static final long serialVersionUID = 119489037513872417L;
	private JPanel body;
	protected int lineNr = 0;
	
	public RegaDBWizardPage(String stepId, String stepName) {
		this(stepId, stepName, "");
	}
	
	public RegaDBWizardPage(String stepId, String stepName, String stepDescription) {
		super(stepId, stepName);
		
		setLayout(new BorderLayout());
		add(new JLabel(stepDescription), BorderLayout.NORTH);
		
		body = new JPanel();
		add(body, BorderLayout.CENTER);
	}
	
	public JTextField getTextFieldByName(String name) {
		return (JTextField)getComponentByName(name);
	}
	public Component getComponentByName(String name) {
		Component c = null;
		for( Component cc : body.getComponents() ) {
			String n = cc.getName();
			if ( n != null && n.equals(name) ) {
				c = cc;
				break;
			}
		}
		return c;
	}
	
	public JComponent getContainer() {
		return body;
	}
	
//	public void addToParent(Component c, Object o) {
//		super.add(c, o);
//	}
	
	public JTextField addLine(String text, String name) {
		return addLine(text, name, "");
	}
	public JTextField addLine(String text, String name, String value) {
		body.add(new JLabel(text), getGridBag(1, lineNr));
		
		JTextField tf;
		if ( text.toLowerCase().contains("pass") || name.toLowerCase().contains("pass") ) {
			tf = new JPasswordField(value);
		} else {
			tf = new JTextField(value);
		}
		
		tf.setName(name);
		tf.setPreferredSize(new Dimension(300, tf.getPreferredSize().height));
		body.add(tf, getGridBag(2, lineNr));
		
		lineNr++;
		return tf;
	}
	
	public GridBagConstraints getGridBag(int x, int y) {
		return getGridBag(x, y, 1, 1);
	}
	public GridBagConstraints getGridBag(int x, int y, int spanx, int spany) {
		return new GridBagConstraints(x, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 5, 5);
	}
	
	public static String tr(String key) {
		return I18n.tr(key);
	}
	public static String getConf(String property) {
		return GlobalConf.getInstance().getProperty(property);
	}
}
