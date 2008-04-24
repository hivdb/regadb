package com.pharmadm.custom.rega.gui;

import java.awt.Frame;

import javax.swing.JButton;

import com.pharmadm.custom.rega.queryeditor.SimpleQuery;

public class SQLEditor extends SQLViewer {

	public SQLEditor(Frame parent, boolean modal, String sqlQuery) {
		super(parent, modal, sqlQuery);
		setEditable(true);
		
		JButton runButton = new JButton();
		runButton.setMnemonic('r');
		runButton.setText("Run Query");
		runButton.addActionListener(new java.awt.event.ActionListener() {
	        public void actionPerformed(java.awt.event.ActionEvent evt) {
	            runQuery(evt);
	        }
	    });
		addButton(runButton);
		
	}
	
	private void runQuery(java.awt.event.ActionEvent evt) {
		QueryEditorFrame frame = (QueryEditorFrame) getParent();
		frame.runQuery(new SimpleQuery(getQuery()));
	}

}
