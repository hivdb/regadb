package net.sf.regadb.install.wizard.steps;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import net.sf.regadb.install.wizard.ChangeListener;
import net.sf.regadb.install.wizard.GlobalConf;
import net.sf.regadb.install.wizard.RegaDBWizardPage;
import net.sf.regadb.util.pair.Pair;

public class ProxyStep extends RegaDBWizardPage {
	private static final long serialVersionUID = -6705445101469381212L;
	private int proxyLineNr = 0;
	private JButton more, less;
	
	public ProxyStep() {
		super("proxy", tr("stepProxy"), tr("proxy_Description"));
		
		getContainer().setLayout(new GridBagLayout());
		
		getContainer().add(new JLabel(tr("proxy_URL")), getGridBag(1, 0));
		getContainer().add(new JLabel(tr("proxy_Port")), getGridBag(2, 0));
		
		more = new JButton(tr("proxy_more"));
		more.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				addProxyLine();
			}
		});
		less = new JButton(tr("proxy_less"));
		less.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				removeLastLine();
			}
		});
		
		addProxyLine();
		
		GlobalConf.getInstance().addListener(new ChangeListener() {
			public void changed() {
				while ( proxyLineNr > 1 ) {
					removeLastLine();
				}
				proxyLineNr = 0;
				for( Pair<String, String> proxy : GlobalConf.getInstance().getProxies() ) {
					addProxyLine(proxy.getKey(), proxy.getValue());
				}
			}
		});
	}
	
	private void addProxyLine() {
		proxyLineNr++;
		
		JLabel lbl = new JLabel("Proxy " + proxyLineNr);
		lbl.setName("proxylabel" + proxyLineNr);
		getContainer().add(lbl, getGridBag(0, proxyLineNr));
		
		JTextField url = new JTextField();
		url.setName("proxyurl" + proxyLineNr);
		url.setPreferredSize(new Dimension(150, url.getPreferredSize().height));
		getContainer().add(url, getGridBag(1, proxyLineNr));
		
		JTextField port = new JTextField();
		port.setName("proxyport" + proxyLineNr);
		port.setPreferredSize(new Dimension(50, url.getPreferredSize().height));
		getContainer().add(port, getGridBag(2, proxyLineNr));
		
		resetButton();
		revalidate();
	}
	
	private void addProxyLine(String proxyUrl, String proxyPort) {
		if ( getComponentByName("proxyurl" + (proxyLineNr + 1)) == null ) {
			addProxyLine();
		} else {
			proxyLineNr++;
		}
		
		getTextFieldByName("proxyurl" + proxyLineNr).setText(proxyUrl);
		getTextFieldByName("proxyport" + proxyLineNr).setText(proxyPort);
	}
	
	private void removeLastLine() {
		if ( proxyLineNr > 1 ) {
			JTextField url = getTextFieldByName("proxyurl" + proxyLineNr);
			JTextField port = getTextFieldByName("proxyport" + proxyLineNr);
			JLabel lbl = (JLabel)getComponentByName("proxylabel" + proxyLineNr);
			
			url.getParent().remove(url);
			port.getParent().remove(port);
			lbl.getParent().remove(lbl);
			
			proxyLineNr--;
			resetButton();
			revalidate();
		}
	}
	
	private void resetButton() {
		if ( less != null && less.getParent() != null ) {
			less.getParent().remove(less);
		}
		if ( more != null && more.getParent() != null ) {
			more.getParent().remove(more);
		}
		getContainer().add(less, getGridBag(1, proxyLineNr + 1));
		getContainer().add(more, getGridBag(2, proxyLineNr + 1));
	}
}
