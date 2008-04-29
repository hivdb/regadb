package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;
import java.util.List;


public interface CompositionBehaviour extends Serializable {
	/**
	 * returns true if the given clause can be composed with the signature clause
	 * @param signatureClause reference clause
	 * @param clause clause that must be checked for composability against the 
	 *        signature clause
	 * @return
	 */
	public boolean canCompose(AtomicWhereClause signatureClause, AtomicWhereClause clause);
	
	/**
	 * returns true if the given clause matches this behaviour
	 * @return
	 */
	public boolean matches(AtomicWhereClause clause);
	
	/**
	 * returns the list of words that should be turned into a single configurer
	 * @param clause
	 * @return
	 */
	public List<ConfigurableWord> getComposableWords(AtomicWhereClause clause);
	
	/**
	 * returns the configurer that will replace the configurers used for the words
	 * returned by getComposableWords
	 * @param configurers
	 * @return
	 */
	public WordConfigurer getWordConfigurer(List<WordConfigurer> configurers);
}
