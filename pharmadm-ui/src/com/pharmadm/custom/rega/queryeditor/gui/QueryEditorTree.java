
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

import java.beans.DefaultPersistenceDelegate;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.DefaultTreeModel;

import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.constant.*;
import com.pharmadm.custom.rega.queryeditor.persist.*;
import com.pharmadm.custom.rega.savable.DirtinessListener;
import com.pharmadm.custom.rega.savable.Savable;
import com.thoughtworks.xstream.XStream;

/**
 * The controller ('Controller' pattern) for editing a Query.
 *
 */
public class QueryEditorTree extends DefaultTreeModel implements Savable, QueryEditorComponent {
    
	private QueryEditor editor;
	
    public QueryEditorTree(Query query) {
        super(new WhereClauseTreeNode(query.getRootClause()), true);
        this.editor = new QueryEditor(query, this);
    }
    
    public QueryEditor getEditor() {
    	return editor;
    }
    
    private void setQuery(Query query) {
        setRoot(new WhereClauseTreeNode(query.getRootClause()));
        Object[] rootPath = {getRoot()};
        fireTreeStructureChanged(this, rootPath, null, null);
        getEditor().setQuery(query);
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
        WhereClauseTreeNode parentNode = getNode(parent);
        insertNodeInto(new WhereClauseTreeNode(child), parentNode, parentNode.getChildCount());
        getEditor().addChild(parent, child);
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
        WhereClauseTreeNode childNode = getNode(child);
        if (childNode != null) {
        	removeNodeFromParent(childNode);
        }
        getEditor().removeChild(parent, child);
    }
    
    public void replaceChild(WhereClause parent, WhereClause oldChild, WhereClause newChild) throws IllegalWhereClauseCompositionException  {
        WhereClauseTreeNode parentNode = getNode(parent);
        WhereClauseTreeNode childNode = getNode(oldChild);
        int i = getIndexOfChild(parentNode, childNode);
        removeNodeFromParent(childNode);
        insertNodeInto(new WhereClauseTreeNode(newChild), parentNode, i);
        getEditor().replaceChild(parent, oldChild, newChild);
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
        getEditor().setDirty(false);
    }
    
    /* baseTable concept probably not needed
    public void createNewQuery(Table table) {
        setQuery(new Query(table));
        // %$ KVB : keep catalog in synch with query
        setAWCPrototypeCatalog(table.getAWCPrototypeCatalog());
    }
     */
    
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
//    	/* FREEK METHOD */
//		java.beans.XMLEncoder encoder = new java.beans.XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
//		installPersistenceDelegates(encoder);
//		encoder.writeObject(object);
//		encoder.close();
    	
//    	/* VIRO METHOD */
//		try {
//			ObjectOutputStream objstream = new ObjectOutputStream(new FileOutputStream(file));
//	        objstream.writeObject(object);
//	        objstream.close();    	
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    	
//    	/* DIET METHOD */
    	PrintStream ps = new PrintStream(new FileOutputStream(file));
    	ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		ps.print(new XStream().toXML(object));
		ps.close();
    }
    
    private Object loadObject(File file) throws java.io.FileNotFoundException {
    	Object object = null;
    	
//    	/* FREEK METHODE */
//		try {
//	        ObjectInputStream objstream = new ObjectInputStream(new FileInputStream(file));
//	        object = objstream.readObject();
//	        objstream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
    	
//    	/* VIRO METHODE */
//    	java.beans.XMLDecoder decoder = new java.beans.XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
//    	object = decoder.readObject();
		
//    	/* DIET METHODE */
    	XStream xs = new XStream();
    	object = xs.fromXML(new FileInputStream(file));
    	
    	return object;
    }
    
    public WhereClause loadSubquery(File file) throws java.io.FileNotFoundException {
        WhereClause clause = (WhereClause)loadObject(file);
        getEditor().getQuery().getUniqueNameContext().assignUniqueNamesToAll(clause);
        return clause;
    }
    
    public void saveXMLQuery(File file) throws java.io.FileNotFoundException {
    	saveObject(getEditor().getQuery(), file);
        getEditor().setDirty(false);
    }
    
    public void loadXMLQuery(File file) throws java.io.FileNotFoundException {
    	setQuery((Query) loadObject(file));
        getEditor().setDirty(false);
    }	 
    
    public void load(File file) throws IOException {
        loadXMLQuery(file);
    }
    
    public void save(File file) throws IOException {
        saveXMLQuery(file);
    }

	public void addDirtinessListener(DirtinessListener listener) {
		getEditor().addDirtinessListener(listener);
	}

	public boolean isDirty() {
		return getEditor().isDirty();
	}
}


