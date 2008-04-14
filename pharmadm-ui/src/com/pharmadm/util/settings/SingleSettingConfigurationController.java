/*
 * SingleSettingConfigurationController.java
 *
 * Created on July 19, 2001, 11:51 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

/**
 *
 * @author  toms
 * @version 
 */
public abstract class SingleSettingConfigurationController extends ConfigurationController {

    private boolean isDirty = false;
    
    public boolean isDirty(){
        return isDirty;
    }
    
    public void setDirty(boolean dirty){
        isDirty = dirty;
    }

}
