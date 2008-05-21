/*
 * AWCWordConfigurer.java
 *
 * Created on September 1, 2003, 2:21 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;


/**
 *
 * @author  kristof
 *
 * <p>
 * Implementing classes of AWCWordConfigurer must be subclasses of 
 * java.awt.Component in order to be able to access them in the GUI,
 * and are preferably subclasses of javax.swing.JComponent. A
 * constructor taking the AWCWord to be configured as parameter 
 * should be provided.
 * </p>
 * 
 */
public interface WordConfigurer {
    
    /** returns the AWCWord managed by this configurer */
    public ConfigurableWord getWord();
    
    /** configures the AWCWord managed by this configurer with the relevant value */ 
    public void configureWord();
    
    /**
     * assign this configurer to a different configuration controller
     */
    public void reAssign(Object o);
    
    /**
     * return false when it is pointless to have clauses with this configurer
     * @return
     */
    public boolean isUseless();
}
