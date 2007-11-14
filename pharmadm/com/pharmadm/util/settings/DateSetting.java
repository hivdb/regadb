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
public class DateSetting extends Setting {
    
    private static java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
    
    public static java.text.DateFormat getFormat() {
        return format;
    }
    
    public DateSetting(XMLSettings xs, String name) {
        super(xs, name);
    }
    
    public DateSetting(XMLSettings xs, String name, java.util.Date defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    public boolean setValue(Object o) {
        try {
            super.setValue((java.util.Date) o);
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
    
    public long timeValue() {
        java.util.Date val = ((java.util.Date)getValue());
        if (val != null) {
            return val.getTime();
        } else {
            val = (java.util.Date)getDefaultValue();
            if (val != null) {
                return val.getTime();
            } else {
                return Long.MAX_VALUE;
            }
        }
    }
    
    public boolean read(String s) {
        try {
            if (s.equals("null")) {
                setValue(null);
            } else {
                setValue(format.parse(s));
            }
            return true;
        } catch (java.text.ParseException pe) {
            return false;
        }
    }
    
    public boolean write(PrintStream writer) {
        writer.print(format.format(getValue()));
        return true;
    }
    
    protected JComponent getConfigurationControlImpl(JDialog parent) {
        final JTextField textField = new JTextField();
        textField.setText(format.format(getValue()));
        textField.setEditable(true);
        
        final SingleSettingConfigurationController controller = new SingleSettingConfigurationController() {
            
            public void defaultValue() {
                textField.setText(format.format(getDefaultValue()));
            }
            
            public void reset() {
                textField.setText(format.format(getValue()));
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
