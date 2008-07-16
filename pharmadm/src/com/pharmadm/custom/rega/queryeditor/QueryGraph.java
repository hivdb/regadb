package com.pharmadm.custom.rega.queryeditor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class QueryGraph {
	private HashMap<String, QueryGraphNode> graph;
	
	public QueryGraph(Query query) {
		graph = new HashMap<String, QueryGraphNode>();
		traverseNode(query.getRootClause());
	}
	
	private void traverseNode(WhereClause clause) {
    	Iterator<WhereClause> it = clause.iterateChildren();
    	while (it.hasNext()) {
    		WhereClause child = it.next();
    		if (child.isAtomic()) {
    			List<Join> joins = ((AtomicWhereClause) child).getRelations();
    			for (Join join :  joins) {
    				List<String> str = join.getJoinedVariables();
    				for (String s : str) {
    					addNode(s);
    				}
    				addRelation(str);
    			}
    		}
    		else {
    			traverseNode(child);
    		}
    	}
	}
    	
	private void addNode(String name) {
		if (!graph.containsKey(name)) {
			 graph.put(name, new QueryGraphNode(name));
		}
	}
	
	private void addRelation(List<String> str) {
		if (str.size() == 2) {
			graph.get(str.get(0)).addAssociation(graph.get(str.get(1)));
		}
	}
	
	public boolean isConnected() {
		if (graph.size() == 0) {
			return true;
		}
		return graph.values().iterator().next().traverse(new HashSet<QueryGraphNode>()).size() == graph.size();
	}
    
	
}
