package net.sf.hivgensim.treecluster;

import java.util.ArrayList;

public class TreeNode {
	
	private String taxus;
	private double distance;
	private TreeNode parent;
	private ArrayList<TreeNode> children;
	
	//TODO vars used by treeweights (refactor?)
	public double weight = 0;
	public double tmp = 0;
	
	public TreeNode(){		
	}
	
	public String getTaxus() {
		return taxus;
	}

	void setTaxus(String taxus) {
		this.taxus = taxus;
	}	
	
	public double getDistance() {
		return distance;
	}

	void setDistance(double distance) {
		this.distance = Math.abs(distance);
	}

	public TreeNode getParent() {
		return parent;
	}

	private void setParent(TreeNode parent) {
		this.parent = parent;
	}

	public void addChild(TreeNode child) {
		if(isLeaf()){
			children = new ArrayList<TreeNode>();
		}
		children.add(child);
		child.setParent(this);
	}
	
	public String toString(){
		String result = getTaxus() + ":" + getDistance() + " " + (getParent() == null? null : getParent().getTaxus()) + "\n";
		if(isLeaf()){
			return result;
		}
		for(TreeNode child : children){
			result += child.toString();			
		}
		return result;
	}
	
	public boolean isSource(){
		return getTaxus().charAt(0) == 'T';
	}
	
	public boolean isTarget(){
		return getTaxus().charAt(0) == 'N';
	}
	
	public boolean isLeaf(){
		return children == null;
	}
	
	public int getNbOfChildren(){
		return children.size();
	}
	
	public TreeNode getChild(int index){
		return children.get(index);
	}

	public String printWeights() {
		if(isLeaf()){
			if(isTarget()){
				return getTaxus().substring(1) + "," + weight + "\n";
			}else{
				return "";
			}
		}else{
			String result = "";
			for(int i = 0; i < getNbOfChildren(); i++){
				result += getChild(i).printWeights();
			}
			return result;
		}
	}
	

}
