/*
 * FormattedComboBoxEditor.java
 *
 * Created on September 25, 2003, 9:21 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.gui;

import javax.swing.*;

/**
 *
 * @author  kristof
 */
public class FormattedComboBoxEditor extends javax.swing.plaf.basic.BasicComboBoxEditor {
    
    
    /** Creates a new instance of FormattedComboBoxEditor */
    public FormattedComboBoxEditor(JFormattedTextField formatEditor) {
        editor = formatEditor;
    }
    
}
