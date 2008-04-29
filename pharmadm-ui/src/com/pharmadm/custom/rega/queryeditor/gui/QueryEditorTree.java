
/** Java class "QueryEditor.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.queryeditor.gui;

import java.util.*;
import java.beans.DefaultPersistenceDelegate;
import java.io.*;

import javax.swing.tree.*;

import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.constant.*;
import com.pharmadm.custom.rega.queryeditor.persist.*;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.savable.*;

/**
 * The controller ('Controller' pattern) for editing a Query.
 *
 */
public class QueryEditorTree extends DefaultTreeModel implements QueryEditor {
    
    private Query query;
    private boolean dirty = false;
    private Collection<DirtinessListener> dirtinessListeners = new ArrayList<DirtinessListener>();
    private Collection<SelectionListChangeListener> listChangeListeners = new ArrayList<SelectionListChangeListener>();

    private final SelectionChangeListener selectionListener = new SelectionChangeListener() {
        public void selectionChanged() {
            setDirty(true);
        }
    };
    
    public QueryEditorTree(Query query) {
        super(new WhereClauseTreeNode(query.getRootClause()), true);
        this.query = query;
        query.getSelectList().addSelectionChangeListener(selectionListener);
    }
    
    ///////////////////////////////////////
    // access methods for associations
    
    public Query getQuery() {
        return query;
    }
    
    private void setQuery(Query query) {
        if (this.query != null) {
            this.query.getSelectList().removeSelectionChangeListener(selectionListener);
        }
        this.query = query;
        setRoot(new WhereClauseTreeNode(query.getRootClause()));
        Object[] rootPath = {getRoot()};
        fireTreeStructureChanged(this, rootPath, null, null);
        updateSelectionList();
        query.getSelectList().addSelectionChangeListener(selectionListener);
    }
    
    ///////////////////////////////////////
    // operations
    
    private WhereClauseTreeNode getNode(WhereClause clause) {
        return getNode(clause, (WhereClauseTreeNode)getRoot());
    }
    
    private WhereClauseTreeNode getNode(WhereClause clause, WhereClauseTreeNode node) {
        if (node.getUserObject() == clause) {
            return node;
        }
        Enumeration enumChildren = node.children();
        while (enumChildren.hasMoreElements()) {
            WhereClauseTreeNode childNode = (WhereClauseTreeNode)enumChildren.nextElement();
            WhereClauseTreeNode tryNode = getNode(clause, childNode);
            if (tryNode != null) {
                return tryNode;
            }
        }
        return null;
    }
    
    /**
     * <p>
     * Adds a child to the given parent clause, if supported.
     * </p>
     * <p>
     *
     * @throws IllegalWhereClauseCompositionException iff
     * !parent.acceptsAdditionalChild()
     * </p>
     * <p>
     * @param parent the (Composed)WhereClause where to add the new child to.
     * </p>
     * <p>
     * @param child the WhereClause to add to the parent.
     * </p>
     */
    public void addChild(WhereClause parent, WhereClause child) throws IllegalWhereClauseCompositionException {
        parent.addChild(child, getQuery().getUniqueNameContext());
        WhereClauseTreeNode parentNode = getNode(parent);
        insertNodeInto(new WhereClauseTreeNode(child), parentNode, parentNode.getChildCount());
        updateSelectionList();
        setDirty(true);
    }
    
    /**
     * <p>
     * Removes a child from the given parent (Composed)WhereClause.
     * Does nothing if the given WhereClause isn't a child of the parent.
     * </p>
     * <p>
     *
     * <p>
     * @param parent the parent WhereClause to remove the child from
     * </p>
     * <p>
     * @param child the child WhereClause to remove from the parent
     * </p>
     */
    public void removeChild(WhereClause parent, WhereClause child) {
        parent.removeChild(child);
        WhereClauseTreeNode childNode = getNode(child);
        if (childNode != null) {
        	removeNodeFromParent(childNode);
        }
        updateSelectionList();
        setDirty(true);
    }
    
    public void replaceChild(WhereClause parent, WhereClause oldChild, WhereClause newChild) throws IllegalWhereClauseCompositionException  {
        parent.replaceChild(oldChild, newChild, getQuery().getUniqueNameContext());
        WhereClauseTreeNode parentNode = getNode(parent);
        WhereClauseTreeNode childNode = getNode(oldChild);
        int i = getIndexOfChild(parentNode, childNode);
        removeNodeFromParent(childNode);
        insertNodeInto(new WhereClauseTreeNode(newChild), parentNode, i);
        updateSelectionList();
        setDirty(true);
    }
    
