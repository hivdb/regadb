package com.pharmadm.custom.rega.queryeditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.pharmadm.custom.rega.queryeditor.UniqueNameContext.AssignMode;
import com.pharmadm.custom.rega.queryeditor.port.DatabaseManager;
import com.pharmadm.custom.rega.savable.DirtinessEvent;
import com.pharmadm.custom.rega.savable.DirtinessListener;
import com.pharmadm.custom.rega.savable.Savable;

public class QueryEditor implements Cloneable{
	private Query query;
	private boolean dirty;
	private Savable savable;

	private Collection<DirtinessListener> dirtinessListeners = new ArrayList<DirtinessListener>();
    private Collection<SelectionListChangeListener> listChangeListeners = new ArrayList<SelectionListChangeListener>();

    private final SelectionChangeListener selectionListener = new SelectionChangeListener() {
        public void selectionChanged() {
            setDirty(true);
        }
    };
    
	public QueryEditor(Query query, Savable savable) {
		this.query = query;
		this.savable = savable;
	}
	
    
    public void setDirty(boolean dirty) {
        if (this.dirty != dirty) {
            this.dirty = dirty;
            Iterator<DirtinessListener> dLIter = dirtinessListeners.iterator();
            DirtinessEvent de = new DirtinessEvent(savable);
            while (dLIter.hasNext()) {
                ((DirtinessListener)dLIter.next()).dirtinessChanged(de);
            }
        }
    }

	
    public Query getQuery() {
        return query;
    }
    
    public void setQuery(Query query) {
        if (this.query != null) {
            this.query.getSelectList().removeSelectionChangeListener(selectionListener);
        }
        this.query = query;
        updateSelectionList();
        query.getSelectList().addSelectionChangeListener(selectionListener);
    }
    
    public Savable getSavable() {
    	return savable;
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
    public void addChild(WhereClause parent, WhereClause child, AssignMode mode) throws IllegalWhereClauseCompositionException {
        parent.addChild(child, getQuery().getUniqueNameContext(), mode);
        updateSelectionList();
        setDirty(true);
    }
    
    public void replaceChild(WhereClause parent, WhereClause oldChild, WhereClause newChild) throws IllegalWhereClauseCompositionException  {
        parent.replaceChild(oldChild, newChild, getQuery().getUniqueNameContext());
        updateSelectionList();
        setDirty(true);
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
        updateSelectionList();
        setDirty(true);
    }


    private void updateSelectionList() {
        getQuery().getSelectList().update();
        notifySelectionListChangeListeners();
    }    
    
    /**
     * Whether the report format contains unsaved information.
     */
    public boolean isDirty() {
        return dirty;
    }
    
    public void addDirtinessListener(DirtinessListener listener) {
        dirtinessListeners.add(listener);
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
    
    public Object clone() throws CloneNotSupportedException{
    	QueryEditor editor = new QueryEditor((Query) query.clone(), savable);
    	return editor;
    }
        
}

