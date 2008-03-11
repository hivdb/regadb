package com.pharmadm.custom.rega;


import java.util.*;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.ConfigurationController;
import com.pharmadm.custom.rega.queryeditor.gui.VisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.gui.WordConfigurer;

/**
 *
 * @author  kristof
 */
public class WLOEditorPanel extends javax.swing.JPanel {
    
    private List configList;
    protected ConfigurationController controller;

    /** Creates new form WLOEditorPanel */
    public WLOEditorPanel(ConfigurationController controller) {
        this.controller = controller;
        initMoreComponents();
    }
    
    private void initMoreComponents() {
        setLayout(new java.awt.FlowLayout());
        VisualizationComponentFactory factory = controller.getVisualizationComponentFactory();
        configList = new ArrayList();
        Iterator iter = controller.getVisualizationList().iterator();
        while (iter.hasNext()) {
            ConfigurableWord word = (ConfigurableWord)iter.next();
            WordConfigurer wordConfigurer = factory.createComponent(word);
            configList.add(wordConfigurer); 
            try {
                add((java.awt.Component)wordConfigurer);
            } catch (ClassCastException cce) {
                System.out.println("Warning : Can only add objects of class java.awt.Component to GUI");
            }
        }
    }
        
    /** Applies changes made to all visualisation components in the componentList to the corresponding AWCWords */
    public void applyEditings() {
        Iterator iter = configList.iterator();
        while (iter.hasNext()) {
            WordConfigurer confy = (WordConfigurer)iter.next();
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
        Iterator iter = configList.iterator();
        while (iter.hasNext()) {
            java.awt.Component confy = (java.awt.Component)iter.next();
            confy.addFocusListener(listener);
        }
    }
    
    public void freeResources() {
        Iterator iter = configList.iterator();
        while (iter.hasNext()) {
            WordConfigurer confy = (WordConfigurer)iter.next();
            confy.freeResources();
        }
    }
}