    private void updateSelectionList() {
        getQuery().getSelectList().update();
        notifySelectionListChangeListeners();
    }
    
    /**
     * <p>
     * Reports whether an additional child can be added to the given parent
     * (Composed)WhereClause.
     * </p>
     * <p>
     *
     * @return true iff an additional child can be added to the parent
     * (Composed)WhereClause
     * </p>
     * <p>
     * @param clause the parent to add a child to
     * </p>
     */
    public boolean acceptsAdditionalChild(WhereClause clause) {
        return clause.acceptsAdditionalChild();
    }
    
    /**
     * <p>
     * Iterates through all immediate children of the given WhereClause. If the
     * WhereClause is atomic, returns an empty Iterator. Never returns null.
     * </p>
     * <p>
     *
     * @return an Iterator through all immediate children of the given
     * WhereClause
     * </p>
     * <p>
     * @param clause the parent WhereClause from which the children are
     * requested
     * </p>
     */
    public Iterator<WhereClause> iterateChildren(WhereClause clause) {
        return clause.iterateChildren();
    }
    
    /**
     * <p>
     * Iterates through all immediate atomic children of the given parent
     * WhereClause. If there are no atomic children, then an empty Iterator is
     * returned. Never returns null.
     * </p>
     * <p>
     *
     * @return an Iterator through all immediate atomic children of the given
     * WhereClause
     * </p>
     * <p>
     * @param clause the parent of which the atomic children are requested
     * </p>
     */
    public Iterator<WhereClause> iterateAtomicChildren(WhereClause clause) {
        return clause.iterateAtomicChildren();
    }
    
    /**
     * <p>
     * Calculates whether the given WhereClause is valid, i.e. wether all
     * constants are set and all input variables are bound, for the given
     * WhereClause and for all of its descendants.
     * </p>
     * <p>
     *
     * @return whether the given WhereClause and all of its descendants are
     * valid.
     * </p>
     * <p>
     * @param clause the WhereClause that is to be determined to be valid
     * </p>
     */
    public boolean isValid(WhereClause clause) {
        return clause.isValid();
    }
    
    /**
     * Wraps the given clause in a surrounding AND node.
     * Does nothing if the given clause is the root clause.
     *
     * @param clause the clause that will be wrapped in a surrounding AND
     *
     * @pre clause !=null
     */
    public void wrapAnd(List<WhereClause> clauses) {
    	wrapInClause(clauses, new AndClause());
    }
    
    /**
     * <p>
     * Evaluates whether wrapping the given clause in a surrounding OR node
     * would make one or more input variables unbound.
     * </p>
     * <p>
     *
     * @return true iff the argument clause can be wrapped in a surrounding OR
     * node and the result has no more unbound variables than the current query.
     * </p>
     * <p>
     * @param clause the clause that would be wrapped in a surrounding OR
     * </p>
     */
    public boolean isWrapInOrValid(WhereClause clause) {
        // your code here
        return false;
    } // end isWrapInOrValid
    
    /**
     * Wraps the given clause in a surrounding OR node.
     * Does nothing if the given clause is the root clause.
     *
     * @param clause the clause that will be wrapped in a surrounding OR
     *
     * @pre clause !=null
     */
    public void wrapOr(List<WhereClause> clauses) {
    	wrapInClause(clauses, new InclusiveOrClause());
    }
    
    private void wrapInClause(List<WhereClause> clauses, WhereClause wrapperClause) {
        try {
            WhereClause parentClause = clauses.get(0).getParent();
            if (parentClause != null) {  // can not wrap top node
                WhereClause newClause = wrapperClause;
                replaceChild(parentClause, clauses.get(0), newClause);

            	for (WhereClause clause: clauses) {
                    removeChild(parentClause, clause);
                    addChild(newClause, clause);
            	}
            }

        } catch (IllegalWhereClauseCompositionException iwcce) {
            System.err.println("Wrap AND failed: " + iwcce.getMessage());
        }
    }
    
    /**
     * <p>
     * Evaluates whether wrapping the given clause in a surrounding NOT node
     * would make one or more input variables unbound.
     * </p>
     * <p>
     *
     * @return true iff the argument clause can be wrapped in a surrounding NOT
     * node and the result has no more unbound variables than the current query.
     * </p>
     * <p>
     * @param clause the clause that would be wrapped in a surrounding NOT
     * </p>
     */
    public boolean isWrapInNotValid(WhereClause clause) {
        // your code here
        return false;
    } // end isWrapInNotValid
    
