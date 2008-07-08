
/** Java class "OutputVariable.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor;

import java.io.Serializable;
import java.util.*;

import com.pharmadm.custom.rega.queryeditor.catalog.DbObject;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.queryeditor.port.QueryVisitor;

/**
 * <p>
 * Represents a value that gets calculated by an AtomicWhereClause, and of
 * which the result can be reused elsewhere in the Query. Reuse occurs by
 * associating an OutputVariable with a compatible InputVariable.
 * </p>
 * <p>
 * An OutputVariable has two names. It has a non-unique, descriptive,
 * formal name. From this formal name, a Query-wide unique name is derived.
 * The unique name can be used by the user to distinguish the different
 * OutputVariables in a Query.
 * </p>
 * <p>
 * An OutputVariable also has an OutputExpression. An OutputExpression can
 * build an expression String that is inlined while building a query
 * String, when there is need to refer to an InputVariable that is
 * associated with this OutputVariable.
 * </p>
 * <p>
 * This class supports xml-encoding. 
 * The following new properties are encoded :
 *  formalName
 *  uniqueName
 *  expression
 * </p> 
 */
public class OutputVariable extends Variable implements AWCWord, Cloneable, Serializable {

	public enum RelationDisplay {SHOW, HIDE}
	public enum DescriptionDisplay {SHOW, HIDE, SHOW_WHEN_ASSIGNED, SHOW_WHEN_UNASSIGNED}
	public enum UniqueNameDisplay {SHOW, HIDE, SHOW_WHEN_ASSIGNED, SHOW_WHEN_UNASSIGNED}
	
  ///////////////////////////////////////
  // attributes

    public OutputVariable(DbObject object) {
        super(object);
        this.expression= new OutputExpression();
        this.relation = null;
    }

    /** For xml-encoding purposes only */
    public OutputVariable() {
    }
    
/**
 * <p>
 * Represents a non-unique name of the variable that has some meaning to
 * the user. For example, an OutputVariable of the type 'Time' could be
 * referred to as variable 'T'. A formal name may not:<ul>
 * <li>be null
 * <li>have a zero length
 * <li>contain other characters than letters (small or caps), digits, dashes and underscores
 * <li>have a last character that is a digit or a dash
 * </ul>
 * </p>
 * <p>
 * The formal name is persistent.
 * </p>
 * 
 */

/**
 * description of the relation this outputvariable represents
 */
    private String relation;
    
    
/**
 * <p>
 * Represents a unique name of the variable that can is unique for an
 * entire query. The uniqueName is derived from the formal name, but may be
 * longer to allow for uniqueness.
 * </p>
 * <p>
 * For example, two OutputVariables in a query, both of the type 'Time' and
 * with formal name 'T', could have uniqueNames 'T1' and 'T2'.
 * </p>
 * <p>
 * The unique name is transient, for it is always possible to derive a
 * unique name from the formal name. Although re-generation would not
 * nescessarily  always result in the same unique name, it can still be
 * used to distinguish variables from each other.
 * </p>
 * 
 */
    private String uniqueName; 

    public String getFormalName() {
    	return getObject().getVariableName();
    }
    
//    /**
//     * sets a different base variable name for this output variable
//     * than the one found in the prototype catalog
//     * @param formalName
//     */
//    public void setFormalName(String formalName) {
//        this.formalName = formalName.replace(" ", "");
//    }
    
    /**
     * returns the unique name of this output variable
     * if no unique name has been assigned yet return the formal name
     * @return
     */
    public String getUniqueName() {
    	if (uniqueName == null) {
    		return getFormalName();
    	}
		return uniqueName;
    }
    
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }
    
    public void setRelation(String name) {
    	this.relation = name;
    }
    
    public String getName(RelationDisplay relation, DescriptionDisplay description, UniqueNameDisplay uName) {
    	String name = "";
    	if (relation == RelationDisplay.SHOW && this.relation != null) {
    		name += this.relation + " ";
    	}
    	if (description == DescriptionDisplay.SHOW ||
    		description == DescriptionDisplay.SHOW_WHEN_UNASSIGNED && uniqueName == null ||
    		description == DescriptionDisplay.SHOW_WHEN_ASSIGNED && uniqueName != null) {
    		name += getObject().getDescription() + " ";
    	}
    	if (uName == UniqueNameDisplay.SHOW ||
    			uName == UniqueNameDisplay.SHOW_WHEN_UNASSIGNED && uniqueName == null ||
    			uName == UniqueNameDisplay.SHOW_WHEN_ASSIGNED && uniqueName != null) {
        		name += getUniqueName();
    	}
    	return name.trim();
    }
    
   ///////////////////////////////////////
   // associations

