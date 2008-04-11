/*
 * ObjectListBuilder.java
 *
 * Created on December 4, 2003, 11:18 AM
 */

/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.reporteditor;

import java.util.*;
import java.sql.*;

import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.util.work.Work;
import com.pharmadm.util.work.WorkAdapter;

/**
 *
 * @author  kristof
 *
 * This class is responsible for building object lists for seeding object list variables,
 * based on output variables obtained from a query in a queryeditor.
 * It always seeds all object list variables, or none.
 *
 *
 */
public class QueryOutputReportSeeder {
    
    // This class is responsible for building object lists based on input from a queryeditor.
    // This could be seen as a special case of a more global class ObjectListBuilder.
    // The class itself does two things :
    //  - it allows for assigning output variables from the query to the object list variables
    //  - it builds the actual object lists given the relevant variables, using a new query
    // For this, one might introduce a few separate (maybe inner) classes
    
    private HashMap seedVariables = new HashMap();
    private HashMap seedObjectData = null;
    private ReportBuilder reporter;
    private QueryEditor querier;
    private boolean seedsChosen = false; // determines whether output variables have been assigned to each object list variable
    private boolean seedsCalculated = false;
    private Collection seedsChangeListeners = new ArrayList();
    
    /** Creates a new instance of ObjectListBuilder */
    public QueryOutputReportSeeder(ReportBuilder reporter, QueryEditor querier) {
        this.reporter = reporter;
        this.querier = querier;
    }
    
    public QueryEditor getQuerier() {
        return querier;
    }
    
    public Collection getObjectListVariables() {
        return reporter.getReport().getFormat().getObjectListVariables();
    }
    
    /* collect all available table-representing outputvariables of the given type from the associated queryeditor */
    public Collection getAvailableOutputVariables(VariableType type) {
        Collection allRes = querier.getQuery().getRootClause().getOutputVariablesAvailableForImport();
        ArrayList res = new ArrayList();
        Iterator iter = allRes.iterator();
        while (iter.hasNext()) {
            OutputVariable ovar = (OutputVariable)iter.next();
            if (ovar.consistsOfSingleFromVariable() && ovar.getVariableType().isCompatibleType(type)) {
                res.add(ovar);
            }
        }
        return res;
    }
    
    public Collection extractSeedablePrototypes(Collection prototypeList) {
        ArrayList res = new ArrayList();
        Iterator iter = prototypeList.iterator();
        while (iter.hasNext()) {
            DataGroup prototype = (DataGroup)iter.next();
            boolean seedable = true;
            Iterator variter = prototype.getObjectListVariables().iterator();
            while (variter.hasNext()) {
                ObjectListVariable olvar = (ObjectListVariable)variter.next();
                VariableType olvarType = olvar.getVariableType();
                if (getAvailableOutputVariables(olvarType).size() == 0) {
                    seedable = false;
                    break;
                }
            }
            if (seedable) {
                res.add(prototype);
            }
        }
        return res;
    }
    
    public boolean areSeedsChosen() {
        return seedsChosen;
    }
    
    public void assign(ObjectListVariable olvar, OutputVariable ovar) {
        //System.err.println("Seeder assigns !");
        seedVariables.put(olvar, ovar);
        reassessSeedsChosen();
        notifySeedsChangeListeners();
        seedsCalculated = false;
    }
    
    protected OutputVariable getAssignedVariable(ObjectListVariable olvar) {
        return (OutputVariable)seedVariables.get(olvar);
    }
    
    private void reassessSeedsChosen() {
        Iterator iter = getObjectListVariables().iterator();
        while (iter.hasNext()) {
            ObjectListVariable var = (ObjectListVariable)iter.next();
            Object seed = seedVariables.get(var);
            if (seed == null) {
                seedsChosen = false;
                return;
            }
        }
        seedsChosen = true;
    }
    
    /* collect all output variables that have been assigned to some object list variable, in the right order */
    private List getSelectedOutputVariables(List objectListVariables) {
        ArrayList res = new ArrayList();
        Iterator iter = objectListVariables.iterator();
        while (iter.hasNext()) {
            ObjectListVariable var = (ObjectListVariable)iter.next();
            Object seed = seedVariables.get(var);
            if (seed != null) {
                res.add(seed);
            } else {
                System.err.println("Warning : unseeded object list variable " + var.getUniqueName() + " !");
            }
        }
        return res;
    }
    
    /* build a select clause picking up all primary keys of the selected output variables */
    private String getObjectListSelectClause(List objectListVariables) {
        if (seedsChosen) {
            StringBuffer buffy = new StringBuffer("SELECT DISTINCT ");
            Iterator varIter = getSelectedOutputVariables(objectListVariables).iterator();
            while (varIter.hasNext()) {
                OutputVariable ovar = (OutputVariable)varIter.next();
                Iterator keyIter = ovar.getPrimaryKeyWhereClauseNames().iterator();
                while (keyIter.hasNext()) {
                    buffy.append((String)keyIter.next());
                    buffy.append(", ");
                }
            }
            buffy.setLength(buffy.length() - 2);
            return buffy.toString();
        } else {
            System.err.println("Warning : trying to build a query without all the required seeds !");
            return null;
        }
    }
    
