package com.pharmadm.custom.rega.queryeditor;

import java.util.List;


public class NullComposition implements CompositionBehaviour {

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
	public WordConfigurer getWordConfigurer(List<WordConfigurer> configurers) {
		return null;
	}
}
