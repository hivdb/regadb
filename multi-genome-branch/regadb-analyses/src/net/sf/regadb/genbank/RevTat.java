package net.sf.regadb.genbank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.util.http.HttpDownload;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class RevTat {
	public static void main(String [] args) {
        System.setProperty("http.proxyHost", "www-proxy");
        System.setProperty("http.proxyPort", "3128");
        
        ArrayList<Sequence> sequences = new ArrayList<Sequence>();
        
        //TODO
        //comment out to test
        //FastaHelper.handleXNA(sequences, new File("/home/plibin0/projects/genbank/hiv1/splicing/hiv-db.fasta"), true);
        
        Sequence tat1_ref = null;
        Sequence tat2_ref = null;
        try {
			tat1_ref = DNATools.createDNASequence("atggagccagtagatcctagactagagccctggaagcatccaggaagtcagcctaaaactgcttgtaccaattgctattgtaaaaagtgttgctttcattgccaagtttgtttcataacaaaagccttaggcatctcctatggcaggaagaagcggagacagcgacgaagagctcatcagaacagtcagactcatcaagcttctctatcaaagca", "tat1-ref");
			tat2_ref = DNATools.createDNASequence("acccacctcccaaccccgaggggacccgacaggcccgaaggaatagaagaagaaggtggagagagagacagagacagatccattcgattag", "tat2-ref");
		} catch (IllegalSymbolException e) {
			e.printStackTrace();
		}
        
        
        for(Sequence s : sequences) {
        	String [] name = s.getName().split("\\.");
        	System.err.println(name[name.length-1]);
        	String join = handleNCBIEntry(name[name.length-1]);
        	if(join!=null && join.contains("join")) {
        		String join2 = "join(" + getStartStop(tat1_ref, s) + "," + getStartStop(tat2_ref, s)+")"; 
            	if(!join2.equals(join)) {
            		System.err.println("joins differ:" + join + "->" + join2);
            	} else {
            		System.err.println("joins OK:");
            	}
        	}
        }
	}
	
	public static String getStartStop(Sequence ref, Sequence seq) {

		//TODO uncomment to test code
		////     	NtAlignment ntAlign = new NtAlignment();
////    	ScoredAlignment align = ntAlign.ntAlign(seq, ref);
//    	
//    	Sequence refNtAligned = (Sequence)align.getAlignment().symbolListForLabel(ref.getName());
//    	Sequence s_align = (Sequence)align.getAlignment().symbolListForLabel(seq.getName());

		Sequence refNtAligned = null;
		Sequence s_align = null;
		
    	String seqS = s_align.seqString();
    	String refS = refNtAligned.seqString();

    	int start = -1;
    	int stop = -1;
    	
    	for(int i =0; i<refS.length(); i++) {
    		if(start==-1) {
    			if(refS.charAt(i)!='-') {
    				start = i;
    			}
    		}
			if(refS.charAt(i)!='-') {
				stop = i;
			}
    	}

    	//adjust startPos
    	String regionWithGaps = seqS.substring(start, stop+1);
    	for(int i = 0; i<regionWithGaps.length(); i++) {
    		if(regionWithGaps.charAt(i)!='-') {
    			start = start-i;
    			stop = stop-i;
    			break;
    		}
    	}
    	
    	regionWithGaps = seqS.substring(start, stop+1);
    	//adjust stopPos
    	for(int i = 0; i<regionWithGaps.length(); i++) {
    		if(regionWithGaps.charAt(i)=='-') {
    			stop--;
    		}
    	}
    	
    	
    	
    	return (start+1)+".."+(stop+1);
	}
	
	public static String handleNCBIEntry(String ncbiId) {
	       File organism = null;
	        try {
	            organism = File.createTempFile("organism-genbank", ".xml");
	            //System.err.println(organism.getAbsolutePath());
	            efetchGenbankXmlFile(ncbiId, "nucleotide", organism);
	        } catch (IOException e1) {
	            e1.printStackTrace();
	        }
	        
	        SAXBuilder builder = new SAXBuilder();

	        GBOrganism organismGB = new GBOrganism();
	        
	        try {
	            Document doc = builder.build(organism);
	            Element root = doc.getRootElement();
	            
	            Element ref_seq_el = ((Element)root.getChild("GBSeq")).getChild("GBSeq_sequence");
	            String refSeq = ref_seq_el.getValue().trim();
	            
	            List<Element> elList = new ArrayList<Element>();
	            findElementRecursively(root, "CDS", elList);    
	            
	            for(Element e : elList) {
	                String location = null;
	                String note = null;
	                String protein_id = null;
	                String product = null;
	                for(Object o : e.getChildren()) {
	                    Element eEl = (Element)o;
	                    if(eEl.getName().trim().equals("GBFeature_location")) {
	                        location = eEl.getValue().trim();
	                    } else if(eEl.getName().trim().equals("GBFeature_quals")) {
	                        note = getQualsValue(eEl, "gene");
	                        protein_id = getQualsValue(eEl, "protein_id");
	                        product = getQualsValue(eEl, "product");
	                        if(product!=null && product.toLowerCase().contains("tat")) {
	                        	if(location!=null) {
	                        		return location;
	                        	}
	                        }
	                    }
	                }
	            }
	        } catch (JDOMException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        return null;
	}
	
    public static void efetchGenbankXmlFile(String id, String database, File f) {
        String genbankEfetchUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=$database&id=$id&retmode=xml";
        genbankEfetchUrl = genbankEfetchUrl.replace("$database", database);
        genbankEfetchUrl = genbankEfetchUrl.replace("$id", id);
        
        HttpDownload.download(genbankEfetchUrl, f.getAbsolutePath());
    }
    
    private static void findElementRecursively(Element parent, String valueName, List<Element> elementList) {
        for(Object o : parent.getChildren()) {
            Element e = (Element)o;
            if(e.getValue().trim().equals(valueName)) {
                elementList.add(e.getParentElement());
            } else {
                findElementRecursively(e, valueName, elementList);
            }
        }
    }
    
    private static String getQualsValue(Element INSDFeature_quals_value, String name) {
        for(Object o : INSDFeature_quals_value.getChildren("GBQualifier")) {
            Element e = (Element)o;
            String qName = e.getChild("GBQualifier_name").getValue().trim();
            if(qName.equals(name)) {
                String qValue = e.getChild("GBQualifier_value").getValue().trim();
                return qValue;
            }
        }
        
        return null;
    }
}
