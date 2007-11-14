/*
 * Step.java
 *
 * Created on October 10, 2003, 2:15 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.gui.wizard;

import javax.swing.JPanel;

/**
 *  A Step in a Wizard.
 *
 * @author  kdg
 */
public interface Step {
    
    public String getStepDescription();
    
    public String getSubtitle();
    
    public String getMainInstructions();
    
    public JPanel getContentPanel();
    
    /**
     * Iff false, the back button will be disabled when this step is current.
     */
    public boolean allowsBack();
    
    /**
     * Iff false, the next/finish button will be disabled when this step is current.
     */
    public boolean allowsNextOrFinish();

    /**
     * Determines what to do (if anything) when the next button is pressed.
     */
    public void onNextOrFinish();
    
    /**
     * Determines what to do (if anything) when the back button is pressed.
     */
    public void onBack();
    
    /**
     * If the wizard is set, the step is responsible to call notifyAllowsNextOrFinishChanged()
     * on the wizard whenever apropriate.
     */
    public void setWizard(Wizard wizard);
    
}
