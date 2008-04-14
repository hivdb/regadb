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
 * A composed setting with a variable number of children, all belonging to the same Setting class. 
 * A prototype is used to read the children from XML. All children have the same name.
 * Note: The prototype does not count as a child.
 *
 * @author  kdg
 */
public abstract class PrototypeComposedSetting extends ComposedSetting {
    
    private AbstractSetting prototype;

    /** Creates a new instance of ListComposedSetting */
    public PrototypeComposedSetting(String name, AbstractSetting prototype) {
        this.prototype = prototype;
        setName(name);
    }
    
    /**
     * For cloning only.
     */
    private PrototypeComposedSetting() {
    }
    
    protected AbstractSetting getPrototype() {
        return prototype;
    }
    
    public boolean readXML(Node node) {
        clear();
        boolean result = true;
        Node child = node.getFirstChild();
        for (; child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                AbstractSetting setting = (AbstractSetting)getPrototype().clone();
                if (!setting.readXML(child)) {
                    result = false;
                } else {
                    add(setting);
                }
            }
        }
        return result;
    }
    
    /**
     * Such call should no occur, all children have the same name. A deficieny of the design :( henkv
     */
    public AbstractSetting getChild(String name) {
	return null;
    }
    
    /**
     * Removes all children.
     */
    public abstract void clear();

    /**
     * Deep clone.
     */
    public Object clone() {
        PrototypeComposedSetting clone = (PrototypeComposedSetting)super.clone();
        clone.clear();
        clone.prototype = (AbstractSetting)prototype.clone();
        for (Iterator iter = getChildren(); iter.hasNext();) {
            clone.add((AbstractSetting) ((AbstractSetting) iter.next()).clone());
        }
        return clone;
    }
}
