package net.sf.hivgensim.preprocessing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.Login;

public class Utils {

	public static void createReferenceSequenceFile(Login login, String organismName, String orfName, String filename){
		Transaction t = login.createTransaction();
		Genome genome = null;
		for(Genome g : t.getGenomes()){
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
				out.println(">" + organismName + "." + orf.getName());
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

	public static Protein getProtein(Login login, String organismName, String orfName, String proteinAbbreviation){
		Transaction t = login.createTransaction();

		Genome genome = null;
		for(Genome g : t.getGenomes()){
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
