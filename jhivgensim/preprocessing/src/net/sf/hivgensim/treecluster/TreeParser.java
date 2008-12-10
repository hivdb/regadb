package net.sf.hivgensim.treecluster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This classes parses a tree from a file in the newick format.
 * Only single-tree files are supported
 * 
 * @author gbehey0
 *
 */

public class TreeParser {
	
	private String entireTree = "";
	
	public TreeParser(String filename) throws IOException{
		this(new File(filename));
	}
	
	public TreeParser(File file) throws IOException{
		BufferedReader bfr = new BufferedReader(new FileReader(file));
		String line = "";
		while((line = bfr.readLine()) != null){
			entireTree += line.trim();
		}		
	}
	
	public TreeNode parseTree() throws IOException{
		TreeNode root = new TreeNode();
		TreeNode current = root;
				
		for(int i = 0; i < entireTree.length(); i++){
			if(entireTree.charAt(i) == '('){
				//make a new node and go to there
				TreeNode newCurrent  = new TreeNode();
				current.addChild(newCurrent);
				current = newCurrent;				
			}else if(entireTree.charAt(i) == ')'){
				//return to the parent node
				current = current.getParent();
			}else if(entireTree.charAt(i) == ':'){
				//read the distance that comes after the :
				String distance = "";
				char currentChar;
				for(i = i+1; i < entireTree.length(); i++){
					currentChar = entireTree.charAt(i);
					if(currentChar != '(' && currentChar != ')' && 
							currentChar != ':' && currentChar != ',' &&
							currentChar != ';'){
						distance += currentChar; 
					}else{
						break;
					}
				}
				current.setDistance(Double.parseDouble(distance));
				i--; //to go back one character that is a control character
			}else if(entireTree.charAt(i) == ','){
				//create another sibling (child of the parent node) and go to there
				TreeNode newCurrent  = new TreeNode();
				current.getParent().addChild(newCurrent);
				current = newCurrent;
			}else if(entireTree.charAt(i) == ';'){
				//tree is finished, check if root == current
				// if not something went wrong
				if(root != current){
					System.err.println("root != current");
					return null;
				}
				return root;
			}else{
				//all other cases, assume name begins
				//so we read out the name
				String taxus = "";
				char currentChar;
				for(; i < entireTree.length(); i++){
					currentChar = entireTree.charAt(i);
					if(currentChar != '(' && currentChar != ')' && 
							currentChar != ':' && currentChar != ',' &&
							currentChar != ';'){
						taxus += currentChar; 
					}else{
						break;
					}
				}
				current.setTaxus(taxus);
				i--;
			}
		}
		//this should not happen because we return when we meet a semi-colon
		System.err.println("No semi-colon at the end of the tree-file?");
		return null;		
	}
}
