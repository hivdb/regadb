package com.pharmadm.custom.rega.gui;


import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConfigurationController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.VisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

/**
 *
 * @author  kristof
 */
public class WLOEditorPanel extends javax.swing.JPanel {
    
    protected List<WordConfigurer> configList;
    protected ConfigurationController controller;

    /** Creates new form WLOEditorPanel */
    public WLOEditorPanel(ConfigurationController controller) {
        this.controller = controller;
        initMoreComponents();
    }
    
    private void initMoreComponents() {
        setLayout(new java.awt.FlowLayout());
        configList = getConfigurers(controller);
        initConfigurers();
    }
    
    protected List<WordConfigurer> getConfigurers(ConfigurationController controller) {
        VisualizationComponentFactory factory = controller.getVisualizationComponentFactory();
        return factory.createComponents(controller.getVisualizationList());
    }
    
    protected void initConfigurers() {
    	this.removeAll();
    	for (WordConfigurer confy : configList) {
            try {
                add((java.awt.Component)confy);
            } catch (ClassCastException cce) {
                System.out.println("Warning : Can only add objects of class java.awt.Component to GUI");
            }
    	}
    }
        
    /** Applies changes made to all visualisation components in the componentList to the corresponding AWCWords */
    public void applyEditings() {
        Iterator<WordConfigurer> iter = configList.iterator();
        while (iter.hasNext()) {
            WordConfigurer confy = iter.next();
            confy.configureWord();
        }
    }
    
    public ConfigurationController getEditor() {
        return controller;
    }
    
    public void addFocusListener(java.awt.event.FocusListener listener) {
        if (configList == null) {
            // I am very sorry to have to add this, but addFocusListener is apparently called in the behind-the-screens initialization
            // (something to ensure serializability...)
            return;
        }
        Iterator<WordConfigurer> iter = configList.iterator();
        while (iter.hasNext()) {
            java.awt.Component confy = (java.awt.Component)iter.next();
            confy.addFocusListener(listener);
        }
    }
    
    public void addMouseListener(MouseListener listener) {
    	if (configList != null) {
    		for (WordConfigurer confy : configList) {
    			Component comp = (Component) confy;
    			comp.addMouseListener(listener);
    		}
    	}
    }
    
    public void freeResources() {
        Iterator<WordConfigurer> iter = configList.iterator();
        while (iter.hasNext()) {
            WordConfigurer confy = (WordConfigurer)iter.next();
            confy.freeResources();
        }
    }
}
