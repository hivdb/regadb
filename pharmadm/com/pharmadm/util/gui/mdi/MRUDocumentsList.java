/*
 * MRUDocumentsManager.java
 *
 * Created on February 8, 2003, 6:33 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui.mdi;

import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.event.MenuEvent;

//import com.pharmadm.dmax.DMaxSettings;
//import com.pharmadm.dmax.DMaxMainFrame;

/**
 * A generic Most Recently Used (MRU) Documents list.
 * A Document is described by a File. If open, it is associated with an JInternalFrame.
 *
 * The MRUFilesList provides a Setting to persist the list,
 * and a submenu to let the user re-open a recently used document.
 *
 * Based on an earlier MRU implementation for Cubes.
 *
 * @author  kdg
 */
public class MRUDocumentsList {
    
    private final int number; // the number of recent document cached
    private final File[] documentFiles; // the files of the documents
    private final JMenuItem[] menuItems;
    private final RecentDocumentsSetting setting;
    private final DocumentLoader loader;
    private JMenu recentDocumentsMenu;
    private static final char[] chars = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};
    
    /**
     * @pre pluralDescription != null
     * @pre 1 <= setting.getCardinality() <= 9
     *
     * @param pluralDescription a destription of the kind of documents stored in the list, e.g. 'Cubes'.
     * @param setting the setting to persist the list
     */
    public MRUDocumentsList(String pluralDescription, RecentDocumentsSetting setting, DocumentLoader loader) {
        this.number = setting.getCardinality();
        this.setting = setting;
        this.loader = loader;
        documentFiles = new File[number];
        menuItems = new JMenuItem[number];
        initializeMenu(pluralDescription);
    }
    
    public JMenu getMenu() {
        return recentDocumentsMenu;
    }
    
    /**
     * Add an opened document.
     * If the document is not yet in the list, it is added to the head of the MRU list.
     * The last item will drop off the list.  If the document is allready on the list,
     * it will be moved to the head of the list.
     */
    public void addDocument(File documentFile) {
        int index = 0;
        while ((index < (number-1)) && (!documentFile.equals(documentFiles[index]))) {
            index++;
        }
        if (index > 0) {
            drop(index);
            shiftAllDownAndOverwrite(index); // overwrite index
            setting.getRecentFileSetting(0).setValue(documentFile);
            addToMenu(documentFile, 0);
        }
    }
    
    private void initializeMenu(String pluralDescription) {
        recentDocumentsMenu = new JMenu();
        recentDocumentsMenu.setMnemonic('r');
        recentDocumentsMenu.setText("Recent " + pluralDescription);
        recentDocumentsMenu.setToolTipText("Show the most recently opened " + pluralDescription);
        recentDocumentsMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(MenuEvent evt) {
            }
            public void menuDeselected(MenuEvent evt) {
            }
            public void menuSelected(MenuEvent evt) {
                checkDocumentExistence();
            }
        });
        // load the most recent documents from the setting
        for (int index = 0; index < setting.getCardinality(); index++) {
            File file = setting.getRecentFile(index);
            if (file != null) {
                addToMenu(file, index);
            }
        }
    }
    
    /**
     * Kick out references to files that do not exist anymore.
     */
    private void checkDocumentExistence() {
        for (int index = number - 1; index >= 0; index--) {
            boolean removedAnItem;
            do {
                removedAnItem = false;
                File documentFile =  documentFiles[index];
                if (documentFile != null) {
                    if (!documentFile.exists()) {
                        recentDocumentsMenu.remove(menuItems[index]);
                        documentFiles[index] = null;
                        menuItems[index] = null;
                        removedAnItem = true;
                        for (int j = index + 1; j < number; j++) {
                            documentFiles[j-1] = documentFiles[j];
                            menuItems[j-1] = menuItems[j];
                        }
                    }
                }
            } while (removedAnItem);
        }
    }
    
    /*
     * The least recent document is forgotten from the menu.
     */
    private void drop(int index) {
        if (menuItems[index] != null) {
            recentDocumentsMenu.remove(menuItems[index]);
        }
    }
    
    /*
     * Shift all items one place down in the arrays.
     * Overwrite the last item and duplicate the first item.
     */
    private void shiftAllDown() {
        shiftAllDownAndOverwrite(number - 1);
    }
    
    /*
     * Shit all items up to (indexOfItemToOverwrite-1) one place down in the arrays.
     * This will cause item indexOfItemToOverwrite to be overwritten.
     */
    private void shiftAllDownAndOverwrite(int indexOfItemToOverwrite) {
        for (int index = indexOfItemToOverwrite - 1; index >= 0; index--) {
            int from = index;
            int to = index+1;
            if (documentFiles[from] != null) {
                menuItems[to] = menuItems[from];
                menuItems[to].setText((to+1) + " " + documentFiles[from].getName());
                menuItems[to].setMnemonic(chars[to]);
                setting.getRecentFileSetting(to).setValue(documentFiles[from]);
                documentFiles[to]  = documentFiles[from];
            }
        }
    }
    
    private void addToMenu(File documentFile, int index) {
        documentFiles[index]  = documentFile;
        menuItems[index] = new JMenuItem((index + 1) + " " + documentFile.getName());
        menuItems[index].setMnemonic(chars[index]);
        recentDocumentsMenu.insert(menuItems[index],index);
        menuItems[index].addActionListener(new RecentDocumentActionListener(documentFile));
    }
    
    /**
     * What happens if you select a recent document from the popup menu.
     */
    private class RecentDocumentActionListener implements ActionListener {
        
        private final File documentFile;
        
        public RecentDocumentActionListener(File documentFile) {
            this.documentFile = documentFile;
        }
        
        public void actionPerformed(ActionEvent event) {
            loader.loadDocument(documentFile);
        }
    }
}