    private void notifySeedsChangeListeners() {
        Iterator iter = seedsChangeListeners.iterator();
        while (iter.hasNext()) {
            //System.err.println("Seeder notifies 1 listener !");
            SeedsChangeListener listener = (SeedsChangeListener)iter.next();
            listener.seedsChanged();
        }
    }
    
    public void addSeedsChangeListener(SeedsChangeListener listener) {
        seedsChangeListeners.add(listener);
    }
    
    public Work createDeployResultsWork() {
        return new ResultDeployWork();
    }
    
    private class ResultDeployWork extends WorkAdapter {
        
        public ResultDeployWork() {
            setDescription("Fetching data...");
            setAmountDone(0);
            setTotalAmount(5);
        }
        
        /*
         * if all object list variables have an assigned output variable,
         * fetch all relevant primary key data from the database,
         * build objects from these primary keys,
         * and store them in valueLists preserved in seedObjectData
         */
        private void buildObjectLists(List objectListVariables) {
            if (seedsChosen) {
                try {
                    Query query = querier.getQuery();
                    String queryString = getObjectListSelectClause(objectListVariables)
                    + "\nFROM " + query.getRootClause().acceptFromClause(DatabaseManager.getInstance().getQueryBuilder())
                    + "\nWHERE " + query.getRootClause().acceptWhereClause(DatabaseManager.getInstance().getQueryBuilder());
                    
                    System.out.println(queryString);
                    QueryResult resultSet = DatabaseManager.getInstance().getDatabaseConnector().createScrollableReadOnlyStatement().executeQuery(queryString);
                    try {
                    	int count = determineNbRows(resultSet);
                        setTotalAmount(objectListVariables.size() * count);
                        ArrayList[] objectLists = new ArrayList[objectListVariables.size()];
                        for (int n = 0; n < objectListVariables.size(); n++) {
                            objectLists[n] = new ArrayList();
                        }
                        boolean mayContinue = getContinuationArbiter().mayContinue();
                        int k = 0;
                        while (mayContinue && k < count) {
                            Iterator varIter = objectListVariables.iterator();
                            int n = 0;
                            int j = 1;
                            while (mayContinue && varIter.hasNext()) {
                                ObjectListVariable olvar = (ObjectListVariable)varIter.next();
                                OutputVariable ovar = (OutputVariable)seedVariables.get(olvar);
                                int len = ovar.getPrimaryKeyWhereClauseNames().size();
                                Class[] paramTypes = new Class[len];
                                Object[] params = new Object[len];
                                for (int i = 0; i < len; i++) {
                                    params[i] = resultSet.get(k, i + j).toString();
                                    paramTypes[i] = String.class; // %$ KVB : we assume only all-String-constructors for now
                                }
                                Class valueType = olvar.getValueType();
                                String className = valueType.getName();
                                String methodName = "get" + className.substring(className.lastIndexOf(".") + 1);
                                java.lang.reflect.Method constructMethod = valueType.getDeclaredMethod(methodName, paramTypes);
                                objectLists[n].add(constructMethod.invoke(null, params));
                                j += len;
                                n++;
                                increaseAmountDone();
                                mayContinue = getContinuationArbiter().mayContinue();
                            }
                        }
                        if (mayContinue) {
                            int n = 0;
                            seedObjectData = new HashMap();
                            Iterator varIter = getSelectedOutputVariables(objectListVariables).iterator();
                            while (varIter.hasNext()) {
                                OutputVariable ovar = (OutputVariable)varIter.next();
                                seedObjectData.put(ovar, objectLists[n]);
                                n++;
                            }
                            seedsCalculated = true;
                        }
                    } finally {
                        resultSet.close();
                    }
                } catch (Exception e) {
                    // it's all over when this happens...
                    FrontEndManager.getInstance().getFrontEnd().showException(e, "Fatal Error trying to seed report variables !");
                    e.printStackTrace();
                    seedObjectData = null;
                }
            } else {
                System.err.println("Warning : trying to build object lists without the required seeds.");
            }
        }
        
        /* resultSet MUST be scrollable
         */
        private int determineNbRows(QueryResult resultSet) throws SQLException {
        	return resultSet.size();
        }
        
        /* seed the object list variables with their object lists through the controller */
        public void deployResults() {
            reassessSeedsChosen();
            if (! seedsChosen) {
                System.err.println("Please assign values to all report inputs first.");
                return;
            }
            List objectListVariables = new ArrayList(getObjectListVariables());
            if (! seedsCalculated && getContinuationArbiter().mayContinue()) {
                buildObjectLists(objectListVariables);
                if (seedObjectData != null) {
                    reporter.resetSeeds();
                    Iterator iter = objectListVariables.iterator();
                    while (iter.hasNext()) {
                        ObjectListVariable olvar = (ObjectListVariable)iter.next();
                        reporter.seedObjectList((List)seedObjectData.get(seedVariables.get(olvar)), olvar);
                    }
                }
            }
            
        }
        
        public void execute() {
            deployResults();
        }
    }
}
