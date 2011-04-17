package net.sf.regadb.util.settings;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class ContaminationConfig extends ConfigParser {
	public class Distribution {		
		public String organism;
		public String orf;
		public int start;
		public int end;
		
		public double Di_mu;
		public double Di_sigma;
		public double Do_mu;
		public double Do_sigma;
	}
	
	private double threshold;
	private boolean sendMail;
	private List<Distribution> distributions = new ArrayList<Distribution>();

	public ContaminationConfig() {
		super("contamination");
	}

	@Override
	public void setDefaults() {
		threshold = 0.5;
		sendMail = false;
	}

	@Override
	public void parseXml(RegaDBSettings settings, Element e) {
		Element ee = e.getChild("threshold");
		if(ee != null)
			setThreshold(Double.parseDouble(ee.getText()));
		
		ee = e.getChild("send-mail");
		if(ee != null)
			setSendMail(Boolean.parseBoolean(ee.getTextTrim()));
		
		ee = e.getChild("distributions");
		for (Object d_o : ee.getChildren("distribution")) {
			Distribution d = new Distribution();
			
			Element d_e = (Element)d_o;
			d.organism = d_e.getAttributeValue("organism");
			d.orf = d_e.getAttributeValue("orf");
			d.start = Integer.parseInt(d_e.getAttributeValue("start"));
			d.end = Integer.parseInt(d_e.getAttributeValue("end"));
			d.Di_mu = Double.parseDouble(d_e.getAttributeValue("Di_mu"));
			d.Di_sigma = Double.parseDouble(d_e.getAttributeValue("Di_sigma"));
			d.Do_mu = Double.parseDouble(d_e.getAttributeValue("Do_mu"));
			d.Do_sigma = Double.parseDouble(d_e.getAttributeValue("Do_sigma"));
			
			distributions.add(d);
		}
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
	
	public boolean isSendMail() {
		return sendMail;
	}

	public void setSendMail(boolean sendMail) {
		this.sendMail = sendMail;
	}
	
	public List<Distribution> getDistributions() {
		return distributions;
	}
}
