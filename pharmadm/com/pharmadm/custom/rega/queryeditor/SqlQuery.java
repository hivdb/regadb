package com.pharmadm.custom.rega.queryeditor;

import java.sql.SQLException;
import java.util.Iterator;

public class SqlQuery implements QueryVisitor {

	public String visitQuery(Query query) throws java.sql.SQLException{
        return query.getSelectList().accept(this)
        + "\nFROM " + query.getRootClause().acceptFromClause(this)
        + "\nWHERE " + query.getRootClause().acceptWhereClause(this);
	}

	public String visitSelectionSatusList(SelectionStatusList selectList) {
        StringBuffer buffy = new StringBuffer("SELECT  ");
        Iterator iter = selectList.getSelections().iterator();
        while (iter.hasNext()) {
            Selection selection = (Selection)iter.next();
            if (selection.isSelected()) {
                OutputVariable ovar = (OutputVariable)selection.getObject();
                if (selection instanceof TableSelection) {
                    // %$ KVB : for the Hibernate version, we need to apply reflection on these fields
                    //          so that fields of class types are represented by an identifier
                    Iterator fieldIter = ((TableSelection)selection).getSubSelections().iterator();
                    while (fieldIter.hasNext()) {
                        FieldSelection subSelection = (FieldSelection)fieldIter.next();
                        if (subSelection.isSelected()) {
                            buffy.append(ovar.acceptWhereClauseFullName(this, (Field)(subSelection.getObject())));
                            buffy.append(", ");
                        }
                    }
                }
                else { // selection instanceof OutputSelection
                    buffy.append(ovar.getExpression().acceptWhereClause(this));
                    buffy.append(", ");
                }
            }
        }
        buffy.setLength(buffy.length() - 2);
        return buffy.toString();
	}

	public String visitWhereClauseOrderedAWCWordList(OrderedAWCWordList list) {
        StringBuffer sb = new StringBuffer();
        Iterator iterWords = list.getWords().iterator();
        while (iterWords.hasNext()) {
            AWCWord word = (AWCWord)iterWords.next();
			sb.append(word.acceptWhereClause(this));
        }
        return sb.toString();
	}

	public String visitWhereClauseOutputVariable(OutputVariable ovar) {
        return ovar.getExpression().acceptWhereClause(this);
	}

	public String visitWhereClauseEndstringConstant(EndstringConstant constant) {
        return "\'%" + constant.getValue().toString() + "\'";
	}

	public String visitWhereClauseStartstringConstant(StartstringConstant constant) {
        return "\'" + constant.getValue().toString() + "%\'";
	}

	public String visitWhereClauseStringConstant(StringConstant constant) {
		return "\'" + constant.getValue().toString() + "\'";
	}

	public String visitWhereClauseSubstringConstant(SubstringConstant constant) {
        return "\'%" + constant.getValue().toString() + "%\'";
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
        if (ovar.consistsOfSingleFromVariable() && (((FromVariable) ovar.getExpression().getWords().get(0)).getTable() == field.getTable())) {
            return ((FromVariable)ovar.getExpression().getWords().get(0)).acceptWhereClause(this) + "." + field.getName();
        } 
        else { 
            System.err.println("Error : trying to get a field from a variable that does not know it");
            return null;
        }
	}

	public String visitWhereClauseAndClause(AndClause clause) throws SQLException{
        StringBuffer sb = new StringBuffer();
        Iterator iterChildren = clause.iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
            String extraWhereClause = child.acceptWhereClause(this);
            if (extraWhereClause != null && (extraWhereClause.length() > 0)) {
                if (sb.length() > 0) {
                    sb.append(") and (");
                } else {
                    sb.append('(');
                }
                sb.append(extraWhereClause);
            }
        }
        if (sb.length() > 0) {
            sb.append(')');
        }
        return sb.toString();
	}

	public String visitWhereClauseInclusiveOrClause(InclusiveOrClause clause) throws SQLException{
        StringBuffer sb = new StringBuffer();
        Iterator iterChildren = clause.iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
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
        Iterator iterChildren = clause.getChildren().iterator();
        if (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
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
        Iterator iterFromVars = clause.getFromVariables().iterator();
        while (iterFromVars.hasNext()) {
            FromVariable fromVar = (FromVariable)iterFromVars.next();
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(fromVar.getFromClauseStringValue(this));
        }
        return sb.toString();
	}

	public String visitFromClauseComposedWhereClause(ComposedWhereClause clause) throws SQLException {
        StringBuffer sb = new StringBuffer();
        Iterator iterChildren = clause.iterateChildren();
        while (iterChildren.hasNext()) {
            WhereClause child = (WhereClause)iterChildren.next();
            String extraFromClause = child.acceptFromClause(this);
            if (extraFromClause != null && (extraFromClause.length() > 0)) {
                if (sb.length() > 0) {
                    sb.append(", ");
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
