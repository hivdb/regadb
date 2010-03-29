package net.sf.phylis;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

/*
 * Created on Apr 19, 2003
 */

/**
 * @author kdf
 */
public class PhylisConfig {
	private boolean aminoAcid;
    private int windowSize;
    private int stepSize;
    private String inputFileName;
    private String mrBayesBlock;
    private String paupBlock;
    private List taxa;     // List of String
    private List clusters; // List of Cluster

    public static class Cluster {
        private String name;
        private int taxaIndexes[];
         
        Cluster(String name, int taxaIndexes[]) {
            this.name = name;
            this.taxaIndexes = taxaIndexes;
            
            Arrays.sort(taxaIndexes);
        }

        public String getName() {
            return name;
        }
        
        public String toString() {
        	return getName();
        }

        public int[] getTaxaIndexes() {
            return taxaIndexes;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTaxaIndexes(int[] taxaIndexes) {
            this.taxaIndexes = taxaIndexes;
        }
    }

    public PhylisConfig(int windowSize, int stepSize, String mrBayesBlock,
                        String inputFileName, List taxa,
                        List clusters, boolean aminoAcid) {
         this.windowSize = windowSize;
         this.stepSize = stepSize;
         this.inputFileName = inputFileName;
         this.mrBayesBlock = mrBayesBlock;
         this.taxa = taxa;
         this.clusters = clusters;
         this.aminoAcid = aminoAcid;
    }

    public PhylisConfig(File fileName) throws IOException
    {
        retrieve(fileName);
    }

    private void retrieve(File fileName) throws IOException {
        SAXBuilder builder = new SAXBuilder();
        try {
            Document document = builder.build(fileName);
            
            Element root = document.getRootElement();

            if (root.getAttribute("alphabet") != null) {
				aminoAcid = root.getAttribute("alphabet").equals("aminoacid");
            } else
            	aminoAcid = false; // default: DNA

            windowSize = root.getAttribute("windowSize").getIntValue();
            stepSize = root.getAttribute("stepSize").getIntValue();
            Element mrBayesElem = root.getChild("mrBayesBlock");
            mrBayesBlock = mrBayesElem.getText();
			Element paupElem = root.getChild("paupBlock");
			paupBlock = paupElem.getText();
            inputFileName = root.getAttributeValue("inputFileName");

            taxa = new ArrayList();
            List taxaElements = root.getChildren("taxus");
            for (Iterator i = taxaElements.iterator(); i.hasNext();) {
                Element taxusElement = (Element) i.next();
                taxa.add(taxusElement.getAttributeValue("name"));
            }
            
            clusters = new ArrayList();
            List clusterElements = root.getChildren("cluster");
            for (Iterator i = clusterElements.iterator(); i.hasNext();) {
                Element clusterElement = (Element) i.next();
                
                int size = clusterElement.getChildren("member").size();
                int[] elements = new int[size];
                for (int j = 0; j < size; ++j) {
                    Element memberElement = (Element) clusterElement.getChildren().get(j);
                    elements[j] = memberElement.getAttribute("idx").getIntValue();
                }

                clusters.add(new Cluster(clusterElement.getAttributeValue("name"),
                             elements));
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        }        
    }

	public int findTaxus(String taxusName) {
		for (int i = 0; i < taxa.size(); ++i) {
			if (taxa.get(i).equals(taxusName))
				return i;
		}
		
		return -1;
	}

    public void save(File fileName) throws IOException {
        Element root = new Element("slidingbayes");
        root.setAttribute("alphabet", aminoAcid ? "aminoacid" : "nucleotide");
        root.setAttribute("windowSize", new Integer(windowSize).toString());
        root.setAttribute("stepSize", new Integer(stepSize).toString());
        Element mrBayesElem = new Element("mrBayesBlock");
        mrBayesElem.setText(mrBayesBlock);
        root.addContent(mrBayesElem);
		Element paupElem = new Element("paupBlock");
		paupElem.setText(paupBlock);
		root.addContent(paupElem);
        root.setAttribute("inputFileName", inputFileName);
        for (Iterator i = taxa.iterator(); i.hasNext();) {
            String taxus = (String) i.next();
            Element taxusElem = new Element("taxus");
            taxusElem.setAttribute("name", taxus);
            root.addContent(taxusElem);
        }
        for (Iterator i = clusters.iterator(); i.hasNext();) {
            Cluster cluster = (Cluster) i.next();
            Element clusterElem = new Element("cluster");
            clusterElem.setAttribute("name", cluster.getName());
            for (int j = 0; j < cluster.getTaxaIndexes().length; ++j) {
                Element memberElem = new Element("member");
                memberElem.setAttribute("idx", new Integer(cluster.getTaxaIndexes()[j]).toString());
                clusterElem.addContent(memberElem);
            }
            
            root.addContent(clusterElem);
        }

        Document document = new Document();
        document.setRootElement(root);

        OutputStream fileStream = new FileOutputStream(fileName);        
        XMLOutputter xml = new XMLOutputter();
        xml.output(document, fileStream);
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public String getMrBayesBlock() {
        return mrBayesBlock;
    }

    public int getStepSize() {
        return stepSize;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setInputFileName(String inputFile) {
        this.inputFileName = inputFile;
    }

    public void setMrBayesBlock(String mrBayesBlock) {
        this.mrBayesBlock = mrBayesBlock;
    }

    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public List getClusters() {
        return clusters;
    }

   public List getTaxa() {
        return taxa;
    }

    public void setClusters(List clusters) {
        this.clusters = clusters;
    }

    public void setTaxa(List taxa) {
        this.taxa = taxa;
    }

	public String getPaupBlock() {
		return paupBlock;
	}

	public void setPaupBlock(String string) {
		paupBlock = string;
	}

	public void reverseTaxa() {
		Collections.reverse(taxa);
		for (int i = 0; i < clusters.size(); ++i) {
			Cluster c = (Cluster) clusters.get(i);
			
			for (int j = 0; j < c.taxaIndexes.length; ++j) {
				c.taxaIndexes[j] = taxa.size() - 1 - c.taxaIndexes[j];
			}
		}
	}

	public boolean isAminoAcid() {
		return aminoAcid;
	}

	public void setAminoAcid(boolean b) {
		aminoAcid = b;
	}

}
