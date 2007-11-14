/*
 * ListComposedSetting.java
 *
 * Created on February 7, 2003, 1:54 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.util.*;

import org.w3c.dom.Node;

/**
 * A prototype based composed setting that stores its children in a list.
 * The iterator returns children in the order that they were added.
 *
 * @author  kdg
 */
public class ListComposedSetting extends PrototypeComposedSetting {
    
    private ArrayList list = new ArrayList();
    
    /** Creates a new instance of ListComposedSetting */
    public ListComposedSetting(String name, AbstractSetting prototype) {
        super(name, prototype);
    }
    
    public AbstractSetting getPrototypeClone() {
        return (AbstractSetting)this.getPrototype().clone();
    }
    
    public void clear() {
        this.list = new ArrayList();
    }
    
    public void add(AbstractSetting setting) {
        list.add(setting);
    }
    
    public int size() {
        return list.size();
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public AbstractSetting get(int i) {
        return (AbstractSetting)this.list.get(i);
    }
    
    /**
     * Iterates the children in the order that they were added.
     */
    public Iterator getChildren() {
        return list.iterator();
    }
    
    public Object clone() {
        ListComposedSetting clone = (ListComposedSetting)super.clone();
        clone.list = new ArrayList();
        for (Iterator iter = getChildren(); iter.hasNext();) {
            clone.add((AbstractSetting) ((AbstractSetting) iter.next()).clone());
        }
        return clone;
    }
    
}
