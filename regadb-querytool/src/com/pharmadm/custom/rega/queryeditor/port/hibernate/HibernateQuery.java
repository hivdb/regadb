
/** Java class "HibernateQuery.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.port.hibernate;

import java.sql.SQLException;
import java.util.Iterator;

import net.sf.regadb.util.date.DateUtils;

import com.pharmadm.custom.rega.queryeditor.FromVariable;
import com.pharmadm.custom.rega.queryeditor.InclusiveOrClause;
import com.pharmadm.custom.rega.queryeditor.NotClause;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject.ValueType;
import com.pharmadm.custom.rega.queryeditor.constant.BooleanConstant;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.DateConstant;
import com.pharmadm.custom.rega.queryeditor.constant.DoubleConstant;
import com.pharmadm.custom.rega.queryeditor.constant.EndstringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StartstringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.SubstringConstant;
import com.pharmadm.custom.rega.queryeditor.port.jdbc.SqlQuery;


public class HibernateQuery extends SqlQuery {
	private Query query;
	private boolean inselect;
	
	// make sure query building is thread safe as we keep
	// state during building
	public synchronized String visitQuery(Query query)  throws java.sql.SQLException {
		this.query = query;
		inselect = true;
		String select = query.getSelectList().accept(this);
		inselect = false;
		String from = query.getRootClause().acceptFromClause(this);
		String where =  query.getRootClause().acceptWhereClause(this);
		String q = "";
		if (select.length() > 0) {
			q += "\nSELECT\n\t" + select;
		}
		if (query.getFastaExport() != null)
			q += ",\n\t" + query.getFastaExport().getViralIsolate().acceptWhereClause(this);
		if (from.length() > 0) {
			q += "\nFROM\n\t" + from;
		}
		if (where.length() > 0) {
			q += "\nWHERE\n\t" + where;
		}
        return q;
	}

	
	public String visitWhereClauseInclusiveOrClause(InclusiveOrClause clause) throws SQLException{
        StringBuffer sb = new StringBuffer();
        Iterator<WhereClause> iterChildren = clause.iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = iterChildren.next();
            String extraWhereClause = child.acceptWhereClause(this);
            if (extraWhereClause != null && (extraWhereClause.length() > 0)) {
                if (sb.length() > 0) {
                    sb.append(" OR\n\t ");
                } 
                
                String extraFromClause = child.acceptFromClause(this);
                if (clause.getChildCount() > 1) sb.append("(");
                if (extraFromClause != null && (extraFromClause.length() > 0)) {
                    sb.append("EXISTS (SELECT 1 FROM ");
                    sb.append(extraFromClause);
                    sb.append(" WHERE (");
                    sb.append(extraWhereClause);
                    sb.append("))");
                }
                else {
                    sb.append(extraWhereClause);
                }
                if (clause.getChildCount() > 1) sb.append(")");
            }
        }
        return sb.toString();
	}

	public String visitWhereClauseNotClause(NotClause clause) throws SQLException {
        Iterator<WhereClause> iterChildren = clause.getChildren().iterator();
        if (iterChildren.hasNext()) {
            WhereClause child = iterChildren.next();
            String extraWhereClause = child.acceptWhereClause(this);
            String childFromClause = child.acceptFromClause(this);
            if (childFromClause != null && (childFromClause.length() > 0)) {
	            StringBuffer sb = new StringBuffer(" NOT ");
                sb.append("EXISTS (SELECT 1 FROM ");
            	sb.append(childFromClause);
                if (extraWhereClause != null && extraWhereClause.length() > 0) {
	                sb.append(" WHERE ");
	                sb.append(child.acceptWhereClause(this));
            	}
                sb.append(")");
                return sb.toString();
            }
            else if (extraWhereClause != null && extraWhereClause.length() > 0) {
	            StringBuffer sb = new StringBuffer(" NOT ");
                sb.append(extraWhereClause);
	            return sb.toString();
            }
        }
        return "1=1";  // always true
	}
	
	public String visitWhereClauseOutputVariable(OutputVariable ovar) {
        String str = ovar.getExpression().acceptWhereClause(this);
        if (ovar.getObject().getValueType() == ValueType.Number) {
        	return "cast(" + str +", double)";
        }
        else {
        	return str;
        }
	}	

	public String visitFromClauseFromVariable(FromVariable fromVar) {
        return "net.sf.regadb.db." + fromVar.getObject().getTableName() + " " + fromVar.getUniqueName();
	}	
	
	/**
	 * format and escape string
	 */
	protected String formatString(String str) {
		str = super.formatString(str);
		str = str.replace("'", "''");
		return str;
	}	
	
	/**
	 * format but don't escape string
	 * @param str
	 * @return
	 */
	private String formatNonPreparedString(String str) {
		return super.formatString(str);
	}
	
	/**
	 * escape string
	 * @param str
	 * @return
	 */
	private String escapeString(String str) {
		str = str.replace("'", "''");
		return str;
	}
	

	
	private String createKey(Object o) {
		if (inselect) {
			String str = escapeString(o.toString());
			if (o instanceof String) {
				str = "'" + str + "'";
			}
			return str;
		}
		else {
			if(query==null)
				return "";
			else
				return  ":" + query.createKey(o);
		}
	}
	
	public String visitWhereClauseEndstringConstant(EndstringConstant constant) {
		String str = constant.getValue().toString();
        return "%" + createKey(formatNonPreparedString(str));
	}

	public String visitWhereClauseStartstringConstant(StartstringConstant constant) {
		String str = constant.getValue().toString();
        return createKey(formatNonPreparedString(str)) + "%";
	}

	public String visitWhereClauseStringConstant(StringConstant constant) {
		String str = constant.getValue().toString();
		return createKey(formatNonPreparedString(str));
	}

	public String visitWhereClauseSubstringConstant(SubstringConstant constant) {
		String str = constant.getValue().toString();
        return createKey("%" + formatNonPreparedString(str) + "%");
	}
	
	public String visitWhereClauseConstant(BooleanConstant constant) {
		if (constant.getValue().toString().equals("true")) {
			return createKey(true);
		}
		else {
			return createKey(false);
		}
	}

	public String visitWhereClauseConstant(DoubleConstant constant) {
		Object val = Double.parseDouble(constant.getValue().toString());
		return createKey(val);
	}	
	
	public String visitWhereClauseConstant(Constant constant) {
		return createKey(constant.getValue());
	}

	public String visitWhereClauseDateConstant(DateConstant constant) {
		return "TO_DATE(" + createKey(constant.getHumanStringValue()) + ", '" + DateUtils.getHQLdateFormatString() + "')";
	}
}



