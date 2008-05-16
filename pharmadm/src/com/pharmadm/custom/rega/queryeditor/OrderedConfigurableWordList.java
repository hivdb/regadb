/*
 * OrderedWordList.java
 *
 * Created on November 18, 2003, 6:58 PM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author  kristof
 */

/**
 * <p>
 * An OrderedAWCWordList can properly concatenate the String representation of
 * its Words into a clause in a particular format.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  words
 *  owner
 * </p>
 * @invar !hasOwner() || owner contains all non-fixedstring-Words  of this.
 */
public class OrderedConfigurableWordList implements Cloneable, Serializable {
    
    private WordListOwner owner = null;
    private List<ConfigurableWord> words = new ArrayList<ConfigurableWord>(); // of type ConfigurableWord
    
    /** For xml-encoding purposes only */
    public OrderedConfigurableWordList() {
    }
    
    public OrderedConfigurableWordList(WordListOwner owner) {
        this.owner = owner;
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    /**
     * Returns an unmodifiable List of the words.
     */
    public List<ConfigurableWord> getWords() {
        return words; //Collections.unmodifiableList(words);
    }
    
    /** For cloning and xml-encoding purposes only */
    // don't use for anything else, since the sets of the owner don't get updated
    public void setWords(List<ConfigurableWord> newList) {
        words = newList;
    }
    
    public WordListOwner getOwner() {
        return owner;
    }
    
    public boolean hasOwner() {
        return owner != null;
    }
    
    // required for cloning and xml-encoding
    // don't use for anything else, since the sets of the AtomicWhereClauses don't get updated
    public void setOwner(WordListOwner newOwner) {
        this.owner = newOwner;
    }
    
    public String getHumanStringValue() {
        StringBuffer sb = new StringBuffer();
        Iterator<ConfigurableWord> iterWords = getWords().iterator();
        while (iterWords.hasNext()) {
        	ConfigurableWord word = iterWords.next();
            sb.append(word.getHumanStringValue());
            sb.append(" ");
        }
        return sb.toString();
    }
    
    /**
     * Makes a deep clone of the OrderedWordList; 
     * @pre : the list to be cloned contains ConfigurableWords for which a clone has already been made 
     * (these are stored in the originalToCloneMap); the existing clones must be used i.o. new ones; 
     * @pre : the owner of the list has just been cloned as well, and this owner clone
     * must become the owner of the clone we are making here
     */
    public OrderedConfigurableWordList cloneInContext(Map<ConfigurableWord, ConfigurableWord> originalToCloneMap, WordListOwner cloneOwner) throws CloneNotSupportedException {
        OrderedConfigurableWordList clone = (OrderedConfigurableWordList)super.clone();
        clone.setOwner(cloneOwner);
        Iterator<ConfigurableWord> origWordIterator = getWords().iterator();
        ArrayList<ConfigurableWord> cloneWords = new ArrayList<ConfigurableWord>();  // MUST be new list, not a reference to the original list
        while (origWordIterator.hasNext()) {
        	ConfigurableWord word = origWordIterator.next();
            if (word instanceof FixedString) {
                cloneWords.add(word);  // immutable, no need to clone
            } else {
            	ConfigurableWord cloneWord = (ConfigurableWord)originalToCloneMap.get(word);
                cloneWords.add(cloneWord);
            }
        }
        clone.setWords(cloneWords);
        return clone;
    }
    
}