/**
 * <p>
 * 
 * </p>
 */
    private OutputExpression expression;


   ///////////////////////////////////////
   // access methods for associations

    public OutputExpression getExpression() {
        return expression;
    }
    /* public for xml-encoding purposes only */
    public void setExpression(OutputExpression outputExpression) {
        this.expression = outputExpression;
    }
    
    public String acceptWhereClause(QueryVisitor visitor) {
    	return visitor.visitWhereClauseOutputVariable(this);
    }
    
    /**
     * for display in query tree
     */
    public String getHumanStringValue() {
    	return getName(RelationDisplay.SHOW, DescriptionDisplay.SHOW, UniqueNameDisplay.SHOW);
    }
 
    /**
     * for display in input variable dropdowns
     */
    public String toString() {
        return getName(RelationDisplay.HIDE, DescriptionDisplay.HIDE, UniqueNameDisplay.SHOW);
    }
    
    /**
     * Makes a deep clone of the OutputVariable and its OutputExpression; 
     * the OutputExpression may contain AWCWords for which a clone has already been made (these are
     * stored in the originalToCloneMap); in that case the existing clones must be used i.o. new ones 
     */
    protected OutputVariable cloneInContext(Map<ConfigurableWord, ConfigurableWord> originalToCloneMap) throws CloneNotSupportedException {
        OutputVariable clone = (OutputVariable)super.clone();
        clone.setExpression((OutputExpression)getExpression().cloneInContext(originalToCloneMap, null));
        return clone;
    }
    
    public boolean consistsOfSingleFromVariable() {
        List<ConfigurableWord> wordList = expression.getWords();
        return ((wordList.size() == 1) && (wordList.get(0) instanceof FromVariable));
    }
    
    /**
     * returns the first FromVariable in this output variable's expression string
     * if this output variable has no FromVariable in its expression string return null
     * @return
     */
    public FromVariable getFirstFromVariable() {
    	if (getExpression().getWords().size() > 0) {
    		if (getExpression().getWords().get(0) instanceof FromVariable) {
    			return (FromVariable) getExpression().getWords().get(0);
    		}
    		else if (getExpression().getWords().get(0) instanceof OutputVariable) {
    			return ((OutputVariable) getExpression().getWords().get(0)).getFirstFromVariable();
    		}
    		else if (getExpression().getWords().get(0) instanceof InputVariable) {
    			InputVariable ivar = (InputVariable) getExpression().getWords().get(0);
    			if (ivar.getOutputVariable() != null) {
    				return ivar.getOutputVariable().getFirstFromVariable();
    			}
    			else {
    				return null;
    			}
    		}
    		else {
    			return null;
    		}
    	}
    	return null;
    }
    
    /* return the full column name uniquely identifying this field in a result set */ 
    public String getFullColumnName(Field field) {
        if (consistsOfSingleFromVariable() && (((FromVariable)expression.getWords().get(0)).getObject().getTableName().equals(field.getTable().getName()))) {
            return getUniqueName() + "." + field.getDescription();
        } else { 
            System.err.println("Error : trying to get a field from a variable that does not know it");
            return null;
        }
    }
    
    public Collection<String> getPrimaryKeyColumnNames() {
        if (consistsOfSingleFromVariable()) {
            Table table = ((FromVariable)expression.getWords().get(0)).getObject().getTable();
            Collection<String> res = new ArrayList<String>();
            Iterator<Field> iter = table.getPrimaryKeyFields().iterator();
            while (iter.hasNext()) {
                Field field = iter.next();
                res.add(getFullColumnName(field)); 
            }
            return res;
        } else { 
            System.err.println("Error : trying to get a field from a variable that does not know it");
            return null;
        }
    }
    
    /* return the full name uniquely identifying this field in a query (select / where clause) */ 
    public String acceptWhereClauseFullName(QueryVisitor visitor, Field field) {
    	return visitor.visitWhereClauseFullNameOutputVariable(this, field);
    }
    
    public Collection<String> getPrimaryKeyWhereClauseNames() {
        if (consistsOfSingleFromVariable()) {
            Table table = ((FromVariable)expression.getWords().get(0)).getObject().getTable();
            Collection<String> res = new ArrayList<String>();
            Iterator<Field> iter = table.getPrimaryKeyFields().iterator();
            while (iter.hasNext()) {
                Field field = iter.next();
                res.add(acceptWhereClauseFullName(DatabaseManager.getInstance().getQueryBuilder(), field)); 
            }
            return res;
        } else { 
            System.err.println("Error : trying to get a field from a variable that does not know it");
            return null;
        }
    }

	public String getImmutableStringValue() {
		String str = "(" + getFormalName() + ":";
		for (ConfigurableWord word : getExpression().getWords()) {
			str += word.getImmutableStringValue() + " ";
		}
		return str.trim() + ")";
	}

	public boolean equals(Object o) {
		if (o instanceof OutputVariable) {
			OutputVariable ovar = (OutputVariable) o;
			return ovar.getUniqueName().equals(getUniqueName());
		}
		return false;
	}
   
} // end OutputVariable
