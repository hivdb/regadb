/*
 * FloatSetting.java
 *
 * Created on March 5, 2001, 5:09 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.io.*;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JTextField;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

//import com.pharmadm.util.resource.MediaInterface;
/**
 *
 * @author  kdg
 * @version
 */
public class FloatSetting extends Setting {
    
    public FloatSetting(XMLSettings xs, String name) {
        super(xs, name);
    }
    
    public FloatSetting(XMLSettings xs, String name, Float defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    public boolean setValue(Object o) {
        try {
            super.setValue((Float) o);
        } catch (ClassCastException cce) {
            return false;
        }
        return true;
    }
    
    public boolean read(String s) {
        try {
            if (s.equals("null")) {
                setValue(null);
            } else {
                setValue(Float.valueOf(s));
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public boolean write(PrintStream writer) {
        writer.print(getValue());
        return true;
    }
    
    public float getFloatValue() {
        return ((Float)getValue()).floatValue();
    }
    
    protected JComponent getConfigurationControlImpl(JDialog parent) {
        final JTextField textField = new JTextField();
        textField.setText(getValue().toString());
        textField.setEditable(true);
        
        final SingleSettingConfigurationController controller = new SingleSettingConfigurationController() {
            
            public void defaultValue() {
                textField.setText(getDefaultValue().toString());
            }
            
            public void reset() {
                textField.setText(getValue().toString());
                setDirty(false);
            }
            
            protected void commitImpl() {
                read(textField.getText());
            }
            
        };
        
        DocumentListener documentListener = new DocumentListener() {
            
            public void changedUpdate(DocumentEvent e) {
                controller.setDirty(true);
            }
            
            public void insertUpdate(DocumentEvent e) {
                controller.setDirty(true);
            }
            
            public void removeUpdate(DocumentEvent e) {
                controller.setDirty(true);
            }
        };
        
        textField.getDocument().addDocumentListener(documentListener);
        
        setConfigurationController(controller);
        
        return textField;
    }
    
}
