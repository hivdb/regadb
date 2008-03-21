/*
 * AtomicWhereClauseIterator.java
 *
 * Created on August 27, 2003, 3:27 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.util.*;

/**
 * An iterator over all AtomicWhereClauses in a WhereClause tree.
 *
 * @author  kdg
 */
public class AtomicWhereClauseIterator implements Iterator {
    
    private Iterator thisLevelIterator;
    private ArrayList childIterators = new ArrayList(); 
    private Iterator currentAtLevel = null;
    private Object next = null;
    
    public AtomicWhereClauseIterator(WhereClause rootClause) {
        if (rootClause != null) {
            if (rootClause instanceof AtomicWhereClause) {
                childIterators.add(new com.pharmadm.util.SingleIterator(rootClause));
            } else {
                Iterator<WhereClause> childIterator = rootClause.iterateChildren();
                while (childIterator.hasNext()) {
                    WhereClause childClause = childIterator.next();
                    if (childClause instanceof AtomicWhereClause) {
                        childIterators.add(new com.pharmadm.util.SingleIterator(childClause));
                    } else {
                        childIterators.add(new AtomicWhereClauseIterator(childClause));
                    }
                }
            }
        }
        thisLevelIterator = childIterators.iterator();
    }
    
    /* POST : 
     * hasNext() returns false OR currentAtLevel is not null and has next
     */
    public boolean hasNext() {
        while (currentAtLevel == null || (! currentAtLevel.hasNext())) {
            if (thisLevelIterator.hasNext()) {
                currentAtLevel = (Iterator)thisLevelIterator.next();
            } else {
                return false;
            }
        }
        return true;
    }
    
    public Object next() {
        hasNext();
        if (currentAtLevel == null) {
            throw new NoSuchElementException();
        } else {
            return currentAtLevel.next();
        }
    }
    
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
}
