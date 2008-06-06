package com.pharmadm.custom.rega.queryeditor.port;

import java.sql.SQLException;

import com.pharmadm.custom.rega.queryeditor.AndClause;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ComposedWhereClause;
import com.pharmadm.custom.rega.queryeditor.Field;
import com.pharmadm.custom.rega.queryeditor.FromVariable;
import com.pharmadm.custom.rega.queryeditor.InclusiveOrClause;
import com.pharmadm.custom.rega.queryeditor.NotClause;
import com.pharmadm.custom.rega.queryeditor.OrderedAWCWordList;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.DateConstant;
import com.pharmadm.custom.rega.queryeditor.constant.DoubleConstant;
import com.pharmadm.custom.rega.queryeditor.constant.EndstringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.OperatorConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StartstringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.SubstringConstant;

public interface QueryVisitor {
	/**
	 * complete query
	 * @param query
	 * @return
	 * @throws java.sql.SQLException
	 */
	public String visitQuery(Query query)  throws java.sql.SQLException;
	
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
	 * number constants
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseConstant(DoubleConstant constant);	
	
	/**
	 * string representation of constants that are of an unsupported type
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseConstant(Constant constant);
	
	/**
	 * operator constants
	 * for the where clause of the query
	 * @param constant
	 * @return
	 */
	public String visitWhereClauseConstant(OperatorConstant constant);
	
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
