package net.sf.regadb.util.settings;

import org.jdom.Element;

public class SequenceDatabaseConfig implements IConfigParser {
	private String path;
	
	public SequenceDatabaseConfig() {
		setDefaults();
	}
	
	@Override
	public String getXmlTag() {
		return "sequence-database";
	}

	@Override
	public void parseXml(RegaDBSettings settings, Element e) {
		Element pathE = e.getChild("path");
		path = pathE.getTextTrim();
	}

	@Override
	public void setDefaults() {
		path = null;
	}

	@Override
	public Element toXml() {
		return null;
	}

	public String getPath() {
		return path;
	}
}
