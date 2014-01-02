package net.sf.regadb.util.settings;

import java.io.File;
import java.util.HashMap;

import org.jdom.Comment;
import org.jdom.Element;

public class InstituteConfig extends ConfigParser {
	public final static String WTS_URL = "$WTS_URL";
	
	private Filter organismFilter = null;
	private File logDir;
	private File queryResultDir;
	private File importToolDir;
	private int reportDateTolerance;
	private String dateFormat;
	private int minYear;
	private int maxDaysFuture;
	
	private String wtsUrl;
	private boolean wtsUrlSubtyping = false;
	private boolean wtsUrlUpdates = false;
	private boolean wtsUrlAlignment = false;
	private int wtsNrOfRetries = 0;

	private String databaseBackupScript;

	private boolean sampleDateMandatory = true;
	private String logo;
	private boolean trugeneFix = false;
	
	private String defaultDataset = null;

	private WivConfig wivConfig;
	private HicdepConfig hicdepConfig;

	private EmailConfig emailConfig;

	private HashMap<String, FormConfig> forms = new HashMap<String, FormConfig>();
	
	public InstituteConfig(){
		super("institute");
	}

	public void parseXml(RegaDBSettings settings, Element e) {
		Element ee = e.getChild("log-dir");
		if(ee != null)
			logDir = new File(ee.getTextTrim());
		
		ee = e.getChild("logo");
		if(ee != null)
			logo = ee.getTextTrim();
		
		ee = e.getChild("query-result-dir");
		if(ee != null)
			queryResultDir = new File(ee.getTextTrim());
		
		ee = e.getChild("import-tool-dir");
		if(ee != null)
			importToolDir = new File(ee.getTextTrim());
		
		ee = e.getChild("trugene-fix");
		if (ee != null)
			trugeneFix = Boolean.parseBoolean(ee.getTextTrim());
		
		ee = e.getChild("wiv");
		if(ee != null){
			wivConfig = new WivConfig();
			wivConfig.parseXml(settings, ee);
		}
		
		ee = e.getChild("hicdep");
		if(ee != null){
			hicdepConfig = new HicdepConfig();
			hicdepConfig.parseXml(settings, ee);
		}
		
		ee = e.getChild("e-mail");
		if (ee != null) {
			emailConfig = new EmailConfig();
			emailConfig.parseXml(settings, ee);
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

		ee = e.getChild("min-year");
		if(ee != null)
			setMinYear(Integer.parseInt(ee.getTextTrim()));

		ee = e.getChild("max-days-future");
		if(ee != null)
			setMaxDaysFuture(Integer.parseInt(ee.getTextTrim()));
		
		ee = e.getChild("organism-filter");
		if(ee != null)
			organismFilter = new Filter(ee.getText());
		
		ee = e.getChild("sample-date-mandatory");
		if(ee != null)
			sampleDateMandatory = Boolean.parseBoolean(ee.getText());
		
		ee = e.getChild("default-dataset");
		if(ee != null)
			setDefaultDataset(ee.getTextTrim());
			
		ee = e.getChild("forms");
		if(ee != null){
			for(Object oo : ee.getChildren()){
				parseForm(settings, (Element)oo);
			}
		}
		
		ee = e.getChild("wts-url");
		if(ee != null){
			setWtsUrl(ee.getTextTrim());
			String v = ee.getAttributeValue("useForSubtyping");
			setUseWtsUrlForSubtyping(v != null && v.toLowerCase().equals("true"));
			v = ee.getAttributeValue("useForUpdates");
			setUseWtsUrlForUpdates(v != null && v.toLowerCase().equals("true"));
			v = ee.getAttributeValue("useForAlignment");
			setUseWtsUrlForAlignment(v != null && v.toLowerCase().equals("true"));
		}
		
		ee = e.getChild("wts-nr-of-retries");
		if (ee != null)
			wtsNrOfRetries = Integer.parseInt(ee.getTextTrim());
		
		ee = e.getChild("database-backup-script");
		if (ee != null)
			setDatabaseBackupScript(ee.getTextTrim());
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
		addFormConfig(new ViralIsolateFormConfig());
		addFormConfig(new GhbHcvExportFormConfig());
		
		setMinYear(-1);
		setMaxDaysFuture(-1);
		
		setWtsUrl(getDefaultWtsUrl());
		
		wtsNrOfRetries = 0;
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
		
		e = new Element("import-tool-dir");
		e.setText(getImportToolDir().getAbsolutePath());
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
		
		if(getMinYear() != -1){
			e = new Element("min-year");
			e.setText(getMinYear()+"");
			r.addContent(e);
		}
		
		if(getMaxDaysFuture() != -1){
			e = new Element("max-days-future");
			e.setText(getMaxDaysFuture()+"");
			r.addContent(e);
		}
		
		if(organismFilter != null){
			r.addContent(new Comment("Only show test types, tests, drugs linked with this organism, i.e. 'HIV*' will only show HIV-1, HIV-2A/B, ... items."));
			e = new Element("organism-filter");
			e.setText(organismFilter.getConfigString());
			r.addContent(e);
		}
		
		if(defaultDataset != null){
			e = new Element("default-dataset");
			e.setText(getDefaultDataset());
			r.addContent(e);
		}
		
		if(forms.size() > 0){
			e = new Element("forms");
			r.addContent(e);
			
			for(FormConfig fc : forms.values())
				e.addContent(fc.toXml());
		}
		
		e = new Element("wts-url");
		e.setText(getWtsUrl().toString());
		e.setAttribute("useForSubtyping", getUseWtsUrlForSubtyping()+"");
		e.setAttribute("useForUpdates", getUseWtsUrlForUpdates()+"");
		e.setAttribute("useForAlignment", getUseWtsUrlForAlignment()+"");
		r.addContent(e);
		
		e = new Element("wts-nr-of-retries");
		e.setText(wtsNrOfRetries + "");
		
		return r;
	}

	public boolean isSampleDateMandatory() {
		return sampleDateMandatory;
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
	
	public ViralIsolateFormConfig getViralIsolateFormConfig(){
		return (ViralIsolateFormConfig) forms.get(ViralIsolateFormConfig.NAME);
	}
	
	public GhbHcvExportFormConfig getGhbHcvExportFormConfig() {
		return (GhbHcvExportFormConfig) forms.get(GhbHcvExportFormConfig.NAME);
	}
	
	public void setLogDir(File logDir) {
		this.logDir = logDir;
	}

	public File getLogDir() {
		return logDir;
	}
	
	public String getLogo() {
		return logo;
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
	
	public EmailConfig getEmailConfig() {
		return emailConfig;
	}

	public void setMinYear(int minYear) {
		this.minYear = minYear;
	}

	public int getMinYear() {
		return minYear;
	}

	public void setMaxDaysFuture(int maxDaysInFuture) {
		this.maxDaysFuture = maxDaysInFuture;
	}

	public int getMaxDaysFuture() {
		return maxDaysFuture;
	}
	
	public File getImportToolDir() {
		return importToolDir;
	}

	public void setImportToolDir(File importToolDir) {
		this.importToolDir = importToolDir;
	}

	public boolean getUseWtsUrlForUpdates(){
		return wtsUrlUpdates;
	}
	
	public void setUseWtsUrlForUpdates(boolean b){
		this.wtsUrlUpdates = b;
	}

	public boolean getUseWtsUrlForSubtyping(){
		return wtsUrlSubtyping;
	}
	
	public void setUseWtsUrlForSubtyping(boolean b){
		this.wtsUrlSubtyping = b;
	}
	
	public boolean getUseWtsUrlForAlignment(){
		return wtsUrlAlignment;
	}
	
	public void setUseWtsUrlForAlignment(boolean b){
		this.wtsUrlAlignment = b;
	}
	
	public void setWtsUrl(String wtsUrl) {
		if(!wtsUrl.endsWith("/"))
			wtsUrl += '/';
		if(!wtsUrl.startsWith("http://") && !wtsUrl.startsWith("https://"))
			wtsUrl = "http://"+ wtsUrl;
		this.wtsUrl = wtsUrl;
	}

	public String getWtsUrl() {
		return wtsUrl;
	}
	
	public String getWtsUrl(String url){
		
		if(url.equals(WTS_URL))
			return getWtsUrl();
		else
			return url;
	}
	
	public static String getDefaultWtsUrl() {
		return "http://regadb.med.kuleuven.be/wts/services/";
		//return "http://127.0.0.1:8080/wts/services/";
	}
	
	public boolean isTrugeneFix() {
		return trugeneFix;
	}
	
	public String getDefaultDataset(){
		return defaultDataset;
	}
	
	public void setDefaultDataset(String dataset){
		this.defaultDataset = dataset;
	}
	
	public String getDatabaseBackupScript() {
		return databaseBackupScript;
	}

	public void setDatabaseBackupScript(String databaseBackupScript) {
		this.databaseBackupScript = databaseBackupScript;
	}
	
	public HicdepConfig getHicdepConfig() {
		return hicdepConfig;
	}

	public void setHicdepConfig(HicdepConfig hicdepConfig) {
		this.hicdepConfig = hicdepConfig;
	}
	
	public int getWtsNrOfRetries() {
		return wtsNrOfRetries;
	}

	public void setWtsNrOfRetries(int wtsNrOfRetries) {
		this.wtsNrOfRetries = wtsNrOfRetries;
	}
}
