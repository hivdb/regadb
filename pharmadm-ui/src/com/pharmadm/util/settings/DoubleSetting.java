/*
 * DoubleSetting.java
 *
 * Created on August 7, 2002, 11:29 AM
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
import com.pharmadm.util.MStreamTokenizer;

/**
 *
 * @author  alexanderd
 */
public class DoubleSetting extends Setting {
    
    public DoubleSetting(XMLSettings xs, String name) {
        super(xs, name);
    }
    
    public DoubleSetting(XMLSettings xs, String name, Double defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    public boolean setValue(Object o) {
        try {
            super.setValue((Double) o);
        } catch (ClassCastException cce) {
            return false;
        }
        return true;
    }
    
    public boolean read(MStreamTokenizer tokens) {
        try {
            String token = tokens.readToken();
            return read(token);
        } catch (IOException ioe) {
            return false;
        }
    }
    
    public double doubleValue() {
        Double val = ((Double)getValue());
        if (val != null) {
            return val.doubleValue();
        } else {
            val = (Double)getDefaultValue();
            if (val != null) {
                return val.doubleValue();
            } else {
                return Double.NaN;
            }
        }
    }
    
    public boolean read(String s) {
        try {
            if (s.equals("null")) {
                setValue(null);
            } else {
                setValue(Double.valueOf(s));
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
