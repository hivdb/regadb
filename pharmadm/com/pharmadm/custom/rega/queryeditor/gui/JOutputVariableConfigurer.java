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
package com.pharmadm.custom.rega.queryeditor.gui;

import com.pharmadm.custom.rega.queryeditor.*;

/**
 *
 * @author  kristof
 */
public class JOutputVariableConfigurer extends javax.swing.JLabel implements WordConfigurer {
    
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
    public JOutputVariableConfigurer(OutputVariable var, ConfigurationController controller) {
        super(var.getUniqueName());
        this.var = var;
    }
    
    public ConfigurableWord getWord() {
        return var;
    }
    
    /** does nothing, output variables can not be configured */
    public void configureWord() {
    }
    
    public void freeResources() {
        // this class uses no database resources
    }
    
}
