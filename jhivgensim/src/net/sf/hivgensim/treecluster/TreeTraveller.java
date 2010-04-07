package net.sf.hivgensim.treecluster;

import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TreeTraveller implements Iterator<TreeNode> {
	
	private int index;
	private TreeTraveller subTraveller;
	private TreeNode root;
	
	public TreeTraveller(TreeNode root){
		this.root = root;
		index = 0;
		if(!root.isLeaf()){
			subTraveller = new TreeTraveller(root.getChild(0)); 
		}
	}

	public boolean hasNext() {
		if(root.isLeaf()){
			return index == 0;
		}
		return subTraveller.hasNext() || index < root.getNbOfChildren()-1;
	}

	public TreeNode next() {
		if(root.isLeaf()){
			index++;
			return root;
		}
		if(subTraveller.hasNext()){
			return subTraveller.next();
		}
		index++;
		subTraveller = new TreeTraveller(root.getChild(index));
		return subTraveller.next();
	}

	public void remove() {
		throw new NotImplementedException();		
	}
	
}
