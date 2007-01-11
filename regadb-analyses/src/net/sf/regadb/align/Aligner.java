/*
 * Created on Jan 5, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.align;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaInsertionId;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaMutationId;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.genome.Genome;
import net.sf.regadb.genome.HivGenome;
import net.sf.regadb.genome.OpenReadingFrame;
import net.sf.regadb.genome.Protein;

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

    private Map<String, net.sf.regadb.db.Protein> proteins;

    private final SymbolTokenization aatok;
    private final SymbolTokenization nttok;

    public Aligner(AlignmentService service,
            Map<String, net.sf.regadb.db.Protein> proteins) {
        this.service = service;
        this.proteins = proteins;
        try {
            aatok = ProteinTools.getAlphabet().getTokenization("token");
            nttok = DNATools.getDNA().getTokenization("token");
        } catch (BioException e) {
            throw new RuntimeException(e);
        }
    }

    public List<AaSequence> alignHiv(NtSequence seq) throws IllegalSymbolException {

        return align(seq, HivGenome.getHxb2());
    }

    public List<AaSequence> align(NtSequence seq, Genome genome) throws IllegalSymbolException {

        Sequence s = DNATools.createDNASequence(seq.getNucleotides(), "target");

        List<AaSequence> result = new ArrayList<AaSequence>();

        for (String r : genome.getOpenReadingFrames().keySet()) {
            OpenReadingFrame orf = genome.getOpenReadingFrames().get(r);

            List<AaSequence> aas = align(s, orf);
            if (aas != null) {
                for (AaSequence aa:aas) {
                    aa.setNtSequence(seq);
                    seq.getAaSequences().add(aa);
                }

                result.addAll(aas);
            }
        }

        return result;
    }

    private List<AaSequence> align(Sequence s, OpenReadingFrame orf) {
        AlignmentResult r = service.alignTo(s, orf.getSequence());

        if (r != null)
            return convertToAaSequences(orf, r);
        else
            return null;
    }

    private List<AaSequence> convertToAaSequences(OpenReadingFrame orf,
            AlignmentResult aligned) {
        try {
            List<AaSequence> result = new ArrayList<AaSequence>();

            for (String k : orf.getProteins().keySet()) {
                Protein protein = orf.getProtein(k);

                if ((aligned.getFirstAa() < protein.getLastAa())
                        && (aligned.getLastAa() > protein.getFirstAa())) {
                    AaSequence s = new AaSequence();
                    result.add(s);

                    s.setProtein(proteins.get(protein.getName()));
                    s.setFirstAaPos((short) Math.max(1, protein
                            .posInProtein(aligned.getFirstAa())));
                    s.setLastAaPos((short) Math.min(protein.getAaLength(), protein
                            .posInProtein(aligned.getLastAa())));

                    Set<AaMutation> mutations = s.getAaMutations();
                    Set<AaInsertion> insertions = s.getAaInsertions();

                    for (Mutation m:aligned.getMutations()) {
                        System.err.println(m);
                        if (m.getAaPos() >= protein.getFirstAa()
                            && m.getAaPos() <= protein.getLastAa()) {
                            if (m.getInsIndex() == -1) {
                                AaMutation aam = new AaMutation();
                                aam.setId(new AaMutationId((short) protein.posInProtein(m.getAaPos()), s));
                                aam.setAaMutation(asString(m.getTargetAminoAcids()));
                                aam.setAaReference(aatok.tokenizeSymbol(m.getRefAminoAcid()));
                                aam.setNtMutationCodon(asArray(nttok, m.getTargetCodon()));
                                aam.setNtReferenceCodon(asArray(nttok, m.getRefCodon()));
                                
                                mutations.add(aam);
                            } else {
                                AaInsertion aai = new AaInsertion();
                                aai.setId(new AaInsertionId((short) protein.posInProtein(m.getAaPos()), s, (short) m.getInsIndex()));
                                aai.setAaInsertion(asString(m.getTargetAminoAcids()));
                                aai.setNtInsertionCodon(asArray(nttok, m.getTargetCodon()));
                                
                                insertions.add(aai);
                            }
                        }
                    }
                 }
            }

            return result;
        } catch (IllegalSymbolException e) {
            throw new RuntimeException(e);
        }
    }

    private String asArray(SymbolTokenization st, SymbolList codon) {
        try {
            String result = "{";
            for (int i = 1; i <= codon.length(); ++i) {
                if (i != 1)
                    result += ",";
                result += st.tokenizeSymbol(codon.symbolAt(i));
            }
            result += "}";
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
}
