/*
 * JConstantConfigurer.java
 *
 * Created on September 1, 2003, 2:22 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.gui.configurers;


import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConstantController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

/**
 *
 * @author  kristof
 */
public class JConstantConfigurer extends javax.swing.JFormattedTextField implements WordConfigurer {
    
    private Constant constant;
    private ConstantController controller;
    
    /** 
     * <p>
     * Creates a new instance of JConstantConfigurer to show and configure
     * a particular Constant through a given ConstantController 
     * controller
     * </p>
     * <p>
     * @param var The Constant that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     */
    public JConstantConfigurer(Constant constant, ConstantController controller) {
        super(constant.getFormat());
        this.constant = constant;
        this.controller = controller;
        this.setColumns(10);
        this.setValue(constant.getHumanValue());
    }
    
    public ConfigurableWord getWord() {
        return constant;
    }
    
    
    public void configureWord() {
    	System.err.println("text:" + getText());
        if (! controller.setConstantValueString(constant, getText())) {
            System.err.println("Warning : word configuration failed !");
        }
    }


	public void reAssign(Object o) {
		JConstantConfigurer confy = (JConstantConfigurer) o;
		this.controller = confy.controller;
		this.constant = confy.constant;
		this.setValue(confy.getValue());
		this.setText(confy.getText());
		
	}
    
    /* %$ this variant bypasses the controller for efficiency/complexity reasons
    public void configureWord() { 
        constant.setValue(this.getValue());
    }
     */
}
