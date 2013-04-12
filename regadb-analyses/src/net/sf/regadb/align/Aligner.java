/*
 * Created on Jan 5, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaInsertionId;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaMutationId;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;

public class Aligner {
	private AlignmentService service;

	private final SymbolTokenization aatok;
	private final SymbolTokenization nttok;

	private static Map<OpenReadingFrame,Sequence> referenceSequences = new HashMap<OpenReadingFrame,Sequence>();

	public Aligner(AlignmentService service) {
		this.service = service;
		try {
			aatok = ProteinTools.getTAlphabet().getTokenization("token");
			nttok = DNATools.getDNA().getTokenization("token");
		} catch (BioException e) {
			throw new RuntimeException(e);
		}
	}

	public List<AaSequence> align(NtSequence seq, Genome genome) throws IllegalSymbolException {

		Sequence s = DNATools.createDNASequence(seq.getNucleotides(), "target");

		List<AaSequence> result = new ArrayList<AaSequence>();

		for (OpenReadingFrame orf : genome.getOpenReadingFrames()) {
			System.err.println("Trying: " + orf.getName());

			List<AaSequence> aas = align(s, orf);
			System.err.println(orf.getName() + ": " + (aas != null ? aas.size() : 0) + " proteins");            

			if (aas != null) {
				result.addAll(aas);
			}
		}

		return result;
	}

	private List<AaSequence> align(Sequence s, OpenReadingFrame orf) {
		AlignmentResult r=null;
		r = service.alignTo(s, getReferenceSequence(orf));

		if (r != null)
			return convertToAaSequences(orf, r);
		else
			return null;
	}

	private List<AaSequence> convertToAaSequences(OpenReadingFrame orf,
			AlignmentResult aligned) {
		try {
			List<AaSequence> result = new ArrayList<AaSequence>();

			HashMap<Protein,ProteinSequenceBounds> proteinSequenceBounds = getProteinSequenceBounds(orf, aligned);

			for (Protein protein : proteinSequenceBounds.keySet()) {

				ProteinSequenceBounds psb = proteinSequenceBounds.get(protein);

				AaSequence s = new AaSequence();
				s.setProtein(protein);
				s.setFirstAaPos(psb.start);
				s.setLastAaPos(psb.stop);
				result.add(s);
				
				Set<AaMutation> mutations = s.getAaMutations();
				Set<AaInsertion> insertions = s.getAaInsertions();

				for (Mutation m:aligned.getMutations()) {
					//System.err.println(m);
					if (m.getAaPos() >= psb.startOrf && m.getAaPos() <= psb.stopOrf) {
						if (m.getInsIndex() == -1) {
							AaMutation aam = new AaMutation();
							aam.setId(new AaMutationId((short) posInProtein(protein, m.getAaPos()), s));
							aam.setAaMutation(asString(m.getTargetAminoAcids()));
							aam.setAaReference(aatok.tokenizeSymbol(m.getRefAminoAcid()));
							aam.setNtMutationCodon(asCodonString(nttok, m.getTargetCodon()));
							aam.setNtReferenceCodon(asCodonString(nttok, m.getRefCodon()));

							mutations.add(aam);
						} else if(m.getAaPos() != getLastAa(protein)){
							AaInsertion aai = new AaInsertion();
							aai.setId(new AaInsertionId((short) posInProtein(protein, m.getAaPos()), s, (short) m.getInsIndex()));
							aai.setAaInsertion(asString(m.getTargetAminoAcids()));
							aai.setNtInsertionCodon(asCodonString(nttok, m.getTargetCodon()));

							insertions.add(aai);
						}
					}
				}
			}

			return result;
		} catch (IllegalSymbolException e) {
			throw new RuntimeException(e);
		}
	}

	private class ProteinSequenceBounds {

		short start = 0;
		short stop = 0;
		short startOrf = 0;
		short stopOrf = 0;

	}

	private HashMap<Protein,ProteinSequenceBounds> getProteinSequenceBounds(OpenReadingFrame orf, AlignmentResult aligned){
		HashMap<Protein,ProteinSequenceBounds> proteinSequenceBounds = new HashMap<Protein, ProteinSequenceBounds>();

		List<Protein> sortedProteins = new ArrayList<Protein>();
		sortedProteins.addAll(orf.getProteins());
		Collections.sort(sortedProteins, new Comparator<Protein>(){

			public int compare(Protein o1, Protein o2) {
				return o1.getStartPosition() - o2.getStartPosition();
			}

		});

		short position = 0;
		short nbAASeen = 0;
		short insCorrection = 0;
		boolean first = true;
		for(Protein protein : sortedProteins){
			//check for insertions before the first protein
			//so we start at the true start of the protein
			if(first){
				while(position < getFirstAa(protein)){					
					for(Mutation m : aligned.getMutations()){					
						if(m.getAaPos() == position){
							if(m.getInsIndex() != -1){
								insCorrection++;
							}
						}					
					}					
					position++;
				}
				first = false;
			}
			
			
			if(!coversProtein(aligned, protein)){
				position+=getAaLength(protein);
				continue;
			}
			if(getFirstAa(protein) <= aligned.getFirstAa()){
				position = (short) aligned.getFirstAa();
				nbAASeen = (short) (position - getFirstAa(protein));
			}

			ProteinSequenceBounds psb = new ProteinSequenceBounds();
			while(nbAASeen < getAaLength(protein) && (position + insCorrection) <= aligned.getLastAa()){
				boolean foundMut = false;
				for(Mutation m : aligned.getMutations()){					
					if(m.getAaPos() == position){
						if(m.getInsIndex() != -1){ //insertion
							insCorrection++;
						}else if(m.getTargetCodon().seqString().equals("---")){ //deletion
							foundMut = true;
							nbAASeen++;
						}else{ //substitution
							foundMut = true;
							nbAASeen++;
							if(psb.start == 0){
								psb.start = nbAASeen;
								psb.startOrf = position;
							}
							psb.stop = nbAASeen;
							psb.stopOrf = position;
						}
					}					
				}
				if(!foundMut){ //reference
					nbAASeen++;
					if(psb.start == 0){
						psb.start = nbAASeen;
						psb.startOrf = position;
					}
					psb.stop = nbAASeen;
					psb.stopOrf = position;
				}
				position++;
			}
			if(psb.start == 0 && psb.stop == 0)
				continue;
			proteinSequenceBounds.put(protein, psb);
			nbAASeen = 0;
		}
		return proteinSequenceBounds;
	}

	private boolean coversProtein(AlignmentResult aligned, Protein protein){
		return aligned.getFirstAa() < getLastAa(protein) && (aligned.getLastAa() > getFirstAa(protein));
	}

	private int posInProtein(Protein p, int aa) {
		return aa - p.getStartPosition()/3;
	}

	private int getAaLength(Protein p) {
		return (p.getStopPosition() - p.getStartPosition())/3;
	}

	private String asCodonString(SymbolTokenization st, SymbolList codon) {
		try {
			String result = "";
			for (int i = 1; i <= codon.length(); ++i) {
				result += st.tokenizeSymbol(codon.symbolAt(i));
			}
			return result;
		} catch (IllegalSymbolException e) {
			throw new RuntimeException(e);
		} catch (IndexOutOfBoundsException e) {
			throw new RuntimeException(e);
		}
	}

	private String asString(Set<Symbol> targetAminoAcids) {
		try {
			String result = new String();

			for (Symbol s:targetAminoAcids) {
				result += aatok.tokenizeSymbol(s);
			}

			return result;
		} catch (IllegalSymbolException e) {
			throw new RuntimeException(e);
		}
	}

	private int getFirstAa(Protein p){
		return (int)Math.ceil(p.getStartPosition()/3.0);
	}

	private int getLastAa(Protein p){
		return (int)Math.floor(p.getStopPosition()/3.0);
	}

	private static Sequence getReferenceSequence(OpenReadingFrame orf){
		Sequence s = referenceSequences.get(orf);
		if(s == null){
			try {
				s = DNATools.createDNASequence(orf.getReferenceSequence(),orf.getName());
				referenceSequences.put(orf, s);
			} catch (IllegalSymbolException e) {
				e.printStackTrace();
			}
		}
		return s;
	}
}
