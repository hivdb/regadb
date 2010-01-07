package net.sf.regadb.util.settings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom.Element;

public class EmailConfig implements IConfigParser {
	private String host;
	private String from;
	private Set<String> to = new HashSet<String>();
	
	public String getXmlTag() {
		return "e-mail";
	}

	@Override
	public void parseXml(RegaDBSettings settings, Element e) {
		host = e.getChildText("host").trim();
		from = e.getChild("from").getAttributeValue("address").trim();
		to.clear();
		for (Object o : e.getChild("to").getChildren("recipient")) {
			to.add(((Element)o).getAttributeValue("address").trim());
		}
	}

	public void setDefaults() {

	}

	public Element toXml() {
		return null;
	}

	public String getHost() {
		return host;
	}

	public String getFrom() {
		return from;
	}

	public Set<String> getTo() {
		return to;
	}
}
