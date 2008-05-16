/*
 * IteratorFilter.java
 *
 * Created on October 6, 2000, 3:11 PM
 *
 * Copyright (c) 2000 PharmaDM, N.V.
 * Celestijnenlaan, 200A, Heverlee, Belgium
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of 
 * PharmaDM, N.V. ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * PharmaDM.
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.util;

import java.util.*;

/**
 *
 * @author  kdg
 * Based on EnumerationFilter.java (Jan Struyf)
 *
 * A class that returns an Iterator that returns only a subset of a
 * given Iterator using a certain filter. The filter is implemented
 * through the includeElement(Object) method that should be overridden
 * by the subclass.
 */
public abstract class IteratorFilter implements Iterator {

    private Iterator iterator;
    private boolean hasMore = true, advanced = false;
    private Object currentElement;

    /**
     * A constructor for creating a new IteratorFilter based
     * on the given Iterator
     */
    public IteratorFilter(Iterator iterator) {
        this.iterator = iterator;
    }

    /**
     * Returns true if there are still elements to be returned by the Iterator.
     */
    public boolean hasNext() {
        if (!advanced) advanceNext();
        return hasMore;
    }

    /**
     * Returns the next element in the Iterator.
     */
    public Object next() {
        advanced = false;
        return currentElement;
    }

    public void remove() throws IllegalStateException, UnsupportedOperationException {
        iterator.remove();
    }

    /**
     * A method that should return true for those elements in the Iterator
     * passed on in the constructor that must be included in the Iterator
     * that this object represents.
     */
    public abstract boolean includeElement(Object element);

    /*
     * A private auxiliary method that looks for the next element in the original
     * Iterator that must be included in this Iterator object.
     */
    private void advanceNext() {
        advanced = true;
        while (iterator.hasNext()) {
            currentElement = iterator.next();
            if (includeElement(currentElement)) return;
        }
        hasMore = false;
    }
    
}
