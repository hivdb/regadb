/*
 * StringSetting.java
 *
 * Created on February 12, 2001, 5:16 PM
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

public class StringSetting extends Setting {
    
    public StringSetting(XMLSettings xs, String name) {
        super(xs, name);
    }
    
    public StringSetting(XMLSettings xs, String name, String defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    public boolean setValue(Object o) {
        try {
            super.setValue((String) o);
        } catch (ClassCastException cce) {
            return false;
        }
        return true;
    }
    
    public boolean setValue(String s) {
        super.setValue(s);
        return true;
    }
    
    public String stringValue() {
        return (String) getValue();
    }
        
    public boolean read(String s) {
        setValue(s);
        if ((s == null) || (s.equals("null"))) {
            setValue("");
        } else {
            setValue(s);
        }
        return true;
    }
    
    public boolean write(PrintStream writer) {
        if (getValue() == null || getValue().equals("")) {
            writer.println("null");
        } else {
            writer.println( escapeStringToPCDATA((String)getValue() ));
        }
        return true;
    }
    
    protected JComponent getConfigurationControlImpl(JDialog parent) {
        final JTextField textField = new JTextField();
        textField.setText(stringValue());
        textField.setEditable(true);
        
        final SingleSettingConfigurationController controller = new SingleSettingConfigurationController() {
            
            public void defaultValue() {
                textField.setText((String) getDefaultValue());
            }
            
            public void reset() {
                textField.setText(stringValue());
                setDirty(false);
            }
            
            protected void commitImpl() {
                setValue(textField.getText());
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
