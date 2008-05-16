package com.pharmadm.custom.rega.queryeditor.port.jdbc;

import java.sql.SQLException;
import java.util.Iterator;

import com.pharmadm.custom.rega.queryeditor.AWCWord;
import com.pharmadm.custom.rega.queryeditor.AndClause;
import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ComposedWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.Field;
import com.pharmadm.custom.rega.queryeditor.FieldSelection;
import com.pharmadm.custom.rega.queryeditor.FromVariable;
import com.pharmadm.custom.rega.queryeditor.InclusiveOrClause;
import com.pharmadm.custom.rega.queryeditor.NotClause;
import com.pharmadm.custom.rega.queryeditor.OrderedAWCWordList;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.Selection;
import com.pharmadm.custom.rega.queryeditor.SelectionStatusList;
import com.pharmadm.custom.rega.queryeditor.TableSelection;
import com.pharmadm.custom.rega.queryeditor.WhereClause;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.DateConstant;
import com.pharmadm.custom.rega.queryeditor.constant.EndstringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StartstringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.StringConstant;
import com.pharmadm.custom.rega.queryeditor.constant.SubstringConstant;
import com.pharmadm.custom.rega.queryeditor.port.*;

public class SqlQuery implements QueryVisitor {

	public String visitQuery(Query query) throws java.sql.SQLException{
		String select = query.getSelectList().accept(this);
		String from = query.getRootClause().acceptFromClause(this);
		String where =  query.getRootClause().acceptWhereClause(this);
		String q = "";
		if (select.length() > 0) {
			q += "\nSELECT\n\t" + select;
		}
		if (from.length() > 0) {
			q += "\nFROM\n\t" + from;
		}
		if (where.length() > 0) {
			q += "\nWHERE\n\t" + where;
		}
        return q;
	}

	public String visitSelectionSatusList(SelectionStatusList selectList) {
        StringBuffer buffy = new StringBuffer();
        Iterator<Selection> iter = selectList.getSelections().iterator();
        while (iter.hasNext()) {
            Selection selection = iter.next();
            if (selection.isSelected()) {
                OutputVariable ovar = (OutputVariable)selection.getObject();
                if (selection instanceof TableSelection) {
                    if (DatabaseManager.getInstance().getDatabaseConnector().isTableSelectionAllowed()) {
                    	buffy.append(ovar.acceptWhereClause(this));
                        buffy.append(",\n\t");
                    }

                    // %$ KVB : for the Hibernate version, we need to apply reflection on these fields
                    //          so that fields of class types are represented by an identifier
                    Iterator<Selection> fieldIter = ((TableSelection)selection).getSubSelections().iterator();
                    while (fieldIter.hasNext()) {
                        FieldSelection subSelection = (FieldSelection) fieldIter.next();
                        if (subSelection.isSelected()) {
                            buffy.append(ovar.acceptWhereClauseFullName(this, (Field)(subSelection.getObject())));
                            buffy.append(",\n\t");
                        }
                    }
                    
                }
                else { // selection instanceof OutputSelection
                    buffy.append(ovar.getExpression().acceptWhereClause(this));
                    buffy.append(",\n\t");
                }
            }
        }
        String result = buffy.toString();
        if (result.lastIndexOf(',') > 0) {
        	result = result.substring(0, result.lastIndexOf(','));
        }
        return result;
	}

	public String visitWhereClauseOrderedAWCWordList(OrderedAWCWordList list) {
        StringBuffer sb = new StringBuffer();
        Iterator<ConfigurableWord> iterWords = list.getWords().iterator();
        while (iterWords.hasNext()) {
            AWCWord word = (AWCWord) iterWords.next();
            String str = word.acceptWhereClause(this);
            if (!str.equals("1=1")) {
            	sb.append(str);
            }
        }
        return sb.toString();
	}

	public String visitWhereClauseOutputVariable(OutputVariable ovar) {
        return ovar.getExpression().acceptWhereClause(this);
	}

	public String visitWhereClauseEndstringConstant(EndstringConstant constant) {
		String str = constant.getValue().toString();
		str = str.replace('*', '%');
		str = str.replace('?', '_');
        return "\'%" + str + "\'";
	}

	public String visitWhereClauseStartstringConstant(StartstringConstant constant) {
		String str = constant.getValue().toString();
		str = str.replace('*', '%');
		str = str.replace('?', '_');
        return "\'" + str + "%\'";
	}

	public String visitWhereClauseStringConstant(StringConstant constant) {
		String str = constant.getValue().toString();
		str = str.replace('*', '%');
		str = str.replace('?', '_');
		return "\'" + str + "\'";
	}

	public String visitWhereClauseSubstringConstant(SubstringConstant constant) {
		String str = constant.getValue().toString();
		str = str.replace('*', '%');
		str = str.replace('?', '_');
        return "\'%" + str + "%\'";
	}

