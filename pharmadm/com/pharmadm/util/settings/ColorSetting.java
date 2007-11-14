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
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JColorChooser;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 *
 * @author  kdg
 * @version 1.0
 */
public class ColorSetting extends Setting {
    
    public ColorSetting(XMLSettings xs, String name) {
        super(xs, name);
    }
    
    public ColorSetting(XMLSettings xs, String name, Color defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    
    public Color colorValue() {
        return (Color) getValue();
    }
    
    public boolean setValue(Object o) {
        try {
            setValue((Color) o);
        } catch (ClassCastException cce) {
            return false;
        }
        return true;
    }
    
    public boolean setValue(Color value) {
        super.setValue(value);
        return true;
    }
    
    public boolean read(String s) {
        try {
            if (s.equals("null")) {
                setValue(null);
            } else {
                setValue(new Color(Integer.valueOf(s).intValue()));
            }
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    
    public boolean write(PrintStream writer) {
        writer.print(((Color)getValue()).getRGB());
        return true;
    }
    
    protected JComponent getConfigurationControlImpl(final JDialog dialog) {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints;
        final JLabel label = new JLabel();
        label.setPreferredSize(new java.awt.Dimension(2,2));
        label.setText(" ");
        label.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        label.setOpaque(true);
        label.setBackground(colorValue());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        gridBagConstraints.weightx = 1.0;
        panel.add(label, gridBagConstraints);
        final JButton button = new JButton("Change color");
        button.setPreferredSize(new java.awt.Dimension(5, 5));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panel.add(button, gridBagConstraints);
        
        
        final SingleSettingConfigurationController controller = new SingleSettingConfigurationController() {
            
            public void defaultValue() {
                label.setBackground((Color)getDefaultValue());
            }
            
            public void reset() {
                label.setBackground(colorValue());
                setDirty(false);
            }
            
            protected void commitImpl() {
                setValue(label.getBackground());
            }
            
        };
        
        setConfigurationController(controller);
        
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(dialog,label,controller,evt);
            }
        });
        
        return panel;
    }
    
    private void buttonActionPerformed(JDialog dialog, JLabel label, SingleSettingConfigurationController controller, java.awt.event.ActionEvent evt) {
        Color oldColor = label.getBackground();
        Color newColor = JColorChooser.showDialog(
        dialog,
        "Set Color",
        oldColor);
        if (newColor != null && ! oldColor.equals(newColor)) {
            label.setBackground(newColor);
            controller.setDirty(true);
        }
        
    }
    
}

