package net.sf.hivgensim.preprocessing;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.regadb.db.AaMutInsertion;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;

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

	public static String getReferenceSequence(String organismName, String orfName){
		return getOpenReadingFrame(organismName, orfName).getReferenceSequence();
	}

	public static Set<String> getAllMutations(NtSequence seq, SelectionWindow[] windows){
		Set<String> allMutations = new TreeSet<String>();
		for(AaSequence aaSequence : seq.getAaSequences()){
			for(SelectionWindow win : windows){
				String ref = win.getReferenceAaSequence();
				String sprotein = win.getProtein().getAbbreviation();
				String sorganism = win.getProtein().getOpenReadingFrame().getGenome().getOrganismName();
				if(!AaSequenceUtils.coversRegion(aaSequence, sorganism, sprotein)){
					continue;
				}
				Iterator<AaMutInsertion> muts = AaMutInsertion.getSortedMutInsertionList(aaSequence).iterator();
				AaMutInsertion mut = muts.hasNext()? muts.next() : null;

				for(int pos = win.getStart(); pos <= win.getStop(); pos++){
					if(mut == null){
						break;
					}
					if(mut.getPosition() == pos){
						if(!mut.isInsertion() ){
							for(char m : mut.getAaMutationString().toCharArray()){
								allMutations.add(win.getProtein().getAbbreviation() + pos + m);
							}
							if(mut.getAaMutationString().toCharArray().length == 0){
								allMutations.add(win.getProtein().getAbbreviation() + pos + "del");
							}
						}else{
							for(char m : mut.getInsertion().getAaInsertion().toCharArray()){
								allMutations.add(win.getProtein().getAbbreviation() + pos + m + "ins");
							}
						}
						mut = muts.hasNext()? muts.next() : null;
					}else{
						//reference
						allMutations.add(win.getProtein().getAbbreviation() + pos + ref.charAt(pos-win.getStart()));
					}

				}
			}			
		}
		return allMutations;
	}

	public static Set<String> getAllMutations(SelectionWindow[] windows){
		Set<String> mutations = new TreeSet<String>();
		for(SelectionWindow win : windows){
			for(int pos = win.getStart(); pos <= win.getStop(); pos++){
				mutations.add(win.getProtein().getAbbreviation()+pos+"A");
				mutations.add(win.getProtein().getAbbreviation()+pos+"C");
				mutations.add(win.getProtein().getAbbreviation()+pos+"D");
				mutations.add(win.getProtein().getAbbreviation()+pos+"E");
				mutations.add(win.getProtein().getAbbreviation()+pos+"F");
				mutations.add(win.getProtein().getAbbreviation()+pos+"G");
				mutations.add(win.getProtein().getAbbreviation()+pos+"H");
				mutations.add(win.getProtein().getAbbreviation()+pos+"I");
				mutations.add(win.getProtein().getAbbreviation()+pos+"K");
				mutations.add(win.getProtein().getAbbreviation()+pos+"L");
				mutations.add(win.getProtein().getAbbreviation()+pos+"M");
				mutations.add(win.getProtein().getAbbreviation()+pos+"N");
				mutations.add(win.getProtein().getAbbreviation()+pos+"P");
				mutations.add(win.getProtein().getAbbreviation()+pos+"Q");
				mutations.add(win.getProtein().getAbbreviation()+pos+"R");
				mutations.add(win.getProtein().getAbbreviation()+pos+"S");
				mutations.add(win.getProtein().getAbbreviation()+pos+"T");
				mutations.add(win.getProtein().getAbbreviation()+pos+"V");
				mutations.add(win.getProtein().getAbbreviation()+pos+"W");
				mutations.add(win.getProtein().getAbbreviation()+pos+"Y");
				mutations.add(win.getProtein().getAbbreviation()+pos+"ins");
				mutations.add(win.getProtein().getAbbreviation()+pos+"del");
			}
		}
		return mutations;
	}
}