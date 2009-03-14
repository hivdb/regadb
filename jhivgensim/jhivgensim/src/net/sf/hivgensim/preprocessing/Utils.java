package net.sf.hivgensim.preprocessing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;

public class Utils {

	public static void createReferenceSequenceFile(String organismName, String orfName, String filename){
		Genome genome = null;
		for(Genome g : net.sf.regadb.service.wts.util.Utils.getGenomes()){
			if(g.getOrganismName().equals(organismName)){
				genome = g;
				break;
			}				
		}
		OpenReadingFrame orf = null;
		if(genome != null){
			for(OpenReadingFrame o : genome.getOpenReadingFrames()){
				if(o.getName().equals(orfName)){
					orf = o;
					break;
				}
			}
		}
		if(orf != null){
			//create file
			try {
				PrintStream out = new PrintStream(new FileOutputStream(filename));
				out.println(">" + organismName + "." + orf.getName() + "REF SEQ");
				out.println(orf.getReferenceSequence());
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			//orf not found in db
			throw new NullPointerException();
		}		
	}

	public static Protein getProtein(String organismName, String orfName, String proteinAbbreviation){
		Genome genome = null;
		for(Genome g : net.sf.regadb.service.wts.util.Utils.getGenomes()){
			if(g.getOrganismName().equals(organismName)){
				genome = g;
				break;
			}				
		}
		if(genome == null){
			return null;
		}

		OpenReadingFrame orf = null;
		for(OpenReadingFrame o : genome.getOpenReadingFrames()){
			if(o.getName().equals(orfName)){
				orf = o;
				break;
			}
		}
		if(orf == null){
			return null;
		}

		for(Protein p : orf.getProteins()){
			if(p.getAbbreviation().equals(proteinAbbreviation)){
				return p;
			}
		}
		return null;
	}
}