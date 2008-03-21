package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;

public interface QueryVisitor {
	/**
	 * complete query
	 * @param query
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String visitQuery(Query query)  throws java.sql.SQLException;
	
	/**
	 * query for dropdowns
	 * @param query
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String visitDistinctResultQuery(Query query)  throws java.sql.SQLException;
	
	
	/**
	 * select clause of the query
	 * @param selectList
	 * @return
	 */
	public String visitSelectionSatusList(SelectionStatusList selectList);
	
	/**
	 * list of strings and variables that, when concatenated, form a where clause
	 * @param list
	 * @return
	 */
	public String visitWhereClauseOrderedAWCWordList(OrderedAWCWordList list);
	
	/**
	 * simple name of the output variable of an AtomicWhereClause
	 * @param ovar
	 * @return
	 */
	public String visitWhereClauseOutputVariable(OutputVariable ovar);
	
	/**
    /* return the full name uniquely identifying this field in a query (select / where clause) 
	 * @param ovar
	 * @return
	 */
	public String visitWhereClauseFullNameOutputVariable(OutputVariable ovar, Field field);
	
	/**
	 * string constant
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseStringConstant(StringConstant constant);
	
	/**
	 * string constant with undefined end
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseStartstringConstant(StartstringConstant constant);
	
	/**
	 * string constant with undefined start and/or end
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseSubstringConstant(SubstringConstant constant);
	
	/**
	 * string constant with undefined start
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseEndstringConstant(EndstringConstant constant);

	/**
	 * string representation of constants that are of an unsupported type
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseConstant(Constant constant);
	
	/**
	 * date constant
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseDateConstant(DateConstant constant);
	
	/**
	 * table name
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseFromVariable(FromVariable fromVar);

	/**
	 * and clause
	 * @param clause
	 * @return
	 * @throws SQLException
	 */
	public String visitWhereClauseAndClause(AndClause clause) throws SQLException;

	/**
	 * or clause
	 * @param clause
	 * @return
	 * @throws SQLException
	 */
	public String visitWhereClauseInclusiveOrClause(InclusiveOrClause clause) throws SQLException;
	
	/**
	 * or clause
	 * @param clause
	 * @return
	 * @throws SQLException
	 */
	public String visitWhereClauseNotClause(NotClause clause) throws SQLException;

	/**
	 * get the needed from clause for the given where clause
	 * @param clause
	 * @return
	 * @throws SQLException
	 */
	public String visitFromClauseAtomicWhereClause(AtomicWhereClause clause) throws SQLException;
	
	/**
	 * get the needed from clause for the given composed where clause
	 * @param clause
	 * @return
	 * @throws SQLException
	 */
	public String visitFromClauseComposedWhereClause(ComposedWhereClause clause) throws SQLException;
	
	/**
	 * get the needed from clause for the given not clause
	 * @param clause
	 * @return
	 * @throws SQLException
	 */
	public String visitFromClauseNotClause(NotClause clause);
	
	/**
	 * get the needed from clause for the given or clause
	 * @param clause
	 * @return
	 * @throws SQLException
	 */
	public String visitFromClauseInclusiveOrClause(InclusiveOrClause clause);

	/**
	 * string representation of the given from variable
	 * @param fromVar
	 * @return
	 */
	public String visitFromClauseFromVariable(FromVariable fromVar);
}
