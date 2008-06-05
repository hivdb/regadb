/*
 * FromVariablePersistenceDelegate.java
 *
 * Created on October 27, 2003, 16:07 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.persist;

import java.beans.*;

import com.pharmadm.custom.rega.reporteditor.ObjectListVariable;

/**
 *
 * @author  kristof
 */
public class ObjectListVariablePersistenceDelegate extends java.beans.DefaultPersistenceDelegate {
    
    protected Expression instantiate(Object oldInstance, Encoder out) {
        return new Expression(oldInstance, oldInstance.getClass(), "new", new Object[]{((ObjectListVariable)oldInstance).getObject()});
    }
}
