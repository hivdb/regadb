package com.pharmadm.custom.rega;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CollapsableSelectPanel extends JPanel {
	
	private JPanel content;

	public CollapsableSelectPanel(String title) {
		setLayout(new BorderLayout());
		

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
		add(titlePanel, BorderLayout.NORTH);
		JButton label = new JButton();
		label.setText(title);
		label.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleContent();
            }
        });
		titlePanel.add(label);
		
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setVisible(false);
		add(content, BorderLayout.CENTER);
	}
	
	public void toggleContent() {
		content.setVisible(!content.isVisible());
		
	}
	
	public void addPanel(AtomicWhereClauseSelectorPanel panel) {
		content.add(panel);
	}
	
}
