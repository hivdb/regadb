package net.sf.hivgensim.preprocessing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.hivgensim.fastatool.FastaScanner;
import net.sf.hivgensim.fastatool.FastaSequence;
import net.sf.hivgensim.treecluster.TreeNode;
import net.sf.hivgensim.treecluster.TreeParser;
import net.sf.hivgensim.treecluster.TreeTraveller;

public class LongitudinalCleaner {
	
	private ArrayList<FastaSequence> sequences;
	private TreeNode root;
	
	public LongitudinalCleaner(ArrayList<FastaSequence> sequences, TreeNode root){
		this.sequences = sequences;
		this.root = root;
	}
	
	public void check(){
		for(int i = 0; i < sequences.size(); i+=2){
			if(!clusterTogether(sequences.get(i),sequences.get(i+1))){
				System.err.println(sequences.get(i).getId().substring(1)+" and "+sequences.get(i+1).getId().substring(1)+" do not cluster together => removed");
				sequences.remove(i);
				sequences.remove(i+1);
			}
		}
	}
	
	public boolean clusterTogether(FastaSequence seq1, FastaSequence seq2){
		TreeTraveller tt = new TreeTraveller(root);
		while(tt.hasNext()){
			TreeNode next = tt.next();
			if(equals(seq1,next)){
				return equals(seq2,next.getParent().getChild(0)) || equals(seq2,next.getParent().getChild(1)); 
			}
		}
		throw new Error("Could not find corresponding nodes in tree.");
	}
	
	private boolean equals(FastaSequence seq, TreeNode node){
		if(!node.isLeaf()){
			return false;
		}
		return node.getTaxus().equals(seq.getId().substring(1));
	}
	
	public void writeSequencesToFile(String filename) throws IOException{
		FileOutputStream fos = new FileOutputStream(filename);
		for(FastaSequence fs : sequences){
			fos.write(fs.toString().getBytes());
		}
		fos.flush();
		fos.close();
	}	
	
	public static void main(String[] args) throws IOException {
		if(args.length != 3 && args.length != 2){
			System.err.println("check long.fasta tree.phy [long.fasta.clean]");
			System.exit(0);
		}
		
		ArrayList<FastaSequence> sequences = new ArrayList<FastaSequence>(); 
		FastaScanner scanner = new FastaScanner(new File(args[0]));
		while(scanner.hasNextSequence()){
			sequences.add(scanner.nextSequence());
		}
		TreeNode root = new TreeParser(args[1]).parseTree();
		LongitudinalCleaner cl = new LongitudinalCleaner(sequences,root);
		cl.check();
		if(args.length == 3){
			cl.writeSequencesToFile(args[2]);
		}
	}

}
