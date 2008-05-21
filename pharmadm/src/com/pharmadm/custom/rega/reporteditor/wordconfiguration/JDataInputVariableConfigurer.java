/*
 * JInputVariableConfigurer.java
 *
 * Created on September 1, 2003, 2:29 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor.wordconfiguration;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;
import com.pharmadm.custom.rega.reporteditor.DataInputVariable;
import com.pharmadm.custom.rega.reporteditor.DataInputVariableController;
import com.pharmadm.custom.rega.reporteditor.DataOutputVariable;
import com.pharmadm.custom.rega.reporteditor.OutputReportSeeder;


/**
 *
 * @author  kristof
 */
public class JDataInputVariableConfigurer extends javax.swing.JComboBox implements WordConfigurer {
    
    private DataInputVariable var;
    private DataInputVariableController controller;
    
    /** 
     * <p>
     * Creates a new instance of JDataInputVariableConfigurer to show and configure
     * a particular DataInputVariable through a given DataInputVariableController
     * controller
     * </p>
     * <p>
     * @param var The DataInputVariable that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     */
    public JDataInputVariableConfigurer(DataInputVariable input, DataInputVariableController controller, final OutputReportSeeder seedController) {
        super(controller.getCompatibleOutputVariables(input).toArray(new DataOutputVariable[0]));
        this.var = input;
        this.controller = controller;
        if (seedController != null) {
            this.setRenderer(new javax.swing.DefaultListCellRenderer() {
                public java.awt.Component getListCellRendererComponent(javax.swing.JList jList, Object value, int index, boolean sel, boolean hasFocus) {
                    super.getListCellRendererComponent(jList, value, index, sel, hasFocus);
                    if (value != null) {
                        setText(((DataOutputVariable)value).getHumanStringValue(seedController));
                    }
                    return this;
                }
            });
        }
    }
    
    public ConfigurableWord getWord() {
        return var;
    }    
    
    public void configureWord() {
        controller.assignOutputVariable(var, (DataOutputVariable)getSelectedItem());
    }    

	public void add(List<WordConfigurer> words) {
	}

	public void reAssign(Object o) {
		JDataInputVariableConfigurer confy = (JDataInputVariableConfigurer) o;
		this.controller = confy.controller;
		this.var = confy.var;
	}

	public boolean isUseless() {
		return false;
	}
}
