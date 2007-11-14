/*
 * Setting.java
 *
 * Created on February 9, 2001, 2:57 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.settings;

import java.io.*;
import java.lang.ref.WeakReference;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;

//import com.pharmadm.util.resource.MediaInterface;

import org.w3c.dom.Node;

/**
 * An abstract  class representing an atomic Setting of a program.
 *
 * A Setting's value can be null  (unless the contract of a subclass guarantees it cannot).
 *
 * One can get or set the value of an individual Setting.
 * One reads or writes (from/to a text file) at once, the current values of all Settings
 * that are member of a com.pharmadm.util.settings.Settings object.
 *
 * @author  kdg
 * @version 1.0
 */
public abstract class Setting extends SingleSetting {
    
    private List subscribers = null;
    private List weakSubscribers = null;
    
    private Object defaultValue;
    
    public Setting(XMLSettings xs, String name) {
        setName(name);
        if (xs != null) {
            xs.add(this);
        }
    }
    
    private Object value;
    
    public Object getValue() {
        return value;
    }
    
    /**
     * @return true iff value change succeeded
     */
    public boolean setValue(Object o) throws IllegalArgumentException {
        this.value = o;
        notifySubscribers(o);
        return true;
    }
    
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
        setValue(defaultValue);
    }
    
    public abstract boolean read(String value);
    
    public boolean readXML(Node node) {
        boolean result = false;
        Node child = node.getFirstChild();
        for (; child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.TEXT_NODE) {
                result = read(child.getNodeValue().trim());
            }
        }
        
        //System.out.println("VALUE:"+getValue());
        
        return result;
    }
    
    /** Only writes the value part of the Setting.
     * @return true iff writing succeeded
     */
    public abstract boolean write(PrintStream writer);
    
    public boolean writeXML(PrintStream writer) {
        
        if (getName().startsWith("prototype_")||getValue()==null) {
            return true;
        }
        
        writer.print("<");
        writer.print(getName());
        writer.print(">");
        boolean result = write(writer);
        writer.print("</");
        writer.print(getName());
        writer.print(">");
        writer.println();
        return result;
    }
    
    public void addSettingSubscriber(SettingSubscriber subscriber) {
        if (subscribers == null) {
            subscribers = new LinkedList();
        }
        subscribers.add(subscriber);
    }
    
    public void addSettingSubscriber(SettingSubscriber subscriber, boolean useWeakReference) {
        if (!useWeakReference) {
            addSettingSubscriber(subscriber);
        } else {
            if (weakSubscribers == null) {
                weakSubscribers = new LinkedList();
            }
            weakSubscribers.add(new WeakReference(subscriber));
        }
    }
    
    public void removeSettingSubscriber(SettingSubscriber subscriber) {
        if (subscribers != null) {
            subscribers.remove(subscriber);
        }
        if (weakSubscribers != null) {
            boolean removed = false;
            Iterator subscrIter = weakSubscribers.iterator();
            while (!removed && subscrIter.hasNext()) {
                WeakReference weakRef = (WeakReference)subscrIter.next();
                SettingSubscriber aSubscriber = (SettingSubscriber)weakRef.get();
                if (subscriber == aSubscriber) {
                    subscrIter.remove();
                    removed = true;
                } else if (aSubscriber == null) {
                    subscrIter.remove();
                }
            }
        }
    }
    
    public void removeAllSettingSubscribers() {
        subscribers = null;
        weakSubscribers = null;
    }
    
    private void notifySubscribers(Object newValue) {
        if (subscribers != null) {
            for (Iterator iter = subscribers.iterator(); iter.hasNext();) {
                ((SettingSubscriber)  iter.next()).valueChanged(newValue);
            }
        }
        if (weakSubscribers != null) {
            for (Iterator iter = weakSubscribers.iterator(); iter.hasNext();) {
                WeakReference weakRef = (WeakReference)iter.next();
                SettingSubscriber subscriber = (SettingSubscriber)weakRef.get();
                if (subscriber != null) {
                    subscriber.valueChanged(newValue);
                } else {
                    iter.remove();
                }
            }
        }
    }
    
    protected String escapeStringToPCDATA(String s) {
        StringBuffer sb = new StringBuffer((int)(s.length() * 1.3));
        char temp;
        for (int i = 0; i<s.length(); i++) {
            temp = s.charAt(i);
            switch (temp) {
                case '<' : sb.append("&lt;"); break;
                case '>' : sb.append("&gt;"); break;
                case '&' : sb.append("&amp;"); break;
                case '"' : sb.append("&quot;"); break;
                case '\'' : sb.append("&apos;"); break;
                default: sb.append(temp);
            }
        }
        return sb.toString();
    }
    
    public String toString() {
        if (getValue()!=null) {
            return getName()+"<>"+getValue().toString();
        } else {
            return getName()+"<>"+"NULL";
        }
    }
    
}
