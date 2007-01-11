/*
 * Created on Jan 5, 2007
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package net.sf.regadb.genome;

import java.util.HashMap;
import java.util.Map;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;

public class HivGenome implements Genome {
    private static HivGenome instance = new HivGenome();

    private OpenReadingFrame pol;

    private OpenReadingFrame gp160;

    private HivGenome() {
        try {
            Sequence polSeq = DNATools.createDNASequence(
                                    "CTACAAGGGAAGGCCAGGGAATTTTCTTCAGAGCAGACCAGAGCCAACAGCCCCACCAGAAGAGAGCTTC"
                                    + "AGGTCTGGGGTAGAGACAACAACTCCCCCTCAGAAGCAGGAGCCGATAGACAAGGAACTGTATCCTTTAA"
                                    + "CTTCCCTCAGGTCACTCTTTGGCAACGACCCCTCGTCACAATAAAGATAGGGGGGCAACTAAAGGAAGCT"
                                    + "CTATTAGATACAGGAGCAGATGATACAGTATTAGAAGAAATGAGTTTGCCAGGAAGATGGAAACCAAAAA"
                                    + "TGATAGGGGGAATTGGAGGTTTTATCAAAGTAAGACAGTATGATCAGATACTCATAGAAATCTGTGGACA"
                                    + "TAAAGCTATAGGTACAGTATTAGTAGGACCTACACCTGTCAACATAATTGGAAGAAATCTGTTGACTCAG"
                                    + "ATTGGTTGCACTTTAAATTTTCCCATTAGCCCTATTGAGACTGTACCAGTAAAATTAAAGCCAGGAATGG"
                                    + "ATGGCCCAAAAGTTAAACAATGGCCATTGACAGAAGAAAAAATAAAAGCATTAGTAGAAATTTGTACAGA"
                                    + "GATGGAAAAGGAAGGGAAAATTTCAAAAATTGGGCCTGAAAATCCATACAATACTCCAGTATTTGCCATA"
                                    + "AAGAAAAAAGACAGTACTAAATGGAGAAAATTAGTAGATTTCAGAGAACTTAATAAGAGAACTCAAGACT"
                                    + "TCTGGGAAGTTCAATTAGGAATACCACATCCCGCAGGGTTAAAAAAGAAAAAATCAGTAACAGTACTGGA"
                                    + "TGTGGGTGATGCATATTTTTCAGTTCCCTTAGATGAAGACTTCAGGAAGTATACTGCATTTACCATACCT"
                                    + "AGTATAAACAATGAGACACCAGGGATTAGATATCAGTACAATGTGCTTCCACAGGGATGGAAAGGATCAC"
                                    + "CAGCAATATTCCAAAGTAGCATGACAAAAATCTTAGAGCCTTTTAGAAAACAAAATCCAGACATAGTTAT"
                                    + "CTATCAATACATGGATGATTTGTATGTAGGATCTGACTTAGAAATAGGGCAGCATAGAACAAAAATAGAG"
                                    + "GAGCTGAGACAACATCTGTTGAGGTGGGGACTTACCACACCAGACAAAAAACATCAGAAAGAACCTCCAT"
                                    + "TCCTTTGGATGGGTTATGAACTCCATCCTGATAAATGGACAGTACAGCCTATAGTGCTGCCAGAAAAAGA"
                                    + "CAGCTGGACTGTCAATGACATACAGAAGTTAGTGGGGAAATTGAATTGGGCAAGTCAGATTTACCCAGGG"
                                    + "ATTAAAGTAAGGCAATTATGTAAACTCCTTAGAGGAACCAAAGCACTAACAGAAGTAATACCACTAACAG"
                                    + "AAGAAGCAGAGCTAGAACTGGCAGAAAACAGAGAGATTCTAAAAGAACCAGTACATGGAGTGTATTATGA"
                                    + "CCCATCAAAAGACTTAATAGCAGAAATACAGAAGCAGGGGCAAGGCCAATGGACATATCAAATTTATCAA"
                                    + "GAGCCATTTAAAAATCTGAAAACAGGAAAATATGCAAGAATGAGGGGTGCCCACACTAATGATGTAAAAC"
                                    + "AATTAACAGAGGCAGTGCAAAAAATAACCACAGAAAGCATAGTAATATGGGGAAAGACTCCTAAATTTAA"
                                    + "ACTGCCCATACAAAAGGAAACATGGGAAACATGGTGGACAGAGTATTGGCAAGCCACCTGGATTCCTGAG"
                                    + "TGGGAGTTTGTTAATACCCCTCCCTTAGTGAAATTATGGTACCAGTTAGAGAAAGAACCCATAGTAGGAG"
                                    + "CAGAAACCTTCTATGTAGATGGGGCAGCTAACAGGGAGACTAAATTAGGAAAAGCAGGATATGTTACTAA"
                                    + "TAGAGGAAGACAAAAAGTTGTCACCCTAACTGACACAACAAATCAGAAGACTGAGTTACAAGCAATTTAT"
                                    + "CTAGCTTTGCAGGATTCGGGATTAGAAGTAAACATAGTAACAGACTCACAATATGCATTAGGAATCATTC"
                                    + "AAGCACAACCAGATCAAAGTGAATCAGAGTTAGTCAATCAAATAATAGAGCAGTTAATAAAAAAGGAAAA"
                                    + "GGTCTATCTGGCATGGGTACCAGCACACAAAGGAATTGGAGGAAATGAACAAGTAGATAAATTAGTCAGT"
                                    + "GCTGGAATCAGGAAAGTACTATTTTTAGATGGAATAGATAAGGCCCAAGATGAACATGAGAAATATCACA"
                                    + "GTAATTGGAGAGCAATGGCTAGTGATTTTAACCTGCCACCTGTAGTAGCAAAAGAAATAGTAGCCAGCTG"
                                    + "TGATAAATGTCAGCTAAAAGGAGAAGCCATGCATGGACAAGTAGACTGTAGTCCAGGAATATGGCAACTA"
                                    + "GATTGTACACATTTAGAAGGAAAAGTTATCCTGGTAGCAGTTCATGTAGCCAGTGGATATATAGAAGCAG"
                                    + "AAGTTATTCCAGCAGAAACAGGGCAGGAAACAGCATATTTTCTTTTAAAATTAGCAGGAAGATGGCCAGT"
                                    + "AAAAACAATACATACTGACAATGGCAGCAATTTCACCGGTGCTACGGTTAGGGCCGCCTGTTGGTGGGCG"
                                    + "GGAATCAAGCAGGAATTTGGAATTCCCTACAATCCCCAAAGTCAAGGAGTAGTAGAATCTATGAATAAAG"
                                    + "AATTAAAGAAAATTATAGGACAGGTAAGAGATCAGGCTGAACATCTTAAGACAGCAGTACAAATGGCAGT"
                                    + "ATTCATCCACAATTTTAAAAGAAAAGGGGGGATTGGGGGGTACAGTGCAGGGGAAAGAATAGTAGACATA"
                                    + "ATAGCAACAGACATACAAACTAAAGAATTACAAAAACAAATTACAAAAATTCAAAATTTTCGGGTTTATT"
                                    + "ACAGGGACAGCAGAAATCCACTTTGGAAAGGACCAGCAAAGCTCCTCTGGAAAGGTGAAGGGGCAGTAGT"
                                    + "AATACAAGATAATAGTGACATAAAAGTAGTGCCAAGAAGAAAAGCAAAGATCATTAGGGATTATGGAAAA"
                                    + "CAGATGGCAGGTGATGATTGTGTGGCAAGTAGACAGGATGAGGAT", "pol");

            Map<String, Protein> proteins = new HashMap<String, Protein>();
            proteins.put("PRO", new Protein("p6", "Transframe peptide (partially)", 1, 48));
            proteins.put("PRO", new Protein("PRO", "Protease", 48 + 1, 48 + 99));
            proteins.put("RT", new Protein("RT", "Reverse Transcriptase", 48 + 100, 48 + 659));
            proteins.put("IN", new Protein("IN", "Integrase", 48 + 660, 48 + 947));

            pol = new OpenReadingFrame(this, "pol", polSeq, proteins);

            Sequence gp160Seq = DNATools
                    .createDNASequence(
                            "atgagagtgaaggagaaatatcagcacttgtggagatgggggtggagatggggcaccatgctccttgggatgttgatgatctgt"
                            + "agtgctacagaaaaattgtgggtcacagtctattatggggtacctgtgtggaaggaagcaaccaccactctattttgtgcatcagatgctaaagcatatgatacagaggtacataatgtttgggccacacatgcctgtgtacccacagaccccaacccacaagaagtagtattggtaaatgtgacagaaaattttaacatgtggaaaaatgacatggtagaacagatgcatgaggatataatcagtttatgggatcaaagcctaaagccatgtgtaaaattaaccccactctgtgttagtttaaagtgcactgatttgaagaatgatactaataccaatagtagtagcgggagaatgataatggagaaaggagagataaaaaactgctctttcaatatcagcacaagcataagaggtaaggtgcagaaagaatatgcatttttttataaacttgatataataccaatagataatgatactaccagctataagttgacaagttgtaacacctcagtcattacacaggcctgtccaaaggtatcctttgagccaattcccatacattattgtgccccggctggttttgcgattctaaaatgtaataataagacgttcaatggaacaggaccatgtacaaatgtcagcacagtacaatgtacacatggaattaggccagtagtatcaactcaactgctgttaaatggcagtctagcagaagaagaggtagtaattagatctgtcaatttcacggacaatgctaaaaccataatagtacagctgaacacatctgtagaaattaattgtacaagacccaacaacaatacaagaaaaagaatccgtatccagagaggaccagggagagcatttgttacaataggaaaaataggaaatatgagacaagcacattgtaacattagtagagcaaaatggaataacactttaaaacagatagctagcaaattaagagaacaatttggaaataataaaacaataatctttaagcaatcctcaggaggggacccagaaattgtaacgcacagttttaattgtggaggggaatttttctactgtaattcaacacaactgtttaatagtacttggtttaatagtacttggagtactgaagggtcaaataacactgaaggaagtgacacaatcaccctcccatgcagaataaaacaaattataaacatgtggcagaaagtaggaaaagcaatgtatgcccctcccatcagtggacaaattagatgttcatcaaatattacagggctgctattaacaagagatggtggtaatagcaacaatgagtccgagatcttcagacctggaggaggagatatgagggacaattggagaagtgaattatataaatataaagtagtaaaaattgaaccattaggagtagcacccaccaaggcaaagagaagagtggtgcagagagaaaaaaga"
                            + "gcagtgggaataggagctttgttccttgggttcttgggagcagcaggaagcactatgggcgcagcctcaatgacgctgacggtacaggccagacaattattgtctggtatagtgcagcagcagaacaatttgctgagggctattgaggcgcaacagcatctgttgcaactcacagtctggggcatcaagcagctccaggcaagaatcctggctgtggaaagatacctaaaggatcaacagctcctggggatttggggttgctctggaaaactcatttgcaccactgctgtgccttggaatgctagttggagtaataaatctctggaacagatttggaatcacacgacctggatggagtgggacagagaaattaacaattacacaagcttaatacactccttaattgaagaatcgcaaaaccagcaagaaaagaatgaacaagaattattggaattagataaatgggcaagtttgtggaattggtttaacataacaaattggctgtggtatataaaattattcataatgatagtaggaggcttggtaggtttaagaatagtttttgctgtactttctatagtgaatagagttaggcagggatattcaccattatcgtttcagacccacctcccaaccccgaggggacccgacaggcccgaaggaatagaagaagaaggtggagagagagacagagacagatccattcgattagtgaacggatccttggcacttatctgggacgatctgcggagcctgtgcctcttcagctaccaccgcttgagagacttactcttgattgtaacgaggattgtggaacttctgggacgcagggggtgggaagccctcaaatattggtggaatctcctacagtattggagtcaggaactaaagaatagtgctgttagcttgctcaatgccacagccatagcagtagctgaggggacagatagggttatagaagtagtacaaggagcttgtagagctattcgccacatacctagaagaataagacagggcttggaaaggattttgcta",
                            "env");

            proteins = new HashMap<String, Protein>();
            proteins.put("sig", new Protein("sig", "Signal peptide", 1, 28));
            proteins.put("gp120", new Protein("gp120", "Envelope surface glycoprotein gp120", 29, 511));
            proteins.put("gp41", new Protein("gp41", "Envelope transmembrane domain", 512, 856));
        
            gp160 = new OpenReadingFrame(this, "gp160", gp160Seq, proteins);
            
        } catch (IllegalSymbolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static HivGenome getHxb2() {
        return instance;
    }

    public OpenReadingFrame getGp160() {
        return gp160;
    }

    public OpenReadingFrame getPol() {
        return pol;
    }

    public Map<String, OpenReadingFrame> getOpenReadingFrames() {
        Map<String, OpenReadingFrame> result = new HashMap<String, OpenReadingFrame>();
        
        result.put("gp160", gp160);
        result.put("pol", pol);

        return result;
    }
}
