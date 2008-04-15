/*
 * JFixedStringConfigurer.java
 *
 * Created on September 1, 2003, 2:49 PM
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
public class JFixedStringConfigurer extends javax.swing.JLabel implements WordConfigurer {
    
    private FixedString string;
    
    /** 
     * <p>
     * Creates a new instance of JFixedStringConfigurer to show and configure
     * a particular FixedString through a given ConfigurationController
     * controller
     * </p>
     * <p>
     * @param var The FixedString that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     */
    public JFixedStringConfigurer(FixedString string, ConfigurationController controller) {
        super(string.getHumanStringValue());
        this.string = string;
    }
    
    /** does nothing, fixed strings can not be configured */
    public void configureWord() {
    }
    
    public ConfigurableWord getWord() {
        return string;
    }
    
    public void freeResources() {
        // this class uses no database resources
    }
    
}
