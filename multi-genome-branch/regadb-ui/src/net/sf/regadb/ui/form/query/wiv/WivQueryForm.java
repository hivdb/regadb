package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.io.exportCsv.ExportToCsv;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.io.util.WivObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WFileResource;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;
import eu.webtoolkit.jwt.WWidget;

public abstract class WivQueryForm extends FormWidget implements Signal1.Listener<WMouseEvent>{
    private WGroupBox generalGroup_;
    private WGroupBox parameterGroup_;
    private WGroupBox resultGroup_;
    
    private WTable generalTable_;
    private FormTable parameterTable_;
    private FormTable resultTable_;
    
    private WText description_;
    
    private Label statusL_;
    private WText status_;
    
    private Label linkL_;
    private WAnchor link_;
    
    private Label runL_;
    private WPushButton run_;
    
    private String query_;
    private String filename_;
    
    private SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");
    private DecimalFormat decimalFormat = new DecimalFormat("##########.00");
    
    private static String arcPatientQuery = "select arc_pav.patient.patientIi from PatientAttributeValue arc_pav where arc_pav.attribute.name = 'FOLLOW-UP' and lower(arc_pav.attributeNominalValue.value) = '1: arc of the same institution as arl'";

    
    private HashMap<String,IFormField> parameters_ = new HashMap<String,IFormField>();
    
    @SuppressWarnings("serial")
	public class EmptyResultException extends Exception{
        
    }
    
    public WivQueryForm(WString formName, WString description, WString filename){
        super(formName,InteractionState.Viewing);
        
        filename_ = filename.value().trim();
        description_ = new WText(description);
        
        init();
    }
    
    public void init(){
        generalGroup_   = new WGroupBox(tr("form.query.wiv.group.general"), this);
        parameterGroup_     = new WGroupBox(tr("form.query.wiv.group.parameters"), this);
        resultGroup_     = new WGroupBox(tr("form.query.wiv.group.run"), this);
        
        generalTable_ = new WTable(generalGroup_);
        parameterTable_ = new FormTable(parameterGroup_);
        resultTable_ = new FormTable(resultGroup_);

        runL_ = new Label(tr("form.query.wiv.label.run"));
        run_ = new WPushButton(tr("form.query.wiv.pushbutton.run"));

        linkL_ = new Label(tr("form.query.wiv.label.result"));
        link_ = new WAnchor("dummy", lt(""));
        
        statusL_ = new Label(tr("form.query.wiv.label.status"));
        status_ = new WText(tr("form.query.wiv.label.status.initial"));


        generalTable_.elementAt(0, 0).addWidget(description_);
        
        resultTable_.addLineToTable(new WWidget[]{runL_,run_});
        resultTable_.addLineToTable(new WWidget[]{statusL_,status_});
        resultTable_.addLineToTable(new WWidget[]{linkL_,link_});
        
        run_.clicked().addListener(this, this);
    }
    
    public void trigger(WMouseEvent a) 
    {
        run_.disable();
        status_.setText(tr("form.query.wiv.label.status.running"));

        try{
            File csvFile =  getOutputFile();
            
            process(csvFile);
            File output = postProcess(csvFile);
            setDownloadLink(output);
            status_.setText(tr("form.query.wiv.label.status.finished"));
        }
        catch(EmptyResultException e){
            status_.setText(tr("form.query.wiv.label.status.emptyResult"));
        }
        catch(Exception e){
            e.printStackTrace();
            status_.setText(tr("form.query.wiv.label.status.failed"));
        }
        
        run_.enable();
    }

    @SuppressWarnings("unchecked")
    protected void process(File csvFile) throws Exception{
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Query q = createQuery(t);
        
        if(q != null){
            List<Object> result = q.list();
            
            if(result.size()>0) {
                FileOutputStream os = new FileOutputStream(csvFile);
                
                ExportToCsv csvExport = new ExportToCsv();

                Set<Dataset> userDatasets = new HashSet<Dataset>();
                for(DatasetAccess da : t.getSettingsUser().getDatasetAccesses()) {
                    userDatasets.add(da.getId().getDataset());
                }
            
                if(q.getReturnTypes().length == 1)
                {
                    os.write((getCsvHeaderSwitchNoComma(result.get(0), csvExport)+"\n").getBytes());
                }
                else
                {
                    Object[] array = (Object[])result.get(0);
                    
                    for(int i = 0; i < array.length - 1; i++)
                    {
                        os.write((getCsvHeaderSwitchNoComma(array[i], csvExport)+",").getBytes());
                    }
    
                    os.write((getCsvHeaderSwitchNoComma(array[array.length - 1], csvExport)+"\n").getBytes());
                }
            
                for(Object o : result)
                {
                    if(q.getReturnTypes().length == 1)
                    {
                        os.write((getCsvLineSwitchNoComma(o, csvExport, userDatasets)+"\n").getBytes());
                    }
                    else
                    {
                        Object[] array = (Object[])o;
                        
                        for(int i = 0; i < array.length - 1; i++)
                        {
                            os.write((getCsvLineSwitchNoComma(array[i], csvExport, userDatasets)+",").getBytes());
                        }
        
                        os.write((getCsvLineSwitchNoComma(array[array.length - 1], csvExport, userDatasets)+"\n").getBytes());
                    }
                }
            
                os.close();
                
            }
            else{
                t.commit();
                throw new EmptyResultException();
            }
        }
        t.commit();
    }
    
