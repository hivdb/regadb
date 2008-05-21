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
package com.pharmadm.custom.rega.reporteditor.wordconfiguration;

import java.util.List;

import com.pharmadm.custom.rega.reporteditor.*;
import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConfigurationController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

/**
 *
 * @author  kristof
 */
public class JDataOutputVariableConfigurer extends javax.swing.JLabel implements WordConfigurer {
    
    private DataOutputVariable var;
    
    /** 
     * <p>
     * Creates a new instance of JDataOutputVariableConfigurer to show and configure
     * a particular DataOutputVariable through a given ConfigurationController
     * controller
     * </p>
     * <p>
     * @param var The DataOutputVariable that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     */
    public JDataOutputVariableConfigurer(DataOutputVariable var, ConfigurationController controller) {
        super(var.getUniqueName());
        this.var = var;
    }
    
    public ConfigurableWord getWord() {
        return var;
    }
    
    /** does nothing, output variables can not be configured */
    public void configureWord() {
    }


	public void add(List<WordConfigurer> words) {
	}

	public int getSelectedIndex() {
		return 0;
	}

	public void reAssign(Object o) {
		// does nothing no configuration required
	}

	public boolean isUseless() {
		return false;
	}
    
}
