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
 * A prototype based composed setting that stores its children in a TreeSet.
 * The iterator returns children in the order determined by a comparator.
 *
 * @author  kdg
 */
public class TreeSetComposedSetting extends PrototypeComposedSetting {
    
    private TreeSet treeSet;
    
    /** Creates a new instance of ListComposedSetting */
    public TreeSetComposedSetting(String name, AbstractSetting prototype, Comparator comparator) {
        super(name, prototype);
        this.treeSet = new TreeSet(comparator);
    }
    
    public void clear() {
        treeSet.clear();
    }
    
    public void add(AbstractSetting setting) {
        treeSet.add(setting);
    }
    
    public int size() {
        return treeSet.size();
    }
    
    public boolean isEmpty() {
        return treeSet.isEmpty();
    }
    
    /**
     * Iterates the children in the order determined by the comparator.
     */
    public Iterator getChildren() {
        return treeSet.iterator();
    }
}
