/*
 * JFromVariableConfigurer.java
 *
 * Created on September 1, 2003, 2:50 PM
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

/**
 *
 * @author  kristof
 */
public class JObjectListVariableConfigurer extends javax.swing.JLabel implements WordConfigurer {
    
    private ObjectListVariable var;
    
    /** 
     * <p>
     * Creates a new instance of JObjectListVariableConfigurer to show and configure a 
     * particular ObjectListVariable through a given ConfigurationController controller
     * </p>
     * <p>
     * @param var The ObjectListVariable that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     */
    public JObjectListVariableConfigurer(ObjectListVariable var, ConfigurationController controller) {
        super(var.getUniqueName());
        this.var = var;
    }
    
    /** does nothing, objectList variables can not be configured */
    public void configureWord() {
    }
    
    public ConfigurableWord getWord() {
        return var;
    }
    
    public void freeResources() {
        // this class uses no database resources
    }

	@Override
	public void add(List<WordConfigurer> words) {
	}

	@Override
	public int getSelectedIndex() {
		return 0;
	}

	@Override
	public void reAssign(Object o) {
		// does nothing. no configuration required
	}
    
}