    protected File postProcess(File csvFile) throws Exception{
        return csvFile;
    }
    
    public HashMap<String,IFormField> getParameters(){
        return parameters_;
    }
    
    protected Query createQuery(Transaction t){
        Query q = t.createQuery(getQuery());
        IFormField f;
        
        for(String name : parameters_.keySet()){
            f = parameters_.get(name);
            if(f.validate() && f.getFormText() != null && f.getFormText().length() > 0){
                setQueryParameter(q,name,f);
            }
            else{
                f.flagErroneous();
                return null;
            }
        }
        
        return q;
    }
    
    protected void setQueryParameter(Query q, String name, IFormField f){
        if(f.getClass() == DateField.class)
            q.setDate(name, ((DateField)f).getDate());
    }
    
    public String getCsvLineSwitchNoComma(Object o, ExportToCsv csvExport, Set<Dataset> datasets) {
        String temp = csvExport.getCsvLineSwitch(o, datasets, null);
        if(temp==null)
            return temp;
        temp = temp.substring(0, temp.length()-1);
        return temp;
    }
    
    public String getCsvHeaderSwitchNoComma(Object o, ExportToCsv csvExport) {
        String temp = csvExport.getCsvHeaderSwitch(o);
        if(temp==null)
            return temp;
        temp = temp.substring(0, temp.length()-1);
        return temp;
    }
    
    public String getFileName(){
        return filename_;
    }
    
    public void setFileName(String filename){
        filename_ = filename;
    }
    
    public String getQuery(){
        return query_;
    }
    
    public void setQuery(String query){
        query_ = query;
    }
    
    public void addParameter(String name, WString l, IFormField f){
    	parameterTable_.addLineToTable(new Label(l), f);
        parameters_.put(name, f);
    }
    
    public File getResultDir(){
        File wivDir = new File(RegaDBSettings.getInstance().getPropertyValue("regadb.query.resultDir") + File.separatorChar + "wiv");
        if(!wivDir.exists()){
            wivDir.mkdirs();
        }
        return wivDir;
    }
    
    public File getOutputFile() 
    {
        File wivDir = getResultDir();
        return new File(wivDir.getAbsolutePath() + File.separatorChar + getFileName() + ".csv");
    }    
     
    public void setDownloadLink(File file){
        link_.setText(lt("Download Query Result [" + new Date(System.currentTimeMillis()).toString() + "]"));
        WFileResource res = new WFileResource("application/csv", file.getAbsolutePath());
        res.suggestFileName(filename_ +".csv");
        link_.setResource(res);
    }

    @Override
    public void cancel() {
        
    }

    @Override
    public WString deleteObject() {
        return null;
    }

    @Override
    public void redirectAfterDelete() {
        
    }

    @Override
    public void saveData() {
        
    }
    
    // Parse utility methods
    
    protected enum OriginCode{
        ARC,ARL;
        
        public int getCode(){
            if(this == ARC)
               return 2;
            if(this == ARL)
                return 1;
            return -1;
        }
    };
    
    protected enum TypeOfInformationCode{
        LAB_RESULT,THERAPY,LAST_CONTACT_DATE,DEATH;
        
        public int getCode(){
            if(this == LAB_RESULT)
                return 1;
            if(this == THERAPY)
                return 2;
            if(this == LAST_CONTACT_DATE)
                return 3;
            if(this == DEATH)
                return 4;
            return -1;
        }
    };
    
    protected enum TestCode{
        VL,T4,T4PERCENT;
        
        public int getCode(){
            if(this == VL)
                return 1;
            if(this == T4)
                return 2;
            if(this == T4PERCENT)
            	return 3;
            return -1;
        }
    };
    
