/*
 * SelectDescriptionComponentsStep.java
 *
 * Created on November 18, 2003, 11:15 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.fastaexport;

import java.awt.GridBagConstraints;
import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pharmadm.util.gui.wizard.*;

/**
 *
 * @author  kdg
 */
public class SelectDescriptionComponentsStep extends JPanel implements Step {
    
    private Wizard wizard;
    private FastaExporter fastaExporter;
    private PreviewJPanel previewPanel;
    
    /**
     * Creates new form SelectDescriptionComponentsStep
     * For testing purposes only.
     */
    private SelectDescriptionComponentsStep() {
        initComponents();
        DefaultListModel excludeModel = new DefaultListModel();
        DefaultListModel includeModel = new DefaultListModel();
        excludeModel.addElement("Test");
        excludeModel.addElement("Aha");
        excludeModel.addElement("Uhu");
        excludeModel.addElement("SomeColumnName");
        excludeJList.setModel(excludeModel);
        includeJList.setModel(includeModel);
        initMoreComponents();
    }
    
    protected SelectDescriptionComponentsStep(FastaExporter fastaExporter, TableModel tableModel) {
        this.fastaExporter = fastaExporter;
        initComponents();
        DefaultListModel excludeModel = new DefaultListModel();
        DefaultListModel includeModel = new DefaultListModel();
        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            if (!fastaExporter.isSequenceColumn(tableModel, col)) {
                String name = tableModel.getColumnName(col);
                ColumnInfo colInfo = new ColumnInfo(col, name);
                excludeModel.addElement(colInfo);
            }
        }
        excludeJList.setModel(excludeModel);
        includeJList.setModel(includeModel);
        initMoreComponents();
    }
    
    /**
     * Moves the selected items in the fromList to the toList.
     * @pre both given JLists must have DefaultListModels.
     */
    private void moveSelectedItems(JList fromList, JList toList) {
        int[] selectedIndices = fromList.getSelectedIndices();
        DefaultListModel toModel = (DefaultListModel)toList.getModel();
        DefaultListModel fromModel = (DefaultListModel)fromList.getModel();
        for (int i = 0; i < selectedIndices.length; i++) {
            toModel.addElement(fromModel.getElementAt(selectedIndices[i]));
        }
        // remove in reverse order!
        for (int i = (selectedIndices.length - 1); i >= 0; i--) {
            fromModel.remove(selectedIndices[i]);
        }
    }
    
    private void moveSelectedItemsUp(JList list) {
        int[] selectedIndices = list.getSelectedIndices();
        DefaultListModel model = (DefaultListModel)list.getModel();
        // Skip first continuous selection range that preceeds all unselected items.
        // This range is "fixed" against the top of the list, it can't move up.
        int nbNotMovingSelectedItems = 0;
        while ((nbNotMovingSelectedItems < selectedIndices.length) && (selectedIndices[nbNotMovingSelectedItems] == nbNotMovingSelectedItems)) {
            nbNotMovingSelectedItems++;
        }
        for (int i = nbNotMovingSelectedItems; i < selectedIndices.length; i++) {
            int itemIndex = selectedIndices[i];
            Object item = model.remove(selectedIndices[i]);
            selectedIndices[i]--;
            model.insertElementAt(item, selectedIndices[i]);
        }
        list.setSelectedIndices(selectedIndices);
    }
    
    private void moveSelectedItemsDown(JList list) {
        int[] selectedIndices = list.getSelectedIndices();
        int nbSelected = selectedIndices.length;
        DefaultListModel model = (DefaultListModel)list.getModel();
        // Skip last continuous selection range that succeeds all unselected items.
        // This range is "fixed" against the bottom of the list, it can't move down.
        int nbNotMovingSelectedItems = 0;
        int indexInSelectionArray = (nbSelected - 1);
        while ((indexInSelectionArray >= 0) &&
        (selectedIndices[indexInSelectionArray] == ((model.size() - 1) - nbNotMovingSelectedItems))) {
            nbNotMovingSelectedItems++;
            indexInSelectionArray--;
        }
        for (int i = ((nbSelected - 1) - nbNotMovingSelectedItems); i >= 0; i--) {
            int itemIndex = selectedIndices[i];
            Object item = model.remove(selectedIndices[i]);
            selectedIndices[i]++;
            model.insertElementAt(item, selectedIndices[i]);
        }
        list.setSelectedIndices(selectedIndices);
    }
    
    private void updateFastaExporter() {
        ListModel includeModel = includeJList.getModel();
        int[] indices = new int[includeModel.getSize()];
        for (int i = 0; i < includeModel.getSize(); i++) {
            ColumnInfo tCol= (ColumnInfo)includeModel.getElementAt(i);
            indices[i] = tCol.getColumnId();
        }
        fastaExporter.setDescriptionColumnIds(indices);
    }
    
    private void initMoreComponents() {
        final PreviewJPanel previewPanel = new PreviewJPanel(fastaExporter);
        this.previewPanel = previewPanel;
        GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        columnSelectionJPanel.add(previewPanel, gridBagConstraints);
        
        includeJList.getModel().addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {
                updateFastaExporter();
                previewPanel.recalc();
            }
            public void intervalAdded(ListDataEvent e) {
                updateFastaExporter();
                previewPanel.recalc();
            }
            public void intervalRemoved(ListDataEvent e) {
                updateFastaExporter();
                previewPanel.recalc();
            }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        columnSelectionJPanel = new javax.swing.JPanel();
        excludeScrollPane = new javax.swing.JScrollPane();
        excludeJList = new javax.swing.JList();
        includeScrollPane = new javax.swing.JScrollPane();
        includeJList = new javax.swing.JList();
        addJButton = new javax.swing.JButton();
        removeJButton = new javax.swing.JButton();
        moveUpJButton = new javax.swing.JButton();
        moveDownJButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        columnSelectionJPanel.setLayout(new java.awt.GridBagLayout());

        excludeScrollPane.setViewportView(excludeJList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        columnSelectionJPanel.add(excludeScrollPane, gridBagConstraints);

        includeScrollPane.setViewportView(includeJList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        columnSelectionJPanel.add(includeScrollPane, gridBagConstraints);

        addJButton.setMnemonic('>');
        addJButton.setText(">");
        addJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addJButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 5, 0);
        columnSelectionJPanel.add(addJButton, gridBagConstraints);

        removeJButton.setMnemonic('<');
        removeJButton.setText("<");
        removeJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeJButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        columnSelectionJPanel.add(removeJButton, gridBagConstraints);

        moveUpJButton.setMnemonic('U');
        moveUpJButton.setText("Move up");
        moveUpJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpJButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(25, 0, 5, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        columnSelectionJPanel.add(moveUpJButton, gridBagConstraints);

        moveDownJButton.setMnemonic('D');
        moveDownJButton.setText("Move down");
        moveDownJButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownJButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        columnSelectionJPanel.add(moveDownJButton, gridBagConstraints);

        add(columnSelectionJPanel, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    private void moveDownJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownJButtonActionPerformed
        moveSelectedItemsDown(includeJList);
    }//GEN-LAST:event_moveDownJButtonActionPerformed
    
    private void moveUpJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpJButtonActionPerformed
        moveSelectedItemsUp(includeJList);
    }//GEN-LAST:event_moveUpJButtonActionPerformed
    
    private void removeJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeJButtonActionPerformed
        moveSelectedItems(includeJList, excludeJList);
    }//GEN-LAST:event_removeJButtonActionPerformed
    
    private void addJButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addJButtonActionPerformed
        moveSelectedItems(excludeJList, includeJList);
    }//GEN-LAST:event_addJButtonActionPerformed
    
    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        java.awt.Container contentPane = jFrame.getContentPane();
        contentPane.add(new SelectDescriptionComponentsStep());
        jFrame.pack();
        jFrame.show();
    }
    
    public boolean allowsBack() {
        return true;
    }
    
    public boolean allowsNextOrFinish() {
        return true;
    }
    
    public JPanel getContentPanel() {
        updateFastaExporter();
        previewPanel.recalc();
        return this;
    }
    
    public String getMainInstructions() {
        return "Select the columns to use in the description line.\nYou can also change the order in which they occur.\nUse shift to select ranges and control to select multiple items.";
    }
    
    public String getStepDescription() {
        return "Describe sequence";
    }
    
    public String getSubtitle() {
        return "Description line";
    }
    
    public void onBack() {
    }
    
    public void onNextOrFinish() {
    }
    
    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addJButton;
    private javax.swing.JPanel columnSelectionJPanel;
    private javax.swing.JList excludeJList;
    private javax.swing.JScrollPane excludeScrollPane;
    private javax.swing.JList includeJList;
    private javax.swing.JScrollPane includeScrollPane;
    private javax.swing.JButton moveDownJButton;
    private javax.swing.JButton moveUpJButton;
    private javax.swing.JButton removeJButton;
    // End of variables declaration//GEN-END:variables
    
}
