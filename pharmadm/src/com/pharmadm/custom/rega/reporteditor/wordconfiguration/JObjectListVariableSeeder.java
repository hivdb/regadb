/*
 * JObjectListVariableConfigurer.java
 *
 * Created on December 5, 2003, 7:09 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor.wordconfiguration;

import java.util.List;

import com.pharmadm.custom.rega.reporteditor.*;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

/**
 *
 * @author  kristof
 */
public class JObjectListVariableSeeder extends javax.swing.JComboBox implements WordConfigurer {
    
    private ObjectListVariable var;
    private OutputReportSeeder controller;
    
    /** 
     * <p>
     * Creates a new instance of JObjectListVariableSeeder to show and configure a
     * particular ObjectListVariable through a given QueryOutputReportSeeder controller
     * </p>
     * <p>
     * @param var The ObjectListVariable that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     */
    public JObjectListVariableSeeder(ObjectListVariable input, OutputReportSeeder controller) {
        super(controller.getAvailableOutputVariables(input.getObject()).toArray(new OutputVariable[0]));
        this.var = input;
        this.controller = controller;
    }
    
    public ConfigurableWord getWord() {
        return var;
    }
    
    public void configureWord() {
        controller.assign(var, (OutputVariable)getSelectedItem());        
    }


	public void add(List<WordConfigurer> words) {
	}

	public void reAssign(Object o) {
		JObjectListVariableSeeder confy = (JObjectListVariableSeeder) o;
		this.controller = confy.controller;
		this.var = confy.var;
	}

	public boolean isUseless() {
		return false;
	}
}
