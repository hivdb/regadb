package net.sf.regadb.install.wizard.steps;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sf.regadb.install.wizard.RegaDBWizardPage;
import net.sf.regadb.util.pair.Pair;

public class ProxyStep extends RegaDBWizardPage {
	private static final long serialVersionUID = -6705445101469381212L;
	private ArrayList<Pair<String, String>> proxyList = new ArrayList<Pair<String, String>>();
	
	public ProxyStep() {
		super("proxy", tr("stepProxy"), tr("proxy_Description"));
		
		getContainer().setLayout(new GridBagLayout());
		
		addProxyLine();
	}
	
	private void addProxyLine() {
		addProxyLine(null, null);
	}
	private void addProxyLine(String url, String port) {
		proxyList.add(new Pair<String, String>(url, port));
		repaintProxies();
	}
	
	private void repaintProxies() {
		getContainer().removeAll();
		
		getContainer().add(new JLabel(tr("proxy_URL")), getGridBag(1, 0));
		getContainer().add(new JLabel(tr("proxy_Port")), getGridBag(2, 0));
		
		int line = 0;
		for(final Pair<String, String> p : proxyList) {
			String url = p.getKey();
			String port = p.getValue();
			
			line++;
			getContainer().add(new JLabel("Proxy " + line), getGridBag(0, line));
			
			JTextField jurl = new JTextField(url);
			jurl.setName("proxyurl" + line);
			jurl.setPreferredSize(new Dimension(150, jurl.getPreferredSize().height));
			jurl.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					p.setKey( ( (JTextField)e.getComponent() ).getText() );
				}
			});
			getContainer().add(jurl, getGridBag(1, line));
			
			JTextField jport = new JTextField(port);
			jport.setName("proxyport" + line);
			jport.setPreferredSize(new Dimension(40, jport.getPreferredSize().height));
			jport.addKeyListener(new KeyAdapter() {
				public void keyReleased(KeyEvent e) {
					p.setValue( ( (JTextField)e.getComponent() ).getText() );
				}
			});
			getContainer().add(jport, getGridBag(2, line));
			
			final JButton removeButton = new JButton(tr("proxy_remove"));
			removeButton.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if ( removeButton.isEnabled() ) {
						proxyList.remove(p);
						repaintProxies();
					}
				}
			});
			getContainer().add(removeButton, getGridBag(3, line));
			
			if ( proxyList.size() == 1 ) {
				removeButton.setEnabled(false);
			}
		}
		
		final JButton addProxy = new JButton(tr("proxy_add"));
		addProxy.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if ( addProxy.isEnabled() ) {
					addProxyLine();
				}
			}
		});
		getContainer().add(addProxy, getGridBag(3, ++line));
		
		if ( proxyList.size() >= 7 ) {
			addProxy.setEnabled(false);
		}
		
		revalidate();
		repaint();
	}
	
	public static String getDescription() {
		return tr("stepProxy");
	}
}
