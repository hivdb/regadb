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
package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;
import javax.swing.JFormattedTextField;

import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.SuggestedValuesOption;

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
        setEditable(constant.areSuggestedValuesMandatory());
    }
    
    public ConfigurableWord getWord() {
        return constant;
    }
    
    public void configureWord() {
        Object valueToSet = null;
        if (getSelectedItem() != null) {
            valueToSet = ((JDBCComboBoxModel)getModel()).getSelectedValue();
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
        private ArrayList<SuggestedValuesOption> values;
        private SuggestedValuesOption selectedItem;
        public JDBCComboBoxModel(JFormattedTextField textField) {
            values = constant.getSuggestedValuesList();
        	setSelectedItem(textField.getValue());
        }
        
        public void addListDataListener(javax.swing.event.ListDataListener l) {
            // there are no changes, so there's nothing to listen to.
        }
        
        public Object getElementAt(int index) {
            Object value = null;
            if (values != null) {
                value = values.get(index).getOption();
            }
            return value;
        }
        
        public Object getSelectedItem() {
            return selectedItem.getOption();
        }
        
        public Object getSelectedValue() {
        	return selectedItem.getValue();
        }
        
        public int getSize() {
            return values.size();
        }
        
        public void removeListDataListener(javax.swing.event.ListDataListener l) {
            // there are no changes, so there's nothing to listen to.
        }
        
        public void setSelectedItem(Object anItem) {
    		boolean found = false;

    		if (anItem != null) {
    			int i = 0;
    			while (!found && i < values.size()) {
    				SuggestedValuesOption option = values.get(i);
        			if (option.getOption().equals(anItem) || option.getValue().equals(anItem)) {
        				this.selectedItem = option;
        				found = true;
        			}
        			i++;
        		}
        	}
            	
            if (!found && values.size() > 0){
        		this.selectedItem = values.get(0);
        	}
        }
        
        public void close() {
        }
    }

	@Override
	public void add(List<WordConfigurer> words) {
	}

	@Override
	public void reAssign(Object o) {
		JConstantChoiceConfigurer confy = (JConstantChoiceConfigurer) o;
		this.controller = confy.controller;
		this.constant = confy.constant;
		this.textField = confy.textField;
		this.setSelectedIndex(confy.getSelectedIndex());
	}
}
