/*
 * JOutputVariableConfigurer.java
 *
 * Created on September 1, 2003, 2:34 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.gui.configurers;


import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.OutputVariable.RelationDisplay;
import com.pharmadm.custom.rega.queryeditor.OutputVariable.DescriptionDisplay;
import com.pharmadm.custom.rega.queryeditor.OutputVariable.UniqueNameDisplay;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.OutputVariableConfigurer;

/**
 *
 * @author  kristof
 */
public class JOutputVariableConfigurer extends javax.swing.JLabel implements OutputVariableConfigurer {
    
    private OutputVariable var;
    
    /** 
     * <p>
     * Creates a new instance of JOutputVariableConfigurer to show and configure
     * a particular OutputVariable through a given ConfigurationController
     * controller
     * </p>
     * <p>
     * @param var The OutputVariable that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     */
    public JOutputVariableConfigurer(OutputVariable var) {
    	/**
    	 * for display as single output variable
    	 */
        super(var.getName(RelationDisplay.SHOW, DescriptionDisplay.SHOW_WHEN_ASSIGNED, UniqueNameDisplay.SHOW));
        this.var = var;
    }
    
    public ConfigurableWord getWord() {
        return var;
    }
    
    /** does nothing, output variables can not be configured */
    public void configureWord() {
    }


	/**
	 * for display in output variable dropdown
	 */
	public String toString() {
		return var.getName(RelationDisplay.SHOW, DescriptionDisplay.SHOW, UniqueNameDisplay.HIDE);
	}

	public void reAssign(Object o) {
		// does nothing. nothing to configure
	}
    
}
