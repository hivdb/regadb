package net.sf.regadb.util.settings;

import org.jdom.Element;

public class ContaminationConfig extends ConfigParser {
	private double threshold;

	public ContaminationConfig() {
		super("contamination");
	}

	@Override
	public void setDefaults() {
		threshold = 0.5;
	}

	@Override
	public void parseXml(RegaDBSettings settings, Element e) {
		Element ee = e.getChild("threshold");
		if(ee != null)
			setThreshold(Double.parseDouble(ee.getText()));
	}

	@Override
	public Element toXml() {
		Element e = new Element(getXmlTag());
		
		Element ee = new Element("threshold");
		ee.setText(getThreshold() +"");
		e.addContent(ee);
		
		return e;
	}
	
	public double getThreshold(){
		return threshold;
	}
	public void setThreshold(double threshold){
		this.threshold = threshold;
	}

}
