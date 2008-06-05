
/** Java class "AWCPrototypeCatalog.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.catalog;

import java.util.*;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.NullComposition;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;

/**
 * <p>
 * A catalog containing AtomicWhereClause prototypes ('Prototype' pattern).
 * </p>
 * <p>
 * Typically, a catalog is build from a file describing the different
 * prototypes.
 * </p>
 * <p>
 * The prototypes should not be manipulated directly, but rather be cloned
 * first. The catalog can determine which of its prototype represent
 * AtomicWhereClauses whose clones can be added at a point in a WhereClause
 * composition where a given Collection of OutputVariables is available.
 * </p>
 *
 * Prototype pattern
 */
public class AWCPrototypeCatalog {
    
    private Map<String, DbObject> objectNames = new HashMap<String, DbObject>();
    private Map<String, ObjectRelation> relations = new HashMap<String, ObjectRelation>();
    private List<AtomicWhereClause> atomicWhereClauses = new ArrayList<AtomicWhereClause>();
	private Status status = Status.EMPTY;
	private int size;
    
    public enum Status {
    	EMPTY,
    	BUSY,
    	DONE,
    	FAILED;
    }
    
    public void setStatus(Status st) {
    	this.status = st;
    }
    
    public Status getStatus() {
    	return status;
    }
    
    /**
     * adds the given atomic where clause to the catalog
     * @param atomicWhereClause
     */
    public void add(AtomicWhereClause atomicWhereClause) {
    	if (atomicWhereClause != null) {
    		this.atomicWhereClauses.add(atomicWhereClause);
    	}
    }
    
    public void addAll(List<AtomicWhereClause> atomicWhereClauses) {
    	for (AtomicWhereClause clause : atomicWhereClauses) {
    		add(clause);
    	}
    }
    
    public void addRelation(ObjectRelation r) {
    	relations.put(r.getInputTable().getTableName() + "." + r.getForeignTable().getTableName(), r);
    }
    
    public ObjectRelation getRelation(String start, String end) {
    	ObjectRelation r = relations.get(start + "." + end);
    	if (r == null) {
    		System.err.println("DB Relation " + start + "." + end + " not found");
    	} 
    	return r;
    }

    public void addObject(DbObject object) {
    	objectNames.put(object.getTableName() + "." + object.getPropertyName(), object);
    }
    
    public DbObject getObject(String tableName, String propertyName) {
    	DbObject obj = objectNames.get(tableName + "." + propertyName);
    	if (obj == null) {
    		System.err.println("DB Object " + tableName + "." + propertyName + " not found");
    	}
    	return obj;
    }
    
    public DbObject getObject(String tableName) {
    	return getObject(tableName, null);
    }
    
	/**
	 * returns true if the given sql data type number belongs to a string
	 * @param dataType an sql data type number
	 * @return true when the data type is a string
	 */
    public static boolean isStringType(int dataType) {
    	return dataType == 12;
    }

	/**
	 * returns true if the given sql data type number belongs to a boolean
	 * @param dataType an sql data type number
	 * @return true when the data type is a boolean
	 */
    public static boolean isBooleanType(int dataType) {
    	return dataType == -7;
    }
    
	/**
	 * returns true if the given sql data type number belongs to a date
	 * @param dataType an sql data type number
	 * @return true when the data type is a date
	 */
    public static boolean isDateType(int dataType) {
    	return (dataType >= 91) && (dataType <= 93);
    }
    
	/**
	 * returns true if the given sql data type number belongs to a numeric value
	 * @param dataType an sql data type number
	 * @return true when the data type is a number
	 */
    public static boolean isNumericType(int dataType) {
    	return (((8 >= dataType) && (dataType >=1)) || dataType == 1111 || dataType == -5);
    }   
    
    /**
     * <p>
     * Returns a collection with all AtomicWhereClause prototypes that are
     * compatible with the given list of OutputVariables. Compatible means that
     * for all types of the InputVariables of an AtomicWhereClause prototype,
     * there is at least one OutputVariable present in the given Collection.
     * Note that the presence of one OutputVariable may satisfy many
     * InputVariables.
     * </p>
     * <p>
     *
     * @param availableOutputVariables the Collection of OutputVariables that
     * are available to bind InputVariables to.
     * </p>
     * <p>
     * @return a Collection with all AtomicWhereClause prototypes that are
     * compatible with the given list of OutputVariables
     * </p>
     */
    public Collection<AtomicWhereClause> getAWCPrototypes(Collection<OutputVariable> availableOutputVariables) {
    	// your code here
        Collection<AtomicWhereClause> result = new ArrayList<AtomicWhereClause>();
        Iterator<AtomicWhereClause> iter = atomicWhereClauses.iterator();
        while (iter.hasNext()) {
            AtomicWhereClause clause = iter.next();
            boolean clauseOk = true;
            Iterator<InputVariable> inputIter = clause.getInputVariables().iterator();
            while (inputIter.hasNext()) {
                InputVariable ivar = inputIter.next();
                boolean varOk = false;
                Iterator<OutputVariable> outputIter = availableOutputVariables.iterator();
                while (outputIter.hasNext()) {
                    OutputVariable ovar = outputIter.next();
                    if (ivar.isCompatible(ovar)) {
                        varOk = true;
                        break;
                    } 
                }
                if (! varOk) {
                    clauseOk = false;
                    break;
                }
            }
            if (clauseOk) {
            	if (!result.contains(clause)) {
            		result.add(clause);
            	}
            }
        }
        return result;
    }
    
    /**
     * returns a collection of all clauses that are in the same group and have the same
     * composition behavior as the given clause
     * clauses that have a NullComposition only have 
     */
    public List<AtomicWhereClause> getSimilarClauses(AtomicWhereClause clause) {
        List<AtomicWhereClause> result = new ArrayList<AtomicWhereClause>();
        if (clause.getCompositionBehaviour() instanceof NullComposition) {
        	result.add(clause);
        }
        else {
			try {
		    	for (AtomicWhereClause aClause : atomicWhereClauses) {
		    		if (aClause.getGroups().containsAll(clause.getGroups()) &&
		    			clause.getCompositionBehaviour().getClass().equals(aClause.getCompositionBehaviour().getClass())) {
							result.add((AtomicWhereClause) aClause.clone());
		    		}
		    	}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
        }
    	
    	return result;
    }
    
    public void setTotalSize(int size) {
    	this.size = size;
    }
    
    public int getSizePercentage() {
    	if (size == 0) {
    		return 0;
    	}
    	return (int) Math.min(100, (double) atomicWhereClauses.size() * 100 / size);
    }
}