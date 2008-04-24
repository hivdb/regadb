package com.pharmadm.custom.rega.gui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;

/**
 * a collapsable select panel can add AWCs by putting them in composed AWC panels
 * @author fromba0
 *
 */
public class CollapsableAWCSelectorPanel extends AWCSelectorPanel{
	
	private JPanel content;
	private List<AWCSelectorPanel> selectorPanels;
	private String title;
	
	private QueryContext context;
	
	public CollapsableAWCSelectorPanel(String title, QueryContext context) {
		this.context = context;
		this.title = title;
		selectorPanels = new ArrayList<AWCSelectorPanel>();

		setLayout(new BorderLayout());
		JButton label = new JButton();
		label.setText(title);
		add(label, BorderLayout.NORTH);
		
		content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		add(content, BorderLayout.CENTER);
		content.setVisible(false);
		
		label.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleContent();
            }
        });
	}
	
	public String getTitle() {
		return title;
	}
	
	private void toggleContent() {
		content.setVisible(!content.isVisible());
	}
	
	@Override
	public boolean addSelectorPanel(AWCSelectorPanel panel) {
		selectorPanels.add(panel);
		content.add(panel);
		return true;
	}

	/**
	 * will add the clause to the first child AWCSelectorPanel that will accept it.
	 * if no suitable child is found a new ComposedAtomicWhereClauseSelectorPanel 
	 * is made for the clause
	 */
	@Override
	public boolean addAtomicWhereClause(AtomicWhereClause clause) {
        for (AWCSelectorPanel selectorPanel : selectorPanels) {
        	if (selectorPanel.addAtomicWhereClause(clause)) {
        		return true;
        	}
        }
        AWCSelectorPanel newPanel = new ComposedAWCSelectorPanel(context, clause);
        return addSelectorPanel(newPanel);
	}
	
	@Override
	public boolean isSelected() {
		boolean selected = false;
		for (AWCSelectorPanel panel : selectorPanels) {
			selected = selected || panel.isSelected();
		}
		return selected;
	}

	@Override
	public AWCEditorPanel getSelectedClause() {
		AWCEditorPanel selectedClause = null;
		for (AWCSelectorPanel panel : selectorPanels) {
			selectedClause = panel.getSelectedClause();
			if (selectedClause != null) {
				return selectedClause;
			}
		}
		return selectedClause;
	}
	
	@Override
    public List<JRadioButton> getRadioButtons() {
    	ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
    	for (AWCSelectorPanel panel : selectorPanels) {
    		buttons.addAll(panel.getRadioButtons());
    	}
    	return buttons;
    }

	@Override
	public void freeResources() {
    	for (AWCSelectorPanel panel : selectorPanels) {
    		panel.freeResources();
    	}
	}
}
