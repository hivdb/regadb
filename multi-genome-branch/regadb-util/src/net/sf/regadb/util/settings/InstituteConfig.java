package net.sf.regadb.util.settings;

import java.io.File;
import java.util.HashMap;

import org.jdom.Element;

public class InstituteConfig implements IConfigParser {
	
	private Filter organismFilter = null;
	private File logDir;
	private File queryResultDir;
	private String wivCentreName;
	private int reportDateTolerance;
	private String dateFormat;
	
	private HashMap<String, FormConfig> forms = new HashMap<String, FormConfig>();
	
	public String getXmlTag() {
		return "institute";
	}
	
	public InstituteConfig(){
		setDefaults();
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		Element ee = e.getChild("logDir");
		if(ee != null)
			logDir = new File(ee.getTextTrim());
		
		ee = e.getChild("queryResultDir");
		if(ee != null)
			queryResultDir = new File(ee.getTextTrim());
		
		ee = e.getChild("wivCentreName");
		if(ee != null)
			wivCentreName = ee.getTextTrim();
		
		ee = e.getChild("reportDateTolerance");
		if(ee != null){
			try{
				reportDateTolerance = Integer.parseInt(ee.getTextTrim());
			}
			catch(Exception ex){
				System.err.println("reportDateTolerance is not a number");
			}
		}
		
		ee = e.getChild("dateFormat");
		if(ee != null)
			setDateFormat(ee.getTextTrim());

		ee = e.getChild("organismFilter");
		if(ee != null)
			organismFilter = new Filter(ee.getText());
		
		ee = e.getChild("forms");
		if(ee != null){
			for(Object oo : ee.getChildren()){
				parseForm(settings, (Element)oo);
			}
		}
	}
	
	private void parseForm(RegaDBSettings settings, Element e){
		FormConfig cfg = forms.get(e.getAttributeValue("name"));
		if(cfg != null)
			cfg.parseXml(settings, e);
		else
			System.out.println("Form config not supported: "+ e.getAttributeValue("name"));
	}
	
	public void setDefaults() {
		setLogDir(new File("/etc/rega_institute/regadb/log"));
		setQueryResultDir(new File("/etc/rega_institute/regadb/query"));
		setWivCentreName("KUL");
		setReportDateTolerance(2);
		setDateFormat("dd/MM/yyyy");
		organismFilter = null;
		
		forms.clear();
		addFormConfig(new SelectPatientFormConfig());
		addFormConfig(new ContactFormConfig());
	}
	
	public void addFormConfig(FormConfig form){
		forms.put(form.getFormName(),form);
	}

	public Element toXml() {
		Element r = new Element(getXmlTag());
		Element e;

		e = new Element("logDir");
		e.setText(getLogDir().getAbsolutePath());
		r.addContent(e);
		
		e = new Element("queryResultDir");
		e.setText(getQueryResultDir().getAbsolutePath());
		r.addContent(e);
		
		e = new Element("wivCentreName");
		e.setText(wivCentreName);
		r.addContent(e);
		
		e = new Element("reportDateTolerance");
		e.setText(""+reportDateTolerance);
		r.addContent(e);
		
		e = new Element("dateFormat");
		e.setText(getDateFormat());
		r.addContent(e);
		
		if(organismFilter != null){
			e = new Element("organismFilter");
			e.setText(organismFilter.getConfigString());
			r.addContent(e);
		}
		
		if(forms.size() > 0){
			e = new Element("forms");
			r.addContent(e);
			
			for(FormConfig fc : forms.values())
				e.addContent(fc.toXml());
		}
		
		return r;
	}

	public Filter getOrganismFilter(){
		return organismFilter;
	}
	public SelectPatientFormConfig getSelectPatientFormConfig(){
		return (SelectPatientFormConfig) forms.get(SelectPatientFormConfig.NAME);
	}
	public ContactFormConfig getContactFormConfig(){
		return (ContactFormConfig) forms.get(ContactFormConfig.NAME);
	}
	
	public void setLogDir(File logDir) {
		this.logDir = logDir;
	}

	public File getLogDir() {
		return logDir;
	}

	public void setQueryResultDir(File queryResultDir) {
		this.queryResultDir = queryResultDir;
	}

	public File getQueryResultDir() {
		return queryResultDir;
	}

	public void setWivCentreName(String wivCentreName) {
		this.wivCentreName = wivCentreName;
	}

	public String getWivCentreName() {
		return wivCentreName;
	}

	public void setReportDateTolerance(int reportDateTolerance) {
		this.reportDateTolerance = reportDateTolerance;
	}

	public int getReportDateTolerance() {
		return reportDateTolerance;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDateFormat() {
		return dateFormat;
	}
}
