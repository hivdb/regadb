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
package com.pharmadm.custom.rega.gui.configurers;

import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.ComboBoxModel;

import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.SuggestedValuesOption;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConstantController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

/**
 *
 * @author  kdg
 */
public class JConstantChoiceConfigurer extends JComboBox implements WordConfigurer {
    
    private Constant constant;
    private ConstantController controller;
    
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
        setModel(new JDBCComboBoxModel(constant));
        setEditable(!constant.areSuggestedValuesMandatory());
    }
    
    public ConfigurableWord getWord() {
        return constant;
    }
    
    public void configureWord() {
        if (! controller.setConstantValueString(constant, getSelectedItem())) {
            System.err.println("Warning : word configuration failed !");
        }
    }
    
    public void addFocusListener(java.awt.event.FocusListener listener) {
    }
    
    private class JDBCComboBoxModel implements ComboBoxModel {
        private ArrayList<SuggestedValuesOption> values;
        private SuggestedValuesOption selectedItem;
        public JDBCComboBoxModel(Constant constant) {
            values = constant.getSuggestedValuesList();
        	setSelectedItem(constant.getValue());
        }
        
        public void addListDataListener(javax.swing.event.ListDataListener l) {
            // there are no changes, so there's nothing to listen to.
        }
        
        public Object getElementAt(int index) {
            Object value = null;
            if (values != null) {
                value = values.get(index);
            }
            return value;
        }
        
        public Object getSelectedItem() {
            return selectedItem;
        }
        
        public int getSize() {
            return values.size();
        }
        
        public void removeListDataListener(javax.swing.event.ListDataListener l) {
            // there are no changes, so there's nothing to listen to.
        }
        
        public void setSelectedItem(Object anItem) {
        	int index = values.indexOf(anItem);
        	if (index >=0) {
        		selectedItem = values.get(index);
        	}
        	else {
        		selectedItem = values.get(0);
        	}
        }
    }

	public void reAssign(Object o) {
		JConstantChoiceConfigurer confy = (JConstantChoiceConfigurer) o;
		this.controller = confy.controller;
		this.constant = confy.constant;
		this.setSelectedIndex(confy.getSelectedIndex());
	}

	public boolean isUseless() {
		return getModel().getSize() == 0;
	}
}
