package com.pharmadm.custom.rega.queryeditor;

import java.util.HashMap;
import java.util.HashSet;

public class QueryGraphNode {
	private String name;
	private HashMap<String, QueryGraphNode> associations;

	public QueryGraphNode(String name) {
		this.name = name;
		associations = new HashMap<String, QueryGraphNode>();
	}
	
	public String getName() {
		return name;
	}
	
	public void addAssociation(QueryGraphNode node) {
		if (!associations.containsKey(node.getName())) {
			associations.put(node.getName(), node);
			node.addAssociation(this);
		}
	}
	
	public HashSet<QueryGraphNode> traverse(HashSet<QueryGraphNode> nodes) {
		if (!nodes.contains(this)) {
			nodes.add(this);
			for (QueryGraphNode node : associations.values()) {
				nodes = node.traverse(nodes);
			}
		}
		return nodes;
	}
	
}