    /**
     * Wraps the given clause in a surrounding NOT node.
     * Does nothing if the given clause is the root clause.
     *
     * @param clause the clause that will be wrapped in a surrounding NOT
     *
     * @pre clause !=null
     */
    public void wrapNot(List<WhereClause> clauses) {
    	wrapInClause(clauses, new NotClause());
    }
    
    /**
     * Evaluates whether the given clause can be unwrapped from its surrounding
     * node. This is the case if the given clause is the only child of its
     * parent node. The root node cannot be unwrapped.
     *
     * @return true iff the given clause can be unwrapped from its surrounding
     * node
     *
     * @param clause the clause that would be unwrapped from its parent node
     *
     * @pre clause != null
     */
    public boolean canUnwrap(WhereClause clause) {
        WhereClause parent = clause.getParent();
        if (parent == null) {
            return false; // can not remove top node
        }
        WhereClause grandparent = parent.getParent();
        if (grandparent == null) {
            return false; // can not unwrap top node
        }
        return (true);
    }
    
    /**
     * Unwraps the given clause from its surrounding node if it is possible
     * (canUnwrap). Otherwise, does nothing.
     *
     * @param clause The clause that will be unwrapped from its parent node.
     *
     * @pre clause != null
     * @pre canUnwrap(clause)
     */
    public void unwrap(List<WhereClause> clauses) {
    	for (WhereClause clause : clauses) {
    		if (canUnwrap(clause)) {
    	        WhereClause parent = clause.getParent();
    	        WhereClause grandparent = parent.getParent();
    	        if (grandparent.acceptsAdditionalChild()) {
	    	        removeChild(parent, clause);
	    	        try {
						addChild(grandparent, clause);
					} catch (IllegalWhereClauseCompositionException e) {
						e.printStackTrace();
					}
    	        }
    		}
    	}
    }
    
    /**
     * <p>
     * Associates this QueryEditor with a new, empty Query, fit for querying
     * the given table.
     * </p>
     * <p>
     *
     * @param table The Table that the new Query will be able to select items
     * from.
     * </p>
     */
    
    public void createNewQuery() {
        setQuery(new Query());
        setDirty(false);
    }
    
    /* baseTable concept probably not needed
    public void createNewQuery(Table table) {
        setQuery(new Query(table));
        // %$ KVB : keep catalog in synch with query
        setAWCPrototypeCatalog(table.getAWCPrototypeCatalog());
    }
     */
    
    /**
     * <p>
     * Calculates whether the current Query is valid, i.e. whether all
     * parameters are set and all input variables are bound.
     * </p>
     * <p>
     *
     * @return whether the current Query is valid, i.e. whether all parameters
     * are set and all input variables are bound.
     * </p>
     */
    public boolean isValid() {
        return getQuery().isValid();
    }
    
    /**
     * <p>
     * Gets the root clause of the query, wich usually (but not necessarily)
     * is a ComposedWhereClause.
     * </p>
     * <p>
     *
     * @return The root clause of the current query.
     * </p>
     */
    public WhereClause getRootClause() {
        return getQuery().getRootClause();
    }
    
    /**
     * <p>
     * Calculates a collection of clauses that can be added to the given node.
     * If the given clause is atomic, then no clauses can be added and the
     * collection will be empty.
     * </p>
     * <p>
     *
     * @return a collection of clauses that can be added to the given node.
     * </p>
     * <p>
     * @param The clause to which the new clauses could be added.
     * </p>
     */
    public Collection<AtomicWhereClause> getAvailableClauses(WhereClause clause) {
        return clause.getAvailableAtomicClauses(DatabaseManager.getInstance().getAWCCatalog());
    }
    
