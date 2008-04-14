/*
 * AbstractSetting.java
 *
 * Created on July 17, 2001, 3:18 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.io.PrintStream;

import javax.swing.JComponent;
import javax.swing.JDialog;

import org.w3c.dom.Node;

/**
 *
 * @author  toms
 */
public abstract class AbstractSetting implements Cloneable {
    
    private JComponent control = null;
    private ConfigurationController controller;
    private String name;

    public AbstractSetting() {
    }
    
    public abstract boolean isComposed();
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public abstract boolean readXML(Node node);
    
    public abstract boolean writeXML(PrintStream writer);
    
    public JComponent getConfigurationControl(JDialog parent) {
        if (control == null){
            control = getConfigurationControlImpl(parent);
        }
        return control;
    }
    
    protected abstract JComponent getConfigurationControlImpl(JDialog parent);
    
    public void clearConfigurationControl() {
        control = null;
    }
    
    protected void setConfigurationController(ConfigurationController controller){
        this.controller = controller;
    }
    
    public ConfigurationController getConfigurationController() {
        return controller;
    }
    
    public Object clone() {
        try{
            return super.clone();
        } catch (CloneNotSupportedException cnse) {
            return null;
        }
    }
    
    public boolean isDirty() {
        return controller != null && getConfigurationController().isDirty();
    }
}
