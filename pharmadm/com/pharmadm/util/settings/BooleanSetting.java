/*
 * BooleanSetting.java
 *
 * Created on February 12, 2001, 5:17 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.*;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;

//import com.pharmadm.util.resource.MediaInterface;

/**
 *
 * @author  kdg
 * @version 1.0
 */
public class BooleanSetting extends Setting {
    
/*
    public BooleanSetting() {
        super();
    }
    
    public BooleanSetting(XMLSettings xs){
        super(xs);
    }
    */
    public BooleanSetting(XMLSettings xs, String name) {
        super(xs, name);
    }
    
    public BooleanSetting(XMLSettings xs, String name, Object defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    public boolean setValue(Object o) {
        try {
            super.setValue((Boolean)o);
        } catch (ClassCastException cce) {
            return false;
        }
        return true;
    }
    
    public boolean setValue(boolean b) {
        this.setValue(new Boolean(b));
        return true;
    }
    
    public boolean booleanValue(){
        return ((Boolean)this.getValue()).booleanValue();
    }
    
    public boolean read(String s) {
        final String sUpper = s.toUpperCase();
        if (   sUpper.equals("YES")
            || sUpper.equals("TRUE")
            || sUpper.equals("ON")
            || sUpper.equals("1")) {
                setValue(Boolean.TRUE);
            } else if (sUpper.equals("NO")
            || sUpper.equals("FALSE")
            || sUpper.equals("OFF")
            || sUpper.equals("0")) {
                setValue(Boolean.FALSE);
            } else {
                return false;
            }
        return true;
    }
    
    public boolean write(PrintStream writer) {
        writer.print(((getValue().equals(Boolean.TRUE)) ? "Yes" : "No"));
        return true;
    }
    
    protected JComponent getConfigurationControlImpl(JDialog dialog) {
        final JComboBox comboBox = new JComboBox();
        comboBox.setOpaque(false);
        comboBox.addItem(Boolean.TRUE);
        comboBox.addItem(Boolean.FALSE);
        comboBox.setSelectedItem(getValue());
        
        final SingleSettingConfigurationController controller = new SingleSettingConfigurationController() {
            
            public void defaultValue() {
              comboBox.setSelectedItem(getDefaultValue());  
            }
            
            public void reset() {
                comboBox.setSelectedItem(getValue());
                setDirty(false);
            }
            
            protected void commitImpl() {
                setValue(comboBox.getSelectedItem());
            }
            
        };
        
        setConfigurationController(controller);
        
        comboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                controller.setDirty(true);
            }
        });
        
        return comboBox;
    }
}
