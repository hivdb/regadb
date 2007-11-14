/*
 * PDMEventManager.java
 *
 * Created on January 22, 2001, 5:10 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util.event;

import java.util.*;

/**
 * A class that keeps track of all subscribed EventListeners and that makes sure all
 * subscribed EventListeners hear the appropriate Events.
 *
 * Change history
 * 2.0: now fully synchronized.  all methods are atomic.
 *
 * @author  kdg
 * @version 2.0
 */
public class PDMEventManager extends Object {
    
    private HashMap subscribers = new HashMap();
    private boolean enabled = true;
    
    /** Creates new PDMEventManager */
    public PDMEventManager() {
    }
    
    /**
     * Subscribes the class as a Listener to the event referred to by the specified name
     *
     * @param	subscriber	the EventListener
     *		eventName	the name of the Event the class wants to listen for
     *
     * @pre     (subscriber != null) and
     *		(eventName != null)
     *
     * @effect  the subscriber now hears any event with the specified namethat occur.
     */
    public synchronized void subscribe(PDMEventListener subscriber, String eventName) {
        if (!subscribers.containsKey(eventName)) {
            EventList newList = new EventList(eventName);
            newList.add(subscriber);
            subscribers.put(eventName, newList);
        } else {
            EventList list = (EventList)subscribers.get(eventName);
            if (!list.contains(subscriber))
                list.add(subscriber);
        }
    }
    
    public void subscribe(PDMEventListener subscriber, String[] eventNames) {
        for (int i = 0; i < eventNames.length; i++) {
            subscribe(subscriber, eventNames[i]);
        }
    }
    
    /**
     * Publishes an Event.  All appropriate EventListeners hear the Event.
     *
     * @param	e	the Event to be published
     *
     * @pre     e != null
     *
     * @effect  All EventListeners subscribed for an Event with the name of Event e
     *		hear the Event
     */
    public void publish(PDMEvent e) {
        if (!enabled) return;
        int listSize = 0;
        PDMEventListener[] listeners = null;
        synchronized (this) {
            if (subscribers.containsKey(e.getName())) {
                EventList list = (EventList)subscribers.get(e.getName());
                if (!list.isEnabled()) {
                    return;
                }
                listSize = list.size();
                listeners = new PDMEventListener[listSize];
                list.toArray(listeners);
            }
        }
        // The part below must not be synchronized, otherwise
        // a listener cannot unsubscribe itself after hearing
        // the event.
        for (int i = 0; i < listSize; i++) {
            try {
                listeners[i].processEvent(e);
                listeners[i] = null;
            } catch (Exception exp) {
                System.out.println("Exception: " + exp + " in " + listeners[i]);
                exp.printStackTrace(System.out);
            }
        }
    }
    
    public synchronized void setEnabled(boolean enable, String[] eventNames) {
        for (Iterator iter = subscribers.keySet().iterator(); iter.hasNext(); ) {
            EventList list = (EventList)iter.next();
            for (int i = 0; i < eventNames.length; i++) {
                if (list.getName().equals(eventNames[i])) {
                    list.setEnabled(enable);
                }
            }
        }
    }
    
    public synchronized void setEnabled(boolean enable, String eventName) {
        if (!subscribers.containsKey(eventName)) {
            if (!enable) {
                EventList newList = new EventList(eventName);
                newList.setEnabled(false);
                subscribers.put(eventName, newList);
            }
        } else {
            EventList list = (EventList)subscribers.get(eventName);
            list.setEnabled(enable);
        }
    }
    
    /**
     * Removes the EventListener object from the list of objects that listen for the event
     * specified by the specified name.
     *
     * @param	subscriber	the EventListener
     *		eventName	the name of the Event the class wants to listen for
     *
     * @pre     (subscriber != null) and
     *		(eventName != null)
     *
     * @effect  the subscriber no longer hears any event with the specified name
     */
    public synchronized void unsubscribe(PDMEventListener subscriber, String eventName) {
        if (subscribers.containsKey(eventName)) {
            EventList list = (EventList)subscribers.get(eventName);
            if (list.contains(subscriber))
                list.remove(subscriber);
            if (list.isEmpty() && list.isEnabled())
                subscribers.remove(eventName);
        }
    }
    
    public void unsubscribe(PDMEventListener subscriber, String[] eventNames) {
        for (int i = 0; i < eventNames.length; i++) {
            unsubscribe(subscriber, eventNames[i]);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}