    // various classes we will want to encode, require specific Persistence Delegates
    private void installPersistenceDelegates(java.beans.XMLEncoder encoder) {
        encoder.setPersistenceDelegate(AndClause.class, new ComposedClausePersistenceDelegate());
        encoder.setPersistenceDelegate(InclusiveOrClause.class, new ComposedClausePersistenceDelegate());
        encoder.setPersistenceDelegate(NotClause.class, new ComposedClausePersistenceDelegate());
        encoder.setPersistenceDelegate(File.class, new FilePersistenceDelegate());
        encoder.setPersistenceDelegate(FromVariable.class, new FromVariablePersistenceDelegate());
        encoder.setPersistenceDelegate(FieldSelection.class, new SelectionPersistenceDelegate());
        encoder.setPersistenceDelegate(OutputSelection.class, new SelectionPersistenceDelegate());
        encoder.setPersistenceDelegate(TableSelection.class, new SelectionPersistenceDelegate());
        encoder.setPersistenceDelegate(AtomicWhereClause.class, new AWCPersistenceDelegate());
        encoder.setPersistenceDelegate(StringConstant.class, new ConstantPersistenceDelegate());
        encoder.setPersistenceDelegate(OperatorConstant.class, new OperatorConstantPersistenceDelegate());
        encoder.setPersistenceDelegate(DoubleConstant.class, new ConstantPersistenceDelegate());
        encoder.setPersistenceDelegate(Boolean.class, new ConstantPersistenceDelegate());
        encoder.setPersistenceDelegate(StartstringConstant.class, new ConstantPersistenceDelegate());
        encoder.setPersistenceDelegate(SubstringConstant.class, new ConstantPersistenceDelegate());
        encoder.setPersistenceDelegate(EndstringConstant.class, new ConstantPersistenceDelegate());
        encoder.setPersistenceDelegate(DateConstant.class, new ConstantPersistenceDelegate());
        encoder.setPersistenceDelegate(SuggestedValues.class, new SuggestedValuesPersistenceDelegate());
        encoder.setPersistenceDelegate(VisualizationClauseList.class, new VisualizationClauseListPersistenceDelegate());
        encoder.setPersistenceDelegate(WhereClauseComposer.class, new WhereClauseComposerPersistenceDelegate());
        encoder.setPersistenceDelegate(SuggestedValuesOption.class, new DefaultPersistenceDelegate());
        encoder.setPersistenceDelegate(Query.class, new QueryPersistenceDelegate());
    }
    
    public void saveSubquery(WhereClause clause, File file) throws java.io.FileNotFoundException {
    	saveObject(clause, file);
    }
    
    private void saveObject(Object object, File file) throws java.io.FileNotFoundException {
//      java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
//      installPersistenceDelegates(encoder);
//      encoder.writeObject(object);
//      encoder.close();
		try {
			ObjectOutputStream objstream = new ObjectOutputStream(new FileOutputStream(file));
	        objstream.writeObject(object);
	        objstream.close();    	
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private Object loadObject(File file) throws java.io.FileNotFoundException {
    	Object object = null;
		try {
	        ObjectInputStream objstream = new ObjectInputStream(new FileInputStream(file));
	        object = objstream.readObject();
	        objstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
//      java.beans.XMLDecoder decoder = new java.beans.XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
//      object = decoder.readObject();
		return object;
    }
    
    public WhereClause loadSubquery(File file) throws java.io.FileNotFoundException {
        WhereClause clause = (WhereClause)loadObject(file);
        getQuery().getUniqueNameContext().assignUniqueNamesToAll(clause);
        return clause;
    }
    
    public void saveXMLQuery(File file) throws java.io.FileNotFoundException {
    	saveObject(query, file);
        setDirty(false);
    }
    
    public void loadXMLQuery(File file) throws java.io.FileNotFoundException {
    	setQuery((Query) loadObject(file));
        setDirty(false);
    }
    
    public void addDirtinessListener(DirtinessListener listener) {
        dirtinessListeners.add(listener);
    }
    
    public void setDirty(boolean dirty) {
        if (this.dirty != dirty) {
            this.dirty = dirty;
            Iterator<DirtinessListener> dLIter = dirtinessListeners.iterator();
            DirtinessEvent de = new DirtinessEvent(this);
            while (dLIter.hasNext()) {
                ((DirtinessListener)dLIter.next()).dirtinessChanged(de);
            }
        }
    }
    
    /**
     * Whether the report format contains unsaved information.
     */
    public boolean isDirty() {
        return dirty;
    }
    
    public void load(File file) throws IOException {
        loadXMLQuery(file);
    }
    
    public void save(File file) throws IOException {
        saveXMLQuery(file);
    }
    
    private void notifySelectionListChangeListeners() {
        Iterator<SelectionListChangeListener> iter = listChangeListeners.iterator();
        while (iter.hasNext()) {
            SelectionListChangeListener listener = (SelectionListChangeListener)iter.next();
            listener.listChanged();
        }
    }
    
    public void addSelectionListChangeListener(SelectionListChangeListener listener) {
        listChangeListeners.add(listener);
    }
}