	public String visitWhereClauseConstant(Constant constant) {
		return constant.getValue().toString();
	}

	public String visitWhereClauseDateConstant(DateConstant constant) {
		return "TO_DATE(\'" + constant.getFormat().format(constant.getValue()) + "\', 'YYYY-MM-DD')";
	}

	public String visitWhereClauseFromVariable(FromVariable fromVar) {
		return fromVar.getUniqueName();
	}

	public String visitWhereClauseFullNameOutputVariable(OutputVariable ovar, Field field) {
        if (ovar.consistsOfSingleFromVariable() && (((FromVariable) ovar.getExpression().getWords().get(0)).getTableName().equals(field.getTable().getName()))) {
            return ((FromVariable)ovar.getExpression().getWords().get(0)).acceptWhereClause(this) + "." + field.getName();
        } 
        else { 
            System.err.println("Error : trying to get a field from a variable that does not know it");
            return null;
        }
	}

	public String visitWhereClauseAndClause(AndClause clause) throws SQLException{
        StringBuffer sb = new StringBuffer();
        Iterator<WhereClause> iterChildren = clause.iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = iterChildren.next();
            String extraWhereClause = child.acceptWhereClause(this);
            if (extraWhereClause != null && (extraWhereClause.length() > 0) && !extraWhereClause.equals("1=1")) {
                if (sb.length() > 0) {
                    sb.append(") AND\n\t(");
                } else {
                    sb.append('(');
                }
                sb.append(extraWhereClause);
            }
        }
        if (sb.length() > 0) {
            sb.append(")\n");
        }
        return sb.toString();
	}

	public String visitWhereClauseInclusiveOrClause(InclusiveOrClause clause) throws SQLException{
        StringBuffer sb = new StringBuffer();
        Iterator<WhereClause> iterChildren = clause.iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = iterChildren.next();
            String extraWhereClause = child.acceptWhereClause(this);
            if (extraWhereClause != null && (extraWhereClause.length() > 0)) {
                if (sb.length() > 0) {
                    sb.append(") or (");
                } else {
                    sb.append('(');
                }
                sb.append("EXISTS (SELECT 1 FROM ");
                String extraFromClause = child.acceptFromClause(this);
                if (extraFromClause != null && (extraFromClause.length() > 0)) {
                    sb.append(extraFromClause);
                }
                else {
                    sb.append("DUAL");
                }
                sb.append(" WHERE (");
                sb.append(extraWhereClause);
                sb.append("))");
            }
        }
        if (sb.length() > 0) {
            sb.append(')');
        }
        return sb.toString();
	}

	public String visitWhereClauseNotClause(NotClause clause) throws SQLException {
        Iterator<WhereClause> iterChildren = clause.getChildren().iterator();
        if (iterChildren.hasNext()) {
            WhereClause child = iterChildren.next();
            StringBuffer sb = new StringBuffer("NOT EXISTS (SELECT 1 FROM ");
            String childFromClause = child.acceptFromClause(this);
            if (childFromClause != null && (childFromClause.length() > 0)) {
                sb.append(childFromClause);
            }
            else {
                sb.append("DUAL");
            }
            sb.append(" WHERE (");
            sb.append(child.acceptWhereClause(this));
            sb.append("))");
            return sb.toString();
        } else {
            return "1=1";  // always true
        }
	}

	public String visitFromClauseAtomicWhereClause(AtomicWhereClause clause) throws SQLException {
        StringBuffer sb = new StringBuffer();
        Iterator<FromVariable> iterFromVars = clause.getFromVariables().iterator();
        while (iterFromVars.hasNext()) {
            FromVariable fromVar = iterFromVars.next();
            if (sb.length() > 0) {
                sb.append(",\n\t");
            }
            sb.append(fromVar.getFromClauseStringValue(this));
        }
        return sb.toString();
	}

	public String visitFromClauseComposedWhereClause(ComposedWhereClause clause) throws SQLException {
        StringBuffer sb = new StringBuffer();
        Iterator<WhereClause> iterChildren = clause.iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = iterChildren.next();
            String extraFromClause = child.acceptFromClause(this);
            if (extraFromClause != null && (extraFromClause.length() > 0)) {
                if (sb.length() > 0) {
                    sb.append(",\n\t");
                }
                sb.append(extraFromClause);
            }
        }
        return sb.toString();
	}

	public String visitFromClauseInclusiveOrClause(InclusiveOrClause clause) {
		return new String("");
	}

	public String visitFromClauseNotClause(NotClause clause) {
		return new String("");
	}

	public String visitFromClauseFromVariable(FromVariable fromVar) {
        return fromVar.getTableName() + " " + fromVar.getUniqueName();
	}
}