    protected enum CauseOfDeathCode{
        HIV,HEPATITE,CARDIO,SUICIDE,OTHER,UNKNOWN;
    
        public int getCode(){
            if(this == HIV)
                return 1;
            if(this == HEPATITE)
                return 2;
            if(this == CARDIO)
                return 3;
            if(this == SUICIDE)
                return 4;
            if(this == OTHER)
                return 5;
            if(this == UNKNOWN)
                return 9;
            return -1;
        }
    };
    
    protected String getHivTypeCode(String value){
        if(value == null)                       return null;
        if(value.equals("HIV 1"))               return "1";
        if(value.equals("HIV 2"))               return "2";
        if(value.equals("HIV 1/2 Coinfection")) return "3";
        if(value.equals("HIV Undetermined"))    return "4";
        return null;
    }
    
    protected String getCentreName(){
        return RegaDBSettings.getInstance().getPropertyValue("centre.name");
    }
    
    protected String getFormattedDate(Date date){
    	return getFormattedDate(date,"yyyyMMdd");
    }
    
    protected String getFormattedDate(Date date,String format){
    	if(date != null)
            return (new SimpleDateFormat(format)).format(date);
        else
            return getPadding(format.length());
    }
    
    protected Date getDate(String date){
        try{
            if(date.indexOf('-') != -1)
                return sdf_.parse(date);
            else
                return new Date(Long.parseLong(date));
        }
        catch(Exception e){
            return null;
        }
    }
    
    protected int getTestTypeNumber(TestType tt){
        if(StandardObjects.getHiv1ViralLoadTestType().getDescription().equals(tt.getDescription()))
            return 1;
        
        if(StandardObjects.getCd4TestType().getDescription().equals(tt.getDescription()))
            return 2;
        
        return -1;
    }
    
    protected double parseValue(String value){
        return Double.parseDouble(value.replace("<", "").replace("=", "").replace(">", ""));
    }

    protected String getFormattedDecimal(String value){
        return getFormattedDecimal(value, 2, -1);
    }
    
    protected String getFormattedDecimal(String value, int maxFractionDigits, int minFractionDigits){
        double d =  parseValue(value);
        return getFormattedDecimal(d,maxFractionDigits, minFractionDigits);
    }
    
    protected String getFormattedDecimal(double value){
        return getFormattedDecimal(value,2,2);
    }
    
    protected String getFormattedDecimal(double value, int maxFractionDigits, int minFractionDigits){
        if(maxFractionDigits > -1)
            decimalFormat.setMaximumFractionDigits(maxFractionDigits);
        if(minFractionDigits > -1)
            decimalFormat.setMinimumFractionDigits(minFractionDigits);
        String s = decimalFormat.format(value).replace(".", ",");
        return s;
    }
    
