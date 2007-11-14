/*
 * CompsedSetting.java
 *
 * Created on July 17, 2001, 3:12 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.PrintStream;

import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.w3c.dom.Node;

/**
 *
 * @author  toms
 * @version
 */
public abstract class ComposedSetting extends AbstractSetting {
    
    private String version = null;
    
    public boolean writeXML(PrintStream writer) {
        
        boolean result = true;
        
        if (!isEmpty()) {
            writer.print("<");
            writer.print(getName());
            if (getVersion() != null){
                writer.print(" version=\"");
                writer.print(getVersion());
                writer.print("\"");
            }
            writer.print(">");
            writer.println();
            
            for (Iterator iter = getChildren(); iter.hasNext(); ) {
                AbstractSetting as = (AbstractSetting) iter.next();
                result &= as.writeXML(writer);
            }
            
            writer.print("</");
            writer.print(getName());
            writer.print(">");
            writer.println();
        }

        return result;
    }
    
    public boolean isComposed() {
        return true;
    }
    
    public boolean readXML(Node node) {
        boolean result = true;
        
        Node child = node.getFirstChild();
        for (; child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String name = child.getNodeName();
                AbstractSetting setting = getChild(name);
                if (setting != null) {
                    result &= setting.readXML(child);
                }
            }
        }
        return result;
    }
    
    /**
     * Iterates all children in an undefined order.
     * Subclasses can define an order of iteration.
     */
    public abstract Iterator getChildren();
    
    /**
     * Whether this ComposedSetting has children.
     */
    public abstract boolean isEmpty();
    
    /**
     * Returns the child with the given name.
     */
    public abstract AbstractSetting getChild(String name);
    
    /**
     * Adds the given setting as a child.
     */
    public abstract void add(AbstractSetting setting);
    
    public ConfigurationDialog getConfigurationDialog(JFrame owner) {
        return null;
    }
    
    public ConfigurationDialog getConfigurationDialog(JDialog dialog) {
        return null;
    }
    
    public void clearConfigurationControl() {
        super.clearConfigurationControl();
        for (Iterator iter = getChildren(); iter.hasNext();) {
            ((AbstractSetting) iter.next()).clearConfigurationControl();
        }
    }
    
    protected JComponent getConfigurationControlImpl(final JDialog parent) {
        
        JButton button = new JButton("Configure subsettings");
        
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                JDialog dialog = getConfigurationDialog(parent);
                dialog.validate();
                dialog.pack();
                ((com.pharmadm.util.settings.ConfigurationDialog)dialog).formComponentShown(null);
                dialog.setVisible(true);
            }
        });
        
        return button;
    }
    
    //-------------------------------------
    // bit of a hack to introduce versioning
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getVersion() {
        return version;
    }
    
    //-------------------------------------
    
}
