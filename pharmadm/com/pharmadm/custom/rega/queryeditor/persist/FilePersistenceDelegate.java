/*
 * FilePersistenceDelegate.java
 *
 * Created on October 27, 2003, 11:58 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.persist;

import java.beans.*;

/**
 *
 * @author  kristof
 */
public class FilePersistenceDelegate extends java.beans.DefaultPersistenceDelegate {
    
    protected Expression instantiate(Object oldInstance, Encoder out) {
        try {
            return new Expression(oldInstance, oldInstance.getClass(), "new", new String[]{((java.io.File)oldInstance).getCanonicalPath()});
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
        
    }
}
