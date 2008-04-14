/*
 * IntegerSetting.java
 *
 * Created on February 12, 2001, 5:20 PM
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
 * @version 1.0
 */
public class IntegerSetting extends Setting {
    private int lowerBound;
    private int upperBound;
    private boolean bounded;
    
    public IntegerSetting(XMLSettings xs, String name) {
        super(xs, name);
        this.bounded = false;
    }
    
    public IntegerSetting(XMLSettings xs, String name, Integer defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    public IntegerSetting(XMLSettings xs, String name, Integer defaultValue, int lb, int ub) {
        this(xs, name);
        this.bounded = true;
        this.lowerBound = lb;
        this.upperBound = ub;
        setDefaultValue(defaultValue);
    }
    
    public Integer integerValue() {
        return (Integer) getValue();
    }
    
    public int getIntValue() {
        return ((Integer)getValue()).intValue();
    }
    
    public int intValue() {
        return ((Integer)getValue()).intValue();
    }
    
    public boolean setValue(Object o) {
        try {
            setValue((Integer) o);
        } catch (ClassCastException cce) {
            return false;
        }
        return true;
    }
    
    public boolean setValue(Integer value) {
        if (this.bounded) {
            super.setValue(new Integer(Math.max(this.lowerBound,Math.min(this.upperBound,value.intValue()))));
        } else {
            super.setValue(value);
        }
        return true;
    }
    public boolean increaseInteger(int i) {
        this.setValue(this.getIntValue() + i);
        return true;
    }
    
    public boolean decreaseInteger(int i) {
        this.setValue(this.getIntValue() - i);
        return true;
    }
    
    public boolean increaseInteger() {
        this.setValue(this.getIntValue() + 1);
        return true;
    }
    
    public boolean decreaseInteger() {
        this.setValue(this.getIntValue() - 1);
        return true;
    }
    
    public boolean setValue(int value) {
        setValue(new Integer(value));
        return true;
    }
    
    public boolean read(String s) {
        try {
            if (s.equals("null")) {
                setValue(null);
            } else {
                setValue(Integer.valueOf(s));
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

