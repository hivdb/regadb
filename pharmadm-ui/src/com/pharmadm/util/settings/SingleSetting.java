/*
 * SingleSetting.java
 *
 * Created on July 17, 2001, 3:24 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.io.PrintStream;

/**
 *
 * @author  toms
 */
public abstract class SingleSetting extends AbstractSetting {

    /** Creates new SingleSetting */
    public SingleSetting() {
    }
    
    public boolean isComposed() {
        return false;
    }
    
    public abstract boolean setValue(Object value);
    
    public abstract Object getValue();
    
    /**
     * Subscribtions are strong subscriptions by default.
     */
    public abstract void addSettingSubscriber(SettingSubscriber subscriber);

    /**
     * Weak subscriptions are GC'ed as soon as the subscriber is no longer reachable through 
     * an independent strong (regular) reference.
     *
     * @param useWeakReference whether to use a weak or a strong reference.
     *
     * @see java.lang.ref.WeakReference
     */
    public abstract void addSettingSubscriber(SettingSubscriber subscriber, boolean useWeakReference);
    
    public abstract void removeSettingSubscriber(SettingSubscriber subscriber);
    
}
