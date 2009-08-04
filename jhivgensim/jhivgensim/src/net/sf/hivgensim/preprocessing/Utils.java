package net.sf.hivgensim.preprocessing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.regadb.db.AaMutInsertion;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;

public class Utils {
	
	public static String getAlignedNtSequenceString(NtSequence ntseq, SelectionWindow sw){
		StringBuilder result = new StringBuilder();
		
		for(AaSequence aaSequence : ntseq.getAaSequences()){
				String sprotein = sw.getProtein().getAbbreviation();
				String sorganism = sw.getProtein().getOpenReadingFrame().getGenome().getOrganismName();
				if(!AaSequenceUtils.coversRegion(aaSequence, sorganism, sprotein)){
					continue;
				}
				
				Iterator<AaMutInsertion> muts = AaMutInsertion.getSortedMutInsertionList(aaSequence).iterator();
				AaMutInsertion mut = muts.hasNext()? muts.next() : null;
				
				String ref = sw.getReferenceNtSequence();
				
				for(int pos = sw.getStart(); pos <= sw.getStop(); pos++){
					if(mut == null){
						result.append(ref.substring(3*(pos-1), 3*(pos-1)+3));
						continue;
					}
					
					if(mut.getPosition() == pos){
						if(!mut.isInsertion() ){
							result.append(mut.getMutation().getNtMutationCodon());
						}else{
							//TODO insertions
						}
					}else{
						result.append(ref.substring(3*(pos-1), 3*(pos-1)+3));
					}
					
					if(mut.getPosition() == pos){
						mut = muts.hasNext()? muts.next() : null;
					}
				}
				break; //TODO only one aasequence in region per ntsequence?
		}
		
		if(sw.getProtein().getAbbreviation().equals("PR")){
			for(int i = 0; i < 560;++i){
				result.append("---");
			}
		}else if(sw.getProtein().getAbbreviation().equals("RT")){
			for(int i = 0; i < 120;++i){
				result.append("---");
			}		
		}else{
			System.err.println("no PR or RT sequences!?!");
		}
		return result.toString();
	}

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
	
	public static OpenReadingFrame getOpenReadingFrame(String organismName, String orfName){
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
		return orf;
	}
	
	public static Sequence getReferenceSequence(String organismName, String orfName){
		Sequence s = null;
		OpenReadingFrame orf = getOpenReadingFrame(organismName, orfName);
		try {
			s = DNATools.createDNASequence(orf.getReferenceSequence(),orfName);
		} catch (IllegalSymbolException e) {
			e.printStackTrace();
		}
		return s;
	}
}
