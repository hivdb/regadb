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
package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.*;
/**
 *
 * @author  kristof
 */
public class JFromVariableConfigurer extends javax.swing.JLabel implements WordConfigurer {
    
    private FromVariable var;
    
    /** 
     * <p>
     * Creates a new instance of JFromVariableConfigurer to show and configure a 
     * particular FromVariable through a given ConfigurationController controller
     * </p>
     * <p>
     * @param var The FromVariable that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     */
    public JFromVariableConfigurer(FromVariable var) {
        super(var.getUniqueName());
        this.var = var;
    }
    
    /** does nothing, from variables can not be configured */
    public void configureWord() {
    }
    
    public ConfigurableWord getWord() {
        return var;
    }
    
    public void freeResources() {
        // this class uses no database resources
    }

	public void add(List<WordConfigurer> words) {
	}

	public int getSelectedIndex() {
		return 0;
	}

	public void reAssign(Object o) {
		// does nothing. no configuration required
	}
    
}
