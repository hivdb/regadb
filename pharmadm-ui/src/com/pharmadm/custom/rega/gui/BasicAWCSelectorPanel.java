/*
 * AtomicWhereClauseSelectorPanel.java
 *
 * Created on September 3, 2003, 2:00 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;

/**
 * A basic AWC Selector panel can hold only one AWC and no subpanels
 * @author  kristof
 */
public class BasicAWCSelectorPanel extends AWCSelectorPanel {
    
    protected AWCEditorPanel editPanel;
    private JRadioButton radioButton;
    protected QueryContext context;
	
    /** Creates new form AtomicWhereClauseSelectorPanel */
    public BasicAWCSelectorPanel(QueryContext context, AtomicWhereClause clause) {
		this.radioButton = new JRadioButton();
		this.context = context;
		this.editPanel = new AWCEditorPanel(new AtomicWhereClauseEditor(context, clause));
		initMoreComponents();
    }
    
    protected AWCEditorPanel getEditorPanel() {
        return editPanel;
    }

	@Override
	/**
	 * can not add additional panels to this panel
	 */
	public boolean addSelectorPanel(AWCSelectorPanel panel) {
		return false;
	}

	@Override
	public AWCEditorPanel getSelectedClause() {
		if (isSelected()) {
			return editPanel;
		}
		return null;
	}
	
	@Override
	public List<JRadioButton> getRadioButtons() {
		ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
		buttons.add(radioButton);
		return buttons;
	}
	
	@Override
    public boolean isSelected() {
        return radioButton.isSelected();
    }
    
	@Override
    public void freeResources() {
        editPanel.freeResources();
    }	
    
	/**
	 * can not add additional clauses to this panel
	 */
	public boolean addAtomicWhereClause(AtomicWhereClause clause) {
		return false;
	}
	
    private void initMoreComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        
        add(radioButton, gridBagConstraints);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.gridx = 1;
        add(editPanel, gridBagConstraints);

        
        gridBagConstraints.gridx = 2;
        gridBagConstraints.weightx = 100;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        JPanel spacerPanel = new JPanel();
        add(spacerPanel, gridBagConstraints);
        
        editPanel.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent fe) {
                radioButton.setSelected(true);
            }
            public void focusLost(FocusEvent fe) {
            }
        });
        
        editPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                radioButton.setSelected(true);
            }
        });
    }    
}
