/*
 * AtomicClauseSelectionDialog.java
 *
 * Created on September 2, 2003, 7:16 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClauseEditor;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.QueryEditor;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.gui.VisualizationComponentFactory;

/**
 *
 * @author  kristof
 */
public class AtomicClauseSelectionDialog extends JDialog {
    
    /** Creates new form AtomicClauseSelectionDialog */
    public AtomicClauseSelectionDialog(java.awt.Frame parent, QueryEditor queryEditor, WhereClause contextClause, Collection<AtomicWhereClause> clauseList, boolean modal) {
        super(parent, modal);
        initComponents();
        initListComponents(queryEditor, contextClause, clauseList);
        getRootPane().setDefaultButton(okButton);
        pack();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();

        setTitle("Select Query Component to Add");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(600, 570));
        getContentPane().add(jScrollPane2, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents
    
    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        freeResources();
    }//GEN-LAST:event_formWindowClosed
    
    private void initListComponents(QueryEditor queryEditor, WhereClause contextClause, Collection<AtomicWhereClause> clauseList) {
        editPanel = new ScrollableEditPanel();
        editPanel.setLayout(new java.awt.GridBagLayout());
        jScrollPane2.setViewportView(editPanel);
        
        Iterator<AtomicWhereClause> iter = clauseList.iterator();
        selectorList = new ArrayList<AtomicWhereClauseSelectorPanel>();
        
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        editPanel.add(new javax.swing.JLabel(""),gridBagConstraints);
        
        HashMap<String, CollapsableSelectPanel> panels = new HashMap<String, CollapsableSelectPanel>(); 
        panels.put("new", getNewPanel("new"));
        
        while (iter.hasNext()) {
            AtomicWhereClause clause = (AtomicWhereClause)iter.next();
            ArrayList<String> titles = getTitles(clause);
            if (titles.isEmpty()) {
            	panels.get("new").addPanel(getSelectorPanel(clause, queryEditor, contextClause));
            }
            else {
            	Iterator<String> it = titles.iterator();
            	while (it.hasNext()){
            		String title = it.next();
            		if (panels.containsKey(title)) {
            			panels.get(title).addPanel(getSelectorPanel(clause, queryEditor, contextClause));
            		}
            		else {
            	        panels.put(title, getNewPanel(title));
            			panels.get(title).addPanel(getSelectorPanel(clause, queryEditor, contextClause));
            		}
            	}
            }
/*            
            AtomicWhereClause clause = (AtomicWhereClause)iter.next();
            AtomicWhereClauseEditor editor = new AtomicWhereClauseEditor(queryEditor);
            editor.setAtomicWhereClause(clause);
            editor.setContextClause(contextClause);
            editor.setVisualizationComponentFactory(new VisualizationComponentFactory(editor));
            javax.swing.JRadioButton jRadioButton = new javax.swing.JRadioButton();
            AtomicWhereClauseSelectorPanel selectorPanel = new AtomicWhereClauseSelectorPanel(jRadioButton, editor);
            selectorList.add(selectorPanel);
            MouseListener doubleClickListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 2) {
                        okButtonActionPerformed(null);
                    }
                }
            };
            selectorPanel.addMouseListener(doubleClickListener);
            jRadioButton.addMouseListener(doubleClickListener);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
            gridBagConstraints.weightx = 1;
            gridBagConstraints.weighty = 1;
            editPanel.add(selectorPanel,gridBagConstraints);
            buttonGroup1.add(jRadioButton);
            //jScrollPane2.setViewport(jScrollPane2.getViewport());
             */
        }
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        editPanel.add(new javax.swing.JLabel(""),gridBagConstraints);
    }
    
    private CollapsableSelectPanel getNewPanel(String title) {
    	CollapsableSelectPanel panel = new CollapsableSelectPanel(title);
    	GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        editPanel.add(panel ,gridBagConstraints);
        return panel;
    }
    
    private ArrayList<String> getTitles(AtomicWhereClause clause) {
    	ArrayList<String> list = new ArrayList<String>();
    	Iterator<InputVariable> it = clause.getInputVariables().iterator();
    	while (it.hasNext()) {
    		list.add(it.next().getVariableType().getName());
    	}

    	return list;
    }
    
    private AtomicWhereClauseSelectorPanel getSelectorPanel(AtomicWhereClause clause, QueryEditor queryEditor, WhereClause contextClause) {
        AtomicWhereClauseEditor editor = new AtomicWhereClauseEditor(queryEditor);
        editor.setAtomicWhereClause(clause);
        editor.setContextClause(contextClause);
        editor.setVisualizationComponentFactory(new VisualizationComponentFactory(editor));
        javax.swing.JRadioButton jRadioButton = new javax.swing.JRadioButton();
        AtomicWhereClauseSelectorPanel selectorPanel = new AtomicWhereClauseSelectorPanel(jRadioButton, editor);
        selectorList.add(selectorPanel);
        MouseListener doubleClickListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    okButtonActionPerformed(null);
                }
            }
        };
        selectorPanel.addMouseListener(doubleClickListener);
        jRadioButton.addMouseListener(doubleClickListener);
        buttonGroup1.add(jRadioButton);

    	
        return selectorPanel;
    }
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        // Add your handling code here:
        cancelSelection();
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Add your handling code here:
        applySelection();
        setVisible(false);
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed
    
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    public WhereClause getSelectedClause() {
        return selectedClause;
    }
    
    private void applySelection() {
        Iterator<AtomicWhereClauseSelectorPanel> iter = selectorList.iterator();
        while(iter.hasNext()) {
            AtomicWhereClauseSelectorPanel selector = iter.next();
            if (selector.getRadioButton().isSelected()) {
                selector.getEditorPanel().applyEditings();
                selectedClause = selector.getEditorPanel().getClause();
                return;
            }
        }
        selectedClause = null;
    }
    
    private void freeResources() {
        Iterator<AtomicWhereClauseSelectorPanel> iter = selectorList.iterator();
        while(iter.hasNext()) {
            AtomicWhereClauseSelectorPanel selector = iter.next();
            selector.freeResources();
        }
    }
    
    
    private void cancelSelection() {
        selectedClause = null;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
    private AtomicWhereClause selectedClause = null;
    private Collection<AtomicWhereClauseSelectorPanel> selectorList;
    private javax.swing.JPanel editPanel;
    
    private static class ScrollableEditPanel extends JPanel implements Scrollable {
        
        public java.awt.Dimension getPreferredScrollableViewportSize() {
            return new Dimension(580, 560);
        }
        
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            Dimension size = getSize();
            if (orientation == SwingConstants.HORIZONTAL) {
                return (int)(size.getWidth() / 3);
            } else {
                return (int)(size.getHeight() / 3);
            }
        }
        
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
        
        public boolean getScrollableTracksViewportWidth() {
            return false;
        }
        
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }
        
    }
}
