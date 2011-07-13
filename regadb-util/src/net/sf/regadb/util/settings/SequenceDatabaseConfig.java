package net.sf.regadb.util.settings;

import org.jdom.Element;

public class SequenceDatabaseConfig extends ConfigParser {
	private String path;
	private double minimumSimilarity;

	public SequenceDatabaseConfig() {
		super("sequence-database");
	}
	
	@Override
	public void parseXml(RegaDBSettings settings, Element e) {
		Element pathE = e.getChild("path");
		path = pathE.getTextTrim();
		Element similarityE = e.getChild("similarity-threshold");
		minimumSimilarity = Double.parseDouble(similarityE.getTextTrim());
	}

	@Override
	public void setDefaults() {
		path = null;
		minimumSimilarity = 0.99;
	}

	@Override
	public Element toXml() {
		return null;
	}

	public String getPath() {
		return path;
	}
	
	public double getMinimumSimilarity() {
		return minimumSimilarity;
	}
}
