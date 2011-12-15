package net.sf.regadb.io.db.ghb;

import java.io.File;
import java.io.IOException;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.io.db.util.DelimitedReader;
import net.sf.regadb.tools.FastaFile;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;

/**
 * Compare the sequence files to the sequences stored in RegaDB
 * 
 * @author simbre1
 *
 */

public class CheckSequences {
	
	public CheckSequences(){
		
	}
	
	
	/**
	 * 
	 * @param csv csv file with regadb sequences: sample_id,label,nucleotides
	 * @param dir directory containing the original fasta files
	 */
	public void run(File csv, File dir) throws IOException {
		DelimitedReader dr = new DelimitedReader(csv, ",", "\"");
		
		log("sample id","regadb_label","directory","fasta","fasta_label");
		while(dr.readLine() != null){
//			if(dr.get("sample_id").startsWith("AR08"))
				check(dir, dr.get("sample_id"), dr.get("label"), dr.get("nucleotides"));
		}
		
		dr.close();
	}
	
	public boolean check(File maindir, String sampleid, String label, String nucleotides){
		File dir = findDir(maindir, sampleid);
		
		if(dir != null){
			int offset = maindir.getAbsolutePath().length();
			if(check(dir,
					 offset,
					 sampleid,
					 label,
					 nucleotides.toLowerCase().trim()))
				return true;
			else
				log(sampleid,
					label,
					dir.getAbsolutePath().substring(offset));
		}
		else{
			log(sampleid,label);
		}
		return false;
	}
	
	private boolean check(File dir, int offset, String sampleid, String label, String nucleotides){
		boolean found = false;
		
		for(File f : dir.listFiles()){
			if(f.isFile()){
				String name = f.getName().toLowerCase();
				if(name.endsWith(".fsta") || name.endsWith(".fasta")){
					try{
						FastaFile ff = new FastaFile(f);
						for(int i=0; i<ff.size(); ++i){
							NtSequence seq = ff.get(i);
							String nucs = seq.getNucleotides().toLowerCase().trim();
							if(nucleotides.equals(nucs)){
								log(sampleid,
									label,
									dir.getAbsolutePath().substring(offset),
									f.getAbsolutePath().substring(offset),
									seq.getLabel());
								found = true;
								break;
							}
						}
					}
					catch(IOException e){
						System.err.println(e.getMessage());
					}
				}
			}
			else
				found = check(f, offset, sampleid, label, nucleotides);
			
			if(found)
				break;
		}
		return found;
	}
	
	private void log(String... ss){
		int i = 4;
		for(String s : ss){
			System.out.print(s);
			if(i-- > 0)
				System.out.print(';');
		}
		while(i-- > 0)
			System.out.print(';');
		System.out.println();
	}
	
	public File findDir(File maindir, String sampleid){
		sampleid = sampleid.toLowerCase();
		
		for(File f : maindir.listFiles()){
			if(f.isDirectory()){
				if(f.getName().toLowerCase().equals(sampleid))
					return f;
				else{
					File fc = findDir(f, sampleid);
					if(fc != null)
						return fc;
				}
			}
		}
		
		return null;
	}

	
	public static void main(String[] args) throws IOException{
		Arguments as = new Arguments();
		PositionalArgument csv = as.addPositionalArgument("sequences.csv", true);
		PositionalArgument dir = as.addPositionalArgument("sequences-dir", true);
		
		if(!as.handle(args))
			return;
		
		CheckSequences cs = new CheckSequences();
		cs.run(new File(csv.getValue()), new File(dir.getValue()));
	}
}
