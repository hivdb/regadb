package net.sf.regadb.genbank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ParseCompendium {
	public static void main(String [] args) {
    	SAXBuilder builder = new SAXBuilder();
    	
    	String sequenceToSelect = "B.FR.83.HXB2";
    	
    	int start = 22;
    	
    	int startCol = 26;
    	
        Document doc;
		try {
			List<Page> pages = new ArrayList<Page>();
			
			File dir = new File("/home/plibin0/projects/genbank/compendium/compendium_hiv1_nt/text/rectangle");
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
					System.err.println(fileNumber);
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
								System.err.println(val.substring(26));
								parseStartEndPos(p, val, startCol);
							} else {
								if(val.startsWith(sequenceToSelect)) {
									p.seqs.put(sequenceToSelect, val);
									//System.err.println(val.substring(26));
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
