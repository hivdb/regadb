
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

import com.pharmadm.custom.rega.queryeditor.FromVariable;
import com.pharmadm.custom.rega.queryeditor.InclusiveOrClause;
import com.pharmadm.custom.rega.queryeditor.NotClause;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.DateConstant;
import com.pharmadm.custom.rega.queryeditor.constant.EndstringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StartstringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.SubstringConstant;
import com.pharmadm.custom.rega.queryeditor.port.jdbc.SqlQuery;


public class HibernateQuery extends SqlQuery {
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

	public String visitFromClauseFromVariable(FromVariable fromVar) {
        return "net.sf.regadb.db." + fromVar.getObject().getTableName() + " " + fromVar.getUniqueName();
	}	
	
	
	
	
//	
//	
//	public String visitWhereClauseEndstringConstant(EndstringConstant constant) {
//		String str = constant.getValue().toString();
//		str = str.replace('*', '%');
//		str = str.replace('?', '_');
//        return "\'%" + str + "\'";
//	}
//
//	public String visitWhereClauseStartstringConstant(StartstringConstant constant) {
//		String str = constant.getValue().toString();
//		str = str.replace('*', '%');
//		str = str.replace('?', '_');
//        return "\'" + str + "%\'";
//	}
//
//	public String visitWhereClauseStringConstant(StringConstant constant) {
//		String str = constant.getValue().toString();
//		str = str.replace('*', '%');
//		str = str.replace('?', '_');
//		return "\'" + str + "\'";
//	}
//
//	public String visitWhereClauseSubstringConstant(SubstringConstant constant) {
//		String str = constant.getValue().toString();
//		str = str.replace('*', '%');
//		str = str.replace('?', '_');
//        return "\'%" + str + "%\'";
//	}
//
//	public String visitWhereClauseConstant(Constant constant) {
//		return constant.getValue().toString();
//	}
//
//	public String visitWhereClauseDateConstant(DateConstant constant) {
//		return "TO_DATE(\'" + constant.getHumanStringValue() + "\', 'DD-MM-YYYY')";
//	}
}



