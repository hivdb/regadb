package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;


public abstract class CompositionBehaviour implements Serializable {
	/**
	 * returns true if the given clause can be composed with the signature clause
	 * @param signatureClause reference clause
	 * @param clause clause that must be checked for composability against the 
	 *        signature clause
	 * @return
	 */
	public abstract boolean canCompose(AtomicWhereClause signatureClause, AtomicWhereClause clause);
	
	/**
	 * returns true if the given clause matches this behaviour
	 * @return
	 */
	public abstract boolean matches(AtomicWhereClause clause);
	
	/**
	 * returns the list of words that should be turned into a single configurer
	 * @param clause
	 * @return
	 */
	public abstract List<ConfigurableWord> getComposableWords(AtomicWhereClause clause);
	

	/**
	 * replace the configurers corresponding to the given list of words
	 * in the given list of configurers by the given composed configurer
	 * @param configurers list of configurers wherein the replacing should happen
	 * @param words list of words in the given list of configurers the must be replaced
	 * @param configurer the new configurer
	 * @return true on success
	 */
    public static final boolean replaceByComposedWord(List<WordConfigurer> configurers, List<ConfigurableWord> words, ComposedWordConfigurer configurer) {
    	List<ConfigurableWord> oldWords = new ArrayList<ConfigurableWord>();
    	oldWords.addAll(words);
    	int i = 0;
    	boolean firstWordFound = false;
    	while (i < configurers.size() && oldWords.size() > 0) {
			if (configurers.get(i).getWord().equals(oldWords.get(0))) {
				if (!firstWordFound) {
	    			configurers.set(i, configurer);
	    			firstWordFound = true;
	    			i++;
				}
				else {
					configurers.remove(i);
				}
				oldWords.remove(0);
			}
			else {
				i++;
			}
    	}
    	return firstWordFound && oldWords.isEmpty();
    }}
