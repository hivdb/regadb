package com.pharmadm.custom.rega.queryeditor;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;


public class NullComposition extends CompositionBehaviour {

	/**
	 * the null behaviour never composes clauses
	 */
	public boolean canCompose(AtomicWhereClause signatureClause, AtomicWhereClause clause) {
		return false;
	}

	/**
	 * the null behaviour never composes clauses
	 */
	public boolean matches(AtomicWhereClause clause) {
		return false;
	}

	/**
	 * composition never happens
	 */
	public List<ConfigurableWord> getComposableWords(AtomicWhereClause clause) {
		return null;
	}

	/**
	 * composition never happens
	 */
	public ComposedWordConfigurer getWordConfigurer(List<WordConfigurer> configurers) {
		return null;
	}
}