    protected Table readTable(File csvFile) {
        try {
            return Table.readTable(csvFile.getAbsolutePath());
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return null;
    }
    
    private static final String paddingString = "????????????????????";
    protected static String getPadding(int length){
        String padding = paddingString;
        for(int i=1;i< java.lang.Math.ceil(length/paddingString.length()); ++i){
            padding += paddingString;
        }
        return padding.substring(0, length);
    }
    private static String getPaddingString(String s, int length){
        int toPad = length - (s==null?0:s.length());
        if(toPad < 1)
            return "";
        else
            return getPadding(toPad);
    }
    protected static String getPaddedRight(String s, int length){
        return s + getPaddingString(s,length);
    }
    protected static String getPaddedLeft(String s, int length){
        return getPaddingString(s,length) + s;
    }

    
    protected String getFormattedString(PatientAttributeValue pav){
        String s = pav.getValue();
        String attr = pav.getAttribute().getName();
        
        if(ValueTypes.getValueType(pav.getAttribute().getValueType()) == ValueTypes.NOMINAL_VALUE){
            if(pav.getAttribute().getAttributeGroup().getGroupName().equals("WIV")){
                return getAbbreviation(pav.getAttributeNominalValue().getValue());
            }
            
            if(attr.equals("Gender")){
                if(pav.getAttributeNominalValue().getValue().equals("male"))
                    return "M";
                else
                    return "F";
            }
        }
        
        if(ValueTypes.getValueType(pav.getAttribute().getValueType()) == ValueTypes.DATE){
        	Date date = DateUtils.parseDate(s);
        	if(attr.equals("ARRIVAL_B") || attr.equals("YEARTRANSF") || attr.equals("PROBYEAR")){
        	    if(s != null && s.length() == 4)   //for wrong imports!
        	        return s;
        		return getFormattedDate(date, "yyyy");
        	}
        	
       		return getFormattedDate(date);
        }

        return s;
    }
    
    protected String getAbbreviation(String nominal){
        int i = nominal.indexOf(':');
        if(i != -1)
            return nominal.substring(0,i);
        else
            return nominal;
    }

    protected TestResult getFirstTestResult(Patient p, TestType[] tt){
        TestResult tr = null;
        
        Date d = new Date();
        
        for(TestResult t : p.getTestResults()){
        	for(int i=0;i<tt.length;++i){
        		if(t.getTest().getTestType().getDescription().equals(tt[i].getDescription())){
        			if(t.getTestDate().before(d)){
        				d = t.getTestDate();
        				tr = t;
        			}
        			break;
        		}
            }
        }
        
        return tr;
    }
    

    protected String getFormattedViralLoadResult(TestResult tr){
        String value = tr.getValue();
        
        boolean log10 = tr.getTest().getTestType().getDescription().equals(StandardObjects.getHiv1ViralLoadTestType().getDescription()); 
        return getFormattedViralLoadResult(value,log10,false);
    }
    
    protected String getFormattedViralLoadResult(String value, boolean log10, boolean toLog10){
        String s;
        char prefix = value.charAt(0);
        
        if(toLog10){
            double vl = parseValue(value);
            vl = java.lang.Math.log10(vl);
            value = vl+"";
        }
        
        if(log10){
            s = getFormattedViralLoadLog10(value);
        }
        else{
            s = getFormattedDecimal(value,2,2);
        }

        char prefixes[] = {'<','>'};
        for(char c : prefixes){
            if(prefix == c){
                s = c + s;
                break;
            }
        }

        return s;
    }
    
    protected String getFormattedViralLoadLog10(String value){
        double d = java.lang.Math.log10(parseValue(value));
        return getFormattedDecimal(d,2,2);
    }
    
    protected enum FillerType {QUESTIONMARK, UNKNOWN, COUNTRYCODE};
    protected class EpidemiologyTable{
        private Map<String, Integer> position = new HashMap<String, Integer>();
        private List<Integer> length = new ArrayList<Integer>();
        private List<FillerType> filler = new ArrayList<FillerType>();
        private ArrayList<String> header = new ArrayList<String>();

        public void add(String name, String header, int length){
            add(name,header,length,FillerType.QUESTIONMARK);
        }
        
        public void add(String name, String header, int length, FillerType filler){
            this.position.put(name, position.size());
            this.header.add(header);
            this.length.add(length);
            this.filler.add(filler);
        }
        
        public ArrayList<String> getHeader(){
            return header;
        }
        
        public int size(){
            return position.size();
        }
        
        public Integer getPosition(String name){
            return position.get(name);
        }
        
        public int getLength(String name){
            Integer i = position.get(name);
            return (i != null? getLength(i):0);
        }
        
        public int getLength(int i){
            return length.get(i);
        }
        
        public FillerType getFillerType(int i){
            return filler.get(i);
        }
        
        public String getEmptyValue(int position){
            FillerType f = getFillerType(position);
            if(f == FillerType.QUESTIONMARK)
                return getPadding(getLength(position));
            
            if(f == FillerType.UNKNOWN)
                return "U";
            
            if(f == FillerType.COUNTRYCODE)
                return "999";
            
            return "";
        }
    }
    
    protected Table getArlEpidemiologyTable(List<Patient> patients){
        EpidemiologyTable table = new EpidemiologyTable();
        
        table.add("PatCode",                    "PAT_CODE",     13);
        table.add("REF_LABO",                   "REF_LABO",     0);
        table.add("HivConf.TestResult.value",   "DATE_TEST",    8);
        table.add("Patient.birthDate",          "BIRTH_DATE",   8);
        table.add("Gender",                     "SEX",          1);
        table.add("HivType.TestResult.value",   "HIVTYPE",      1);
        table.add("NATION",                     "NATION",       3,  FillerType.COUNTRYCODE);
        table.add("COUNTRY",                    "COUNTRY",      3,  FillerType.COUNTRYCODE);
        table.add("RESID_B",                    "RESID_B",      2);
        table.add("ORIGIN",                     "ORIGIN",       3,  FillerType.COUNTRYCODE);
        table.add("ARRIVAL_B",                  "ARRIVAL_B",    0);
        table.add("SEXCONTACT",                 "SEXCONTACT",   1,  FillerType.UNKNOWN);
        table.add("SEXPARTNER",                 "SEXPARTNER",   1,  FillerType.UNKNOWN);
        table.add("NATPARTNER",                 "NATPARTNER",   0);
        table.add("BLOODBORNE",                 "BLOODBORNE",   1,  FillerType.UNKNOWN);
        table.add("YEARTRANSF",                 "YEARTRANSF",   0);
        table.add("TRANCOUNTR",                 "TRANCOUNTR",   0);
        table.add("CHILD",                      "CHILD",        1);
        table.add("PROFRISK",                   "PROFRISK",     1,  FillerType.UNKNOWN);
        table.add("PROBYEAR",                   "PROBYEAR",     1,  FillerType.UNKNOWN);
        table.add("PROBCOUNTR",                 "PROBCOUNTR",   3,  FillerType.COUNTRYCODE);
        table.add("LYMPHO",                     "LYMPHO",       4);
        table.add("VIRLOAD",                    "VIRLOAD",      4);
        table.add("STAD_CLIN",                  "STAD_CLIN",    1,  FillerType.UNKNOWN);
        table.add("REASONTEST",                 "REASONTEST",   1,  FillerType.UNKNOWN);
        table.add("FORM_OUT",                   "FORM_OUT",     8);
        table.add("FORM_IN",                    "FORM_IN",      8);
        table.add("LABO",                       "LABO",         3);
        table.add("Comment",                    "OPMERKING",    0);
        
        Table res = new Table();
        String [] row;
        
        res.addRow(table.getHeader());
        
        for(Patient p : patients){
            
            TestResult tr;
            
            tr = getFirstTestResult(p, new TestType[]{WivObjects.getGenericwivConfirmation().getTestType()});
            if(tr != null){
                row = new String[table.size()];

                TestNominalValue tnv = tr.getTestNominalValue();
                if(tnv != null){
                    String hivTypeCode = getHivTypeCode(tnv.getValue());
                    if(hivTypeCode != null)
                        row[table.getPosition("HivType.TestResult.value")] = hivTypeCode;
                    else
                        continue;
                }
                else
                    continue;
                
	            row[table.getPosition("Patient.birthDate")] = getFormattedDate(p.getBirthDate());
	            
//	            tr = getFirstTestResult(p, new TestType[]{StandardObjects.getHiv1ViralLoadTestType(),StandardObjects.getHiv1ViralLoadLog10TestType()});
//	            if(tr != null){
//	            	row[table.getPosition("VL.TestResult.value")] = getFormattedViralLoadResult(tr);
//	            }
//	            
//	            tr = getFirstTestResult(p, new TestType[]{StandardObjects.getCd4TestType()});
//	            if(tr != null){
//	            	row[table.getPosition("CD4.TestResult.value")] = getFormattedDecimal(tr.getValue(),0,0);
//	            }
//	            else
//	            	row[table.getPosition("CD4.TestResult.value")] = "U";
	            
	            tr = getFirstTestResult(p, new TestType[]{WivObjects.getGenericwivConfirmation().getTestType()});
                if(tr != null){
                    row[table.getPosition("HivConf.TestResult.value")] = getFormattedDate(tr.getTestDate());
                }
	            
	            row[table.getPosition("LABO")] = getCentreName();
	            
	            for(PatientAttributeValue pav : p.getPatientAttributeValues()){
	                Integer pos = table.getPosition(pav.getAttribute().getName());
	                if(pos != null){
	                    String s = null;
	
	                    s = getFormattedString(pav);
	                    if(row[pos] != null)
	                        s = row[pos] + s;
	 
	                    row[pos] = s;
	                }
	                
	            }
	            
	            //fill empty fields
	            for(int j=0; j<row.length; ++j){
	                if(row[j] == null){
	                    row[j] = table.getEmptyValue(j);
	                }
	            }
	
	            ArrayList<String> lRow = new ArrayList<String>(Arrays.asList(row));
	            res.addRow(lRow);
	        }
        }
        
        return res;
    }
    
    protected PatientAttributeValue getPatientAttributeValue(Patient p, String name){
    	for(PatientAttributeValue pav : p.getPatientAttributeValues()){
    		if(pav.getAttribute().getName().equals(name))
    			return pav;
    	}
    	return null;
    }
    
    protected Transaction createTransaction(){
    	return RegaDBMain.getApp().createTransaction();
    }
    
    protected String getArcPatientQuery(){
    	return arcPatientQuery;
    }
}
