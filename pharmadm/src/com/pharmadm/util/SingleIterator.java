/*
 * SingleIterator.java
 *
 * Created on October 6, 2000, 4:07 PM
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
 * @version 1.0
 */
public class SingleIterator implements Iterator {

    private Object o;
    boolean pastObject = false;
    
    
    /** Creates new SingleIterator */
    public SingleIterator(Object o) {
        this.o = o;
    }
    
    public boolean hasNext() {
        return !pastObject;
    }
    
    public Object next() throws NoSuchElementException {
        if (pastObject) throw new NoSuchElementException();
        pastObject = true;
        return o;
    }
    
    public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

}
