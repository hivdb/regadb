package net.sf.regadb.util.settings;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import org.jdom.Comment;
import org.jdom.Element;

public class InstituteConfig implements IConfigParser {
	
	private Filter organismFilter = null;
	private File logDir;
	private File queryResultDir;
	private int reportDateTolerance;
	private String dateFormat;
	
	private WivConfig wivConfig;
	
	private HashMap<String, FormConfig> forms = new HashMap<String, FormConfig>();
	
	public String getXmlTag() {
		return "institute";
	}
	
	public InstituteConfig(){
		setDefaults();
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		Element ee = e.getChild("log-dir");
		if(ee != null)
			logDir = new File(ee.getTextTrim());
		
		ee = e.getChild("query-result-dir");
		if(ee != null)
			queryResultDir = new File(ee.getTextTrim());
		
		ee = e.getChild("wiv");
		if(ee != null){
			wivConfig = new WivConfig();
			wivConfig.parseXml(settings, ee);
		}
		
		ee = e.getChild("report-date-tolerance");
		if(ee != null){
			try{
				reportDateTolerance = Integer.parseInt(ee.getTextTrim());
			}
			catch(Exception ex){
				System.err.println("report-date-tolerance is not a number");
			}
		}
		
		ee = e.getChild("date-format");
		if(ee != null)
			setDateFormat(ee.getTextTrim());

		ee = e.getChild("organism-filter");
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
		setReportDateTolerance(2);
		setDateFormat("dd/MM/yyyy");
		organismFilter = null;
		
		wivConfig = null;
		
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

		r.addContent(new Comment("Directory for logging files, read/write permissions needed."));
		e = new Element("log-dir");
		e.setText(getLogDir().getAbsolutePath());
		r.addContent(e);
		
		r.addContent(new Comment("Directory for temporary query results, read/write permissions needed."));
		e = new Element("query-result-dir");
		e.setText(getQueryResultDir().getAbsolutePath());
		r.addContent(e);
		
		if(wivConfig != null)
			r.addContent(wivConfig.toXml());
		
		r.addContent(new Comment("The maximum amount of days a test result's date can deviate from " +
				"the viral isolate's sample date, for it to be included in a patient's viral isolate " +
				"resistance report."));
		e = new Element("report-date-tolerance");
		e.setText(""+reportDateTolerance);
		r.addContent(e);
		
		r.addContent(new Comment("The date format, detailed information on the format: http://java.sun.com/javase/6/docs/api/java/text/SimpleDateFormat.html"));
		e = new Element("date-format");
		e.setText(getDateFormat());
		r.addContent(e);
		
		if(organismFilter != null){
			r.addContent(new Comment("Only show test types, tests, drugs linked with this organism, i.e. 'HIV*' will only show HIV-1, HIV-2A/B, ... items."));
			e = new Element("organism-filter");
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
	
	public WivConfig getWivConfig(){
		return wivConfig;
	}
	public void setWivConfig(WivConfig wivConfig){
		this.wivConfig = wivConfig;
	}
}
