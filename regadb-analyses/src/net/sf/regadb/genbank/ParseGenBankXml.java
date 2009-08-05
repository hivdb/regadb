package net.sf.regadb.genbank;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.regadb.util.http.HttpDownload;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class ParseGenBankXml {    
    public static GBOrganism parseOrganism(String genbankId) {
        File organism = null;
        try {
            organism = File.createTempFile("organism-genbank", ".xml");
            //System.err.println(organism.getAbsolutePath());
            efetchGenbankXmlFile(genbankId, "nucleotide", organism);
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
                for(Object o : e.getChildren()) {
                    Element eEl = (Element)o;
                    if(eEl.getName().trim().equals("GBFeature_location")) {
                        location = eEl.getValue().trim();
                    } else if(eEl.getName().trim().equals("GBFeature_quals")) {
                        note = getQualsValue(eEl, "gene");
                        protein_id = getQualsValue(eEl, "protein_id");
                    }
                }
                
                organismGB.orfs.addAll(getOrfs(refSeq, location, note, protein_id));
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return organismGB;
    }
    
    public static List<GBORF> getOrfs(String genome, String location, String name, String protein_id) {
        List<GBORF> orfs = new ArrayList<GBORF>();
        
        List<String> locations = new ArrayList<String>();
        if(location.startsWith("join(")) {
            StringBuffer sb = new StringBuffer(location);
            sb.delete(0, "join(".length());
            sb.deleteCharAt(sb.length()-1);
            StringTokenizer st = new StringTokenizer(sb.toString(), ",");
            while(st.hasMoreTokens()) {
                locations.add(st.nextToken());
            }
        } else {
            locations.add(location);
        }
        
        for(int i = 0; i<locations.size(); i++) {
            String [] positions = locations.get(i).split("\\.\\.");
            for(int j = 0; j<positions.length; j++) {
            	positions[j] = positions[j].replace(">", "");
            	positions[j] = positions[j].replace("<", "");
            }
            GBORF orf = new GBORF();
            orf.name = name + " ORF " + (i+1);
            orf.sequence = genome.substring(Integer.parseInt(positions[0])-1, Integer.parseInt(positions[1]));
            orfs.add(orf);
        }
        
        return orfs;
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
    
    public static void efetchGenbankXmlFile(String id, String database, File f) {
        String genbankEfetchUrl = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=$database&id=$id&retmode=xml";
        genbankEfetchUrl = genbankEfetchUrl.replace("$database", database);
        genbankEfetchUrl = genbankEfetchUrl.replace("$id", id);
        
        boolean done = HttpDownload.download(genbankEfetchUrl, f.getAbsolutePath());
        while(!done) {
        	try {
				Thread.sleep(1000*5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.err.println("retrying");
        	done = HttpDownload.download(genbankEfetchUrl, f.getAbsolutePath());
        }
    }
}
