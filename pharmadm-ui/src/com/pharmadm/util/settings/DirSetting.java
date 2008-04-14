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

import java.awt.Dimension;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//import com.pharmadm.util.resource.MediaInterface;
import com.pharmadm.util.file.FileUtil;

/**
 * This Setting is for directories only.  It can create dirs if they do not yet exist when they are requested.
 * For file, use FileSetting.
 *
 * @author  kdg
 * @version 2.0
 *
 * @invar value is null, or a File
 *    * that is a directory,
 *    * and its filename does not end with a separator.
 *    * It is not required to exist.
 *
 * IMPORTANT: subclasses must implement a setDefaultValue that returns a File
 * that satisfies the above invariant.
 */

public class DirSetting extends Setting {
    
    private boolean createNewIfNotExists = true;
    
    private DirSetting(XMLSettings xs, String name) {
        super(xs, name);
    }

    public DirSetting(XMLSettings xs, String name, boolean createNewIfNotExists) {
        this(xs, name);
        this.createNewIfNotExists = createNewIfNotExists;
    }
    
    public DirSetting(XMLSettings xs, String name, File defaultValue) {
        this(xs, name);
        setDefaultValue(defaultValue);
    }
    
    public DirSetting(XMLSettings xs, String name, File defaultValue, boolean createNewIfNotExists) {
        this(xs, name, defaultValue);
        this.createNewIfNotExists = createNewIfNotExists;
    }
    
    /**
     * @pre if o is a File, than it MUST NOT end with a slash.
     */
    public boolean setValue(Object o) {
        if (o instanceof File) {
            super.setValue((File) o);
        } else if (o instanceof String) {
            super.setValue(new File(FileUtil.chopLastSlashes((String) o)));
            
        } else {
            return false;
        }
        return true;
    }
        
    public boolean read(String s) {
        if (s.equals("null")) {
            setValue(null);
            return true;
        } else {
            return setValue(s);
        }
    }
    
    public boolean write(PrintStream writer) {
        writer.println(getValue());
        return true;
    }
    
    /**
     * This returns either a File that is an existing directory, or null.
     * The filename does NOT end with a File.separator  (slash or backslash).
     */
    public Object getValue() {
        File dir;
        boolean retry = false;
        do {
            retry = false;
            dir = (File)super.getValue();
            if (dir != null) {
                if (!dir.exists()) {
                    if (createNewIfNotExists) {
                        if (dir.mkdirs()) {
                            System.out.println("New dir created: " + dir);
                            setValue(dir);
                        } else {
                            System.out.println("Cannot create dir " + dir);
                            if (!promptUser("does not exist and cannot be created")) {
                                setValue(null);
                                dir = null;
                            } else {
                                retry = true;
                            }
                        }
                    } else {
                        dir = null;
                    }
                } else {
                    if (!dir.isDirectory()) {
                        System.out.println("This is a file, not a directory as expected: " + dir);
                        if (!promptUser("seems to be a file, not a directory")) {
                            setValue(null);
                        } else {
                            retry = true;
                        }
                    }
                }
            }
        } while (retry);
        return dir;
    }
    
    public File dirValue() {
        return (File) getValue();
    }
    
    /**
     * Returns a String representation of the directory, INCLUDING a separator (slash or backslash).
     * So: this is NOT the same as getValue().toString()
     */
    public String getStringValue() {
        File file = (File)getValue();
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath() + File.separator;
    }
    
    protected boolean promptUser(String msgAboutValue) {
        String msg = "The directory setting '" + getName() + "' seems to be invalid. \n";
        if (msgAboutValue != null && !msgAboutValue.equals("")) {
            msg += "It's current value (" + getValue() + ") " + msgAboutValue + ". \nSelect another directory (or create a new one) using the browse button below.";
        }
        int option;
        do {
            boolean sure = false;
            do {
                String[] options = {"Browse", "Proceed"};
                option = JOptionPane.showOptionDialog(null, msg, "Invalid directory",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if (option == 0) {
                    sure = true;
                } else {
                    int sureAnswer = JOptionPane.showConfirmDialog(null,
                    "Proceeding with an illegal directory setting may destabilize the program and corrupt your data. \nAre you sure you want to proceed?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    sure = (sureAnswer == JOptionPane.YES_OPTION);
                }
                if (option == 0) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setMultiSelectionEnabled(false);
                    int retval = chooser.showOpenDialog(null);
                    if (retval == JFileChooser.APPROVE_OPTION) {
                        File theDir = chooser.getSelectedFile();
                        if (theDir != null) {
                            setValue(theDir);
                            System.out.println("I have set value to " +theDir);
                            return true;
                        }
                    } else {
                        sure = false;
                    }
                }
            } while (!sure);
            
        } while (option != 1);
        return false;
    }
    
    protected JComponent getConfigurationControlImpl(final JDialog parent) {
        
        final File file = dirValue();
        
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
        
        private File file = dirValue();
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
                    File ff = dirValue();
                    label.setText(ff.toString());
                    setDirty(false);
                }
                
                protected void commitImpl() {
                    setValue(file);
                }
                
            };
            setConfigurationController(controller);
        }
        
        public void actionPerformed(ActionEvent ae) {
            JFileChooser fileChooser =
            file != null ? new JFileChooser(file.getParentFile()) : new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setSelectedFile(file);
            int result = fileChooser.showDialog(parent, "Select a directory");
            if (result == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                label.setText(fileChooser.getSelectedFile().toString());
                controller.setDirty(true);
            }
        }
        
    }
}