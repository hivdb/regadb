/*
 * JConstantChoiceConfigurer.java
 *
 * Created on September 9, 2003, 11:12 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
import javax.swing.JFormattedTextField;

import com.pharmadm.custom.rega.queryeditor.*;

/**
 *
 * @author  kdg
 */
public class JConstantChoiceConfigurer extends JComboBox implements WordConfigurer {
    
    private Constant constant;
    private ConstantController controller;
    private JFormattedTextField textField;
    
    /**
     * <p>
     * Creates a new instance of JConstantChoiceConfigurer to show and configure
     * a particular Constant (with a suggested values query) through a given ConstantController
     * controller.
     * </p>
     * <p>
     * @param var The Constant that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     * @pre constant.getSuggestedValuesQuery() != null
     */
    public JConstantChoiceConfigurer(Constant constant, ConstantController controller) {
        this.controller = controller;
        this.constant = constant;
        textField = new JFormattedTextField(constant.getFormat());
        textField.setValue(constant.getValue());
        setModel(new JDBCComboBoxModel(textField));
        setEditor(new FormattedComboBoxEditor(textField));
        setEditable(true);
    }
    
    public ConfigurableWord getWord() {
        return constant;
    }
    
    public void configureWord() {
        String valueToSet = null;
        if (getSelectedItem() != null) {
            valueToSet = getSelectedItem().toString();
        }
        if (! controller.setConstantValueString(constant, valueToSet)) {
            System.err.println("Warning : word configuration failed !");
        }
    }
    
    public void freeResources() {
        ((JDBCComboBoxModel)getModel()).close();
    }
    
    public void addFocusListener(java.awt.event.FocusListener listener) {
        if (textField == null) {
            return;
        }
        textField.addFocusListener(listener);
    }
    
    private class JDBCComboBoxModel implements ComboBoxModel {
        private ResultSet rs;
        private int rowCount;
        private Object selectedItem;
        public JDBCComboBoxModel(JFormattedTextField textField) {
            selectedItem = textField.getValue();
            try {
                System.err.println("Trying to execute query: " +constant.getSuggestedValuesQuery());
                rs = JDBCManager.getInstance().executeQuery(constant.getSuggestedValuesQuery());
                rs.last();
                this.rowCount = (rs.last() ? rs.getRow() : 0);
                System.err.println("Number of results: " + rowCount);
            } catch (SQLException sqle) {
                System.err.println("Could not fill Combo box with values due to JDBC exception.");
                sqle.printStackTrace();
            }
        }
        
        public void addListDataListener(javax.swing.event.ListDataListener l) {
            // there are no changes, so there's nothing to listen to.
        }
        
        public Object getElementAt(int index) {
            Object value = null;
            if (rs != null) {
                try {
                    rs.absolute(index + 1);
                    value = rs.getObject(1);
                } catch (SQLException sqle) {
                    System.err.println("Could not fetch Combo box value due to JDBC exception.");
                    sqle.printStackTrace();
                }
            }
            return value;
        }
        
        public Object getSelectedItem() {
            return selectedItem;
        }
        
        public int getSize() {
            return rowCount;
        }
        
        public void removeListDataListener(javax.swing.event.ListDataListener l) {
            // there are no changes, so there's nothing to listen to.
        }
        
        public void setSelectedItem(Object anItem) {
            this.selectedItem = anItem;
        }
        
        public void close() {
            //System.err.println("Closing " + rs);
            JDBCManager.getInstance().closeStatement(rs);
        }
        
    }
    
}
