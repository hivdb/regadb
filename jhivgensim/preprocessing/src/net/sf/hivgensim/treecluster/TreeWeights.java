package net.sf.hivgensim.treecluster;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class TreeWeights {
	
	public class DoublePointer{
		double value = 0;
	}

	private double lambda;
		
	private double totalAverageDistance = 0;
	private double totalDistance = 0;
	
	private int treatedCount = 0;
	private double naiveCount = 0;

	public TreeWeights(double lambda){
		this.lambda = lambda;
	}
	
	private double calcWeight(double distance){
	  totalDistance += distance;
	  naiveCount++;
	  return Math.exp(-lambda*distance);
	}
	
	/*
	 * travel the tree, being currently at node and coming from
	 * visited, and add to all leaf nodes node->weight_ the
	 * current node->tmp_ divided by total.
	 */
	private void addWeights(TreeNode node, TreeNode visited, double total){
	  if (node.isLeaf()) {
	    if (node.isTarget()) {	    	
	      node.weight += (node.tmp / total);
	    }
	  } else {
	    for (int i = 0; i < node.getNbOfChildren(); ++i) {
	      TreeNode child = node.getChild(i);

	      if (child != visited) {
	    	  addWeights(child, node, total);
	      }
	    }
	  }

	  if (node.getParent() != null && visited != node.getParent()){
	    addWeights(node.getParent(), node, total);
	  } 
	}

	/*
	 * Travel the tree, visiting node and coming from visited.
	 * with the current node distance away from some originating
	 * node.
	 *
	 * the weight(distance) for every leaf node is recorded in
	 * node->tmp_
	 */
	private void calcWeights(TreeNode node, TreeNode visited, double distance, DoublePointer total){
		if (node.isLeaf()) {
			if (node.isTarget()) {
								node.tmp = calcWeight(distance);
								total.value += node.tmp;
			}
		} else {
			for (int i = 0; i < node.getNbOfChildren(); ++i) {
				TreeNode child = node.getChild(i);
				if (child != visited) {
					calcWeights(child, node, child.getDistance() + distance, total);
				}
			}
		}

		if (node.getParent() != null && visited != node.getParent()){
			calcWeights(node.getParent(), node,	distance + node.getDistance(), total);
		}
	}

	public void calculateWeights(TreeNode node){
		if(node.isLeaf()){
			if(node.isSource()){
				DoublePointer total = new DoublePointer();
				totalDistance = 0;
			    naiveCount = 0;

				calcWeights(node.getParent(), node, node.getDistance(), total);
				addWeights(node.getParent(), node, total.value);

				totalAverageDistance += (totalDistance / naiveCount);
				treatedCount++;
			}			
		}else{
			for(int i = 0; i < node.getNbOfChildren(); i++){
				calculateWeights(node.getChild(i));
			}
		}
	}
		
	public static void main(String[] args){
		try {
			TreeParser tp = new TreeParser("/home/gbehey0/tree.phy");
			TreeNode root = tp.parseTree();
			TreeWeights tw = new TreeWeights(50); // PR:50 RT:100
			tw.calculateWeights(root);
			PrintStream out = new PrintStream(new FileOutputStream("/home/gbehey0/out50.csv"));
			out.println(root.printWeights());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
