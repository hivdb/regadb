package net.sf.regadb.util.settings;

import org.jdom.Element;

public interface IConfigParser {
	public String getXmlTag();

	public void setDefaults();
	public void parseXml(RegaDBSettings settings, Element e);
	public Element toXml();
}
