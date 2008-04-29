/*
 * AWCPersistenceDelegate.java
 *
 * Created on October 27, 2003, 16:32 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.persist;

import java.beans.*;

import com.pharmadm.custom.rega.reporteditor.DataGroup;

/**
 *
 * @author  kristof
 */
public class DataGroupPersistenceDelegate extends java.beans.DefaultPersistenceDelegate {
    
    protected Expression instantiate(Object oldInstance, Encoder out) {
        DataGroup dataGroup = (DataGroup)oldInstance;
        return new Expression(dataGroup, dataGroup.getClass(), "new", new Object[]{dataGroup.getDataInputVariables(), dataGroup.getDataOutputVariables(), dataGroup.getObjectListVariables(), dataGroup.getConstants(), dataGroup.getVisualizationList()});
    }
}
