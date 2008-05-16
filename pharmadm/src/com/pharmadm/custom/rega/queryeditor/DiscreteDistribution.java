/*
 * DiscreteDistribution.java
 *
 * Created on May 7, 2004, 4:29 PM
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
 *
 *
 * @author  kdg
 */
public class DiscreteDistribution {
    
    private final Map map = new HashMap();
    private final Comparator sizeComparator = new SizeComparator();
    
    /** Creates a new instance of DiscreteDistribution */
    public DiscreteDistribution() {
        // Null is a special case.  Always count nulls, even if it doesn't occur.
        map.put(null, new ItemOccurrenceCount(null));
    }
    
    public void accumulate(Object o) {
        ItemOccurrenceCount ocount = (ItemOccurrenceCount)map.get(o);
        if (ocount != null) {
            ocount.increaseOccurenceCount();
        } else {
            ocount = new ItemOccurrenceCount(o);
            ocount.increaseOccurenceCount();
            map.put(o, ocount);
        }
    }
    
    /**
     * Finds the count for nulls and the (maxNbItems-1) most occuring non-null items.  
     * In case of a tie in number of occurences,
     * the order is undefined.  If the tie crosses the max-nb-items boundary,
     * it is undefined which of the items in the tie are returned.
     *
     * @param maxNbItems the maximum size of the list (-1 indicates no limit)
     *
     * @return an array of ItemOccurrenceCounts, sorted by decreasing occurences.
     *          the first element of the array is the count for null
     */
    public ItemOccurrenceCount[] getMostOccuringItemsAndNull(int maxNbItems) {
        Collection values = map.values();
        ItemOccurrenceCount[] result = new ItemOccurrenceCount[values.size()];
        result = (ItemOccurrenceCount[])values.toArray(result);
        Arrays.sort(result, sizeComparator);
        if (maxNbItems != -1 && maxNbItems < result.length) {
            ItemOccurrenceCount[] shorterResult = new ItemOccurrenceCount[maxNbItems];
            System.arraycopy(result, 0, shorterResult, 0, maxNbItems);
            result = shorterResult;
        }
        return result;
    }
    
    /**
     * Returns the number of disctinct items (null counts as one, too).
     */
    public int getNbDistinctItems() {
        return map.values().size();
    }
    
    /**
     * Keeps the count for one item.
     */
    public static class ItemOccurrenceCount {
        
        private int occurenceCount;
        private Object item;
        
        /**
         * Creates a new ItemOccurrence to register that an item occurs zero times.
         */
        public ItemOccurrenceCount(Object item) {
            this.item = item;
        }
        
        public void increaseOccurenceCount() {
            occurenceCount++;
        }
        
        public int getOccurenceCount() {
            return occurenceCount;
        }
        
        public Object getItem() {
            return item;
        }
    }
    
    /**
     * Compares two ItemOccurrenceCounts, based on null-ness (null comes first) and on size (largest come first).
     * ItemOccurrenceCounts of equal size are considered equal.
     */
    private static class SizeComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
            ItemOccurrenceCount ioc1 = (ItemOccurrenceCount)o1;
            ItemOccurrenceCount ioc2 = (ItemOccurrenceCount)o2;
            if (ioc1.getItem() == null) {
                if (ioc2.getItem() == null) {
                    return 0;
                } else {
                    return -1;
                }
            } else if (ioc2.getItem() == null) {
                return 1;
            }
            int count1 = ioc1.getOccurenceCount();
            int count2 = ioc2.getOccurenceCount();
            if (count1 !=  count2) {
                if ((count1 - count2) > 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return 0;
            }
        }
    }
}
