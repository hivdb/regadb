/*
 * ConfigurationController.java
 *
 * Created on July 18, 2001, 9:11 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author  toms
 * @version 
 */
public abstract class ConfigurationController {
    
    private List list = null;
    
    //--------------------------------------
    // USE ME
    
    public abstract boolean isDirty();
    
    //--------------------------------------
    
    public void addCommitSubscriber(CommitSubscriber subscriber) {
        if (list == null){
            list = new LinkedList();
        }
        list.add(subscriber);
    }

    public abstract void defaultValue();
    
    public abstract void reset();
    
    public final void commit(){
        if (isDirty()){
            commitImpl();
            notifySubscribers();
        }
    }
    
    protected abstract void commitImpl();
    
    private void notifySubscribers() {
        if (list != null){
            for (Iterator iter = list.iterator(); iter.hasNext(); ){
                ((CommitSubscriber) iter.next()).commited();
            }
        }
    }
    
}
