package net.sf.regadb.genbank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ParseCompendium {
	public static void main(String [] args) {
    	SAXBuilder builder = new SAXBuilder();
    	
    	String consensusSequenceToSelect = "MAC.US.-.239";
    	String sequenceToSelect = "H2B.CI.-.EHO";
    	
		FastaRead compareSeq = FastaHelper.readFastaFile(new File("/home/plibin0/projects/genbank/hiv2/U27200_eho.fasta"), false);
    	
    	int start = 4;
    	
    	int startCol = 26;
    	
		List<Page> pages = new ArrayList<Page>();
		
        Document doc;
		try {
			File dir = new File("/home/plibin0/projects/genbank/compendium/compendium_hiv2_nt/text/rectangle");
			File[] files = dir.listFiles();
			Arrays.sort(files, new Comparator<File>() {
				public int compare(File arg0, File arg1) {
					Integer i0 = getFileNumber(arg0);
					Integer i1 = getFileNumber(arg1);
					
					return i0.compareTo(i1);
				}
			});
			for(File f : files) {
				int fileNumber = getFileNumber(f);
				
				if(fileNumber>=start) {
					//System.err.println(fileNumber);
					doc = builder.build(f);
					Element root = doc.getRootElement();
					Element textEl = root.getChild("TEXT");
					List pEls = textEl.getChildren("p");
					for(Object o : pEls) {
						if(o instanceof Element) {
							Element e = (Element)o;
							String val = e.getValue();
							Page p = new Page();
							pages.add(p);
							if(val.startsWith(" ")) {
								//System.err.println(val.substring(26));
								//parseStartEndPos(p, val, startCol);
							} else {
								if(val.startsWith(consensusSequenceToSelect)) {
									p.seqs.put(consensusSequenceToSelect, val);
									//System.err.println(val.substring(26));
								} else if(val.startsWith(sequenceToSelect)) {
									p.seqs.put(sequenceToSelect, val);
								}
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
		
		String consensus = "";
		String sequence = "";
		for(Page p : pages) {
			for(Map.Entry<String, String> e : p.seqs.entrySet()) {
				String seq = e.getValue();
				seq = seq.substring(seq.indexOf(' '), seq.lastIndexOf(' ')).trim();
				if(consensusSequenceToSelect.equals(e.getKey())) {
					consensus += seq;
				} else if(sequenceToSelect.equals(e.getKey())) {
					sequence += seq;
				}
			}
		}
		
		System.err.println(consensus.length() + "-" + sequence.length());
		
		consensus = consensus.replace("\n", "");
		sequence = sequence.replace("\n", "");
		
		String sequenceComplete = "";
		
		for(int i = 0; i<consensus.length(); i++) {
			if(sequence.charAt(i)=='-') {
				sequenceComplete += consensus.charAt(i);
			} else {
				sequenceComplete += sequence.charAt(i);
			}
		}
		
		sequenceComplete = sequenceComplete.replaceAll("\\.", "-");
		System.err.println(sequenceComplete.toUpperCase());
		
		sequenceComplete = sequenceComplete.replaceAll("\\-", "");
		
		System.err.println("complete seq: " + sequenceComplete.toLowerCase());
		System.err.println("compare  seq: " + compareSeq.xna_);
		System.err.println(sequenceComplete.toLowerCase().equals(compareSeq.xna_));
	}
	
	public static void parseStartEndPos(Page p, String line, int startCol) {
		if(line.length()>=startCol) {
			String actualContent = line.substring(startCol);
			int index = actualContent.indexOf('/');
			while (index!=-1) {
				System.err.println(index);
				index = actualContent.indexOf(index,'/');
			}
		}
	}
	
	public static int getFileNumber(File f) {
		String fName = f.getName();
		
		return Integer.parseInt(fName.split("_")[3]);
	}
}
