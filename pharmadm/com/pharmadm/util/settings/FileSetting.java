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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

//import com.pharmadm.util.resource.MediaInterface;
//import com.pharmadm.util.file.FileUtil;

/**
 * This Setting is for files only.  It creates files if they do not yet exist when they are requested.
 * For directories, use DirSetting.
 *
 * @author  kdg
 * @version 1.0
 */

public class FileSetting extends Setting {
/*
    public FileSetting() {
        super();
    }
 */
    public FileSetting(XMLSettings xs, String name) {
        super(xs, name);
    }
    
    public FileSetting(XMLSettings xs, String name, File defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    public boolean setValue(Object o) {
        if (o instanceof File) {
            super.setValue((File) o);
        } else if (o instanceof String) {
            super.setValue(new File((String) o));
        } else {
            return false;
        }
        return true;
    }
    
    public boolean read(String s) {
        if (s.equals("null")) {
             setValue(null);
        } else {
             setValue(s);
        }
        return true;
    }
    
    public boolean write(PrintStream writer) {
        writer.println(getValue());
        return true;
    }
    
    public Object getValue() {
        File file = (File) super.getValue();
/*        if (file != null) {
            try {
                if (!file.exists()) {
                    file.createNewFile();
                    System.out.println("New file created: " + file);
                    setValue(file);
                }
            } catch (IOException ioe) {
                System.out.println("Cannot create file " + file);
                setValue(null);
                file = null;
            }
        }
*/        return file;
    }
    
    public File fileValue() {
        return (File) getValue();
    }
    
    protected JComponent getConfigurationControlImpl(final JDialog parent) {
        
        final File file = fileValue();
        
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        panel.setOpaque(false);
                
        final JLabel label = new JLabel("" + file);
        label.setOpaque(false);
        
        JButton button = new JButton("...");
        button.setToolTipText("Select a file");
        button.setMinimumSize(button.getPreferredSize());
        
        button.addActionListener(new ButtonActionListener(label, parent));
        
        panel.add(label);
        panel.add(new Box.Filler(new Dimension(0,0), new Dimension(1000, 1000), new Dimension(1000,1000)));
        panel.add(button);
        
        return panel;
    }
    
    private class ButtonActionListener implements ActionListener {
        
        private File file = fileValue();
        private final JLabel label;
        private final JDialog parent;
        private final SingleSettingConfigurationController controller;
        
        public ButtonActionListener(JLabel l, JDialog parent) {
            this.label = l;
            this.parent = parent;
            controller = new SingleSettingConfigurationController() {
            
                public void defaultValue() {
                    File ff = (File) getDefaultValue();
                    label.setText(ff.toString());
                }
            
                public void reset() {
                    File ff = fileValue();
                    label.setText(ff.toString());
                    setDirty(false);
                }
            
                protected void commitImpl() {
                    setValue(file);
                }
            
            };
            setConfigurationController(controller);
        }
        
        public void actionPerformed(ActionEvent ae){
            JFileChooser fileChooser;
            if (file!=null) {
                fileChooser = new JFileChooser(file.getParentFile());
            } else {
                fileChooser = new JFileChooser(System.getProperty("user.home"));
            }
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setSelectedFile(file);
            int result = fileChooser.showDialog(parent, "Select a file");
            if (result == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                label.setText(fileChooser.getSelectedFile().toString());
                controller.setDirty(true);
            }                                
        }
        
    }
    
}