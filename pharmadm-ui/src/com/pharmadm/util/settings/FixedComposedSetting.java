/*
 * FixedComposedSetting.java
 *
 * Created on July 17, 2001, 3:58 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.util.*;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * A composed setting of which all children have a different name. 
 * The size of the setting is 'fixed' in the sense that the number of children will not change
 * when readXML is invoked.
 *
 * @author  toms
 */
public class FixedComposedSetting extends ComposedSetting {
    
    private LinkedHashMap list;
    
    /** Creates new FixedComposedSetting */
    public FixedComposedSetting() {
        list = new LinkedHashMap();
    }
    
    public FixedComposedSetting(List list) {
        this.list = new LinkedHashMap();
        Iterator i = list.iterator();
        while(i.hasNext()) {
	    AbstractSetting current = (AbstractSetting) i.next();
            this.list.put(current.getName(),current);
        }
    }
    
    public Object clone() {
        FixedComposedSetting clone = (FixedComposedSetting)super.clone();
        clone.list = new LinkedHashMap();
        for (Iterator iter = getChildren(); iter.hasNext();) {
            clone.add((AbstractSetting) ((AbstractSetting) iter.next()).clone());
        }
        return clone;
    }
    
    public void add(AbstractSetting setting) {
	if (list.get(setting.getName()) != null ) {
	    System.out.println("WARNING: the setting: " + setting.getName() + " has already been added to this FixedComposedSettings!");
	}
        list.put(setting.getName(), setting);
    }
    
    public Iterator getChildren() {
        return list.values().iterator();
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
    
    public String toString() {
        
        StringBuffer ret = new StringBuffer();
        
        ret.append(">"+getName()+"\n");
        
        Iterator i = getChildren();
        
        while(i.hasNext()) {
            ret.append(i.next().toString());
            ret.append("\n");
        }
        
        ret.append("<"+getName());
        
        return ret.toString();
    }
    
    public int size() {
	return list.size();
    }

    public AbstractSetting getChild(String name) {
        return (AbstractSetting) list.get(name);
    }
    
    public JComponent getConfigurationControl(JDialog dialog) {
        initConfigurationController();
        return super.getConfigurationControl(dialog);
    }
    
    public ConfigurationDialog getConfigurationDialog(JFrame owner) {
        initConfigurationController();
        return new ConfigurationDialog(owner, this);
    }
    
    public ConfigurationDialog getConfigurationDialog(JDialog owner) {
        initConfigurationController();
        return new ConfigurationDialog(owner, this);
    }
    
    private void initConfigurationController() {
        if (getConfigurationController() == null) {
            setConfigurationController(new FCConfigurationController());
        }
    }
    
    private class FCConfigurationController extends ConfigurationController {
        
        public void defaultValue() {
            for (Iterator iter = getChildren(); iter.hasNext();) {
                ((AbstractSetting) iter.next()).getConfigurationController().defaultValue();
            }
        }
        
        public void reset() {
            for (Iterator iter = getChildren(); iter.hasNext();) {
                ((AbstractSetting) iter.next()).getConfigurationController().reset();
            }
        }
        
        protected void commitImpl() {
            for (Iterator iter = getChildren(); iter.hasNext();) {
                AbstractSetting child = (AbstractSetting) iter.next();
                if (child.isDirty()) {
                    child.getConfigurationController().commit();
                }
            }
        }
        
        public boolean isDirty() {
            boolean result = false;
            for (Iterator iter = getChildren(); iter.hasNext();) {
                AbstractSetting as = (AbstractSetting) iter.next();
                result |= as.isDirty();
            }
            return result;
        }
    }
}
