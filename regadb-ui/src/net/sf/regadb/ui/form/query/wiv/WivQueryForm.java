package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import net.sf.regadb.util.date.DateUtils;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WWidget;
import net.sf.witty.wt.i8n.WMessage;

import org.hibernate.Query;

public abstract class WivQueryForm extends FormWidget implements SignalListener<WMouseEvent>{
    private WGroupBox generalGroup_;
    private WGroupBox parameterGroup_;
    private WGroupBox resultGroup_;
    
    private WTable generalTable_;
    private WTable parameterTable_;
    private WTable resultTable_;
    
    private Label description_;
    
    private Label statusL_;
    private Label status_;
    
    private Label linkL_;
    private WAnchor link_;
    
    private Label runL_;
    private WPushButton run_;
    
    private String query_;
    private String filename_;
    
    private SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");
    
    private HashMap<String,IFormField> parameters_ = new HashMap<String,IFormField>();
    
    public WivQueryForm(WMessage formName, WMessage description, WMessage filename){
        super(formName,InteractionState.Viewing);
        
        filename_ = filename.value();
        description_ = new Label(description);
        
        init();
    }
    
    public void init(){
        generalGroup_   = new WGroupBox(tr("form.query.wiv.group.general"), this);
        parameterGroup_     = new WGroupBox(tr("form.query.wiv.group.parameters"), this);
        resultGroup_     = new WGroupBox(tr("form.query.wiv.group.run"), this);
        
        generalTable_ = new WTable(generalGroup_);
        parameterTable_ = new WTable(parameterGroup_);
        resultTable_ = new WTable(resultGroup_);

        runL_ = new Label(tr("form.query.wiv.label.run"));
        run_ = new WPushButton(tr("form.query.wiv.pushbutton.run"));

        linkL_ = new Label(tr("form.query.wiv.label.result"));
        link_ = new WAnchor("dummy", lt(""));
        
        statusL_ = new Label(tr("form.query.wiv.label.status"));
        status_ = new Label(tr("form.query.wiv.label.status.initial"));


        generalTable_.putElementAt(0, 0, description_);
        
        addLineToTable(resultTable_,new WWidget[]{runL_,run_});
        addLineToTable(resultTable_,new WWidget[]{statusL_,status_});
        addLineToTable(resultTable_,new WWidget[]{linkL_,link_});
        
        run_.clicked.addListener(this);
    }
    
    public void notify(WMouseEvent a) 
    {
        run_.disable();
        status_.setText(tr("form.query.wiv.label.status.running"));

        try{
            File csvFile =  getOutputFile();
            
            if(process(csvFile)){
                File output = postProcess(csvFile);
                
                setDownloadLink(output);
                
                status_.setText(tr("form.query.wiv.label.status.finished"));
            }
            else{
                status_.setText(tr("form.query.wiv.label.status.failed"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
            status_.setText(tr("form.query.wiv.label.status.failed"));
        }
        
        run_.enable();
    }

    @SuppressWarnings("unchecked")
    protected boolean process(File csvFile){
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Query q = createQuery(t);
        
        if(q != null){
            try{
                FileOutputStream os = new FileOutputStream(csvFile);
    
                ExportToCsv csvExport = new ExportToCsv();
                List<Object> result = q.list();
                
                Set<Dataset> userDatasets = new HashSet<Dataset>();
                for(DatasetAccess da : t.getSettingsUser().getDatasetAccesses()) {
                    userDatasets.add(da.getId().getDataset());
                }
                
                if(result.size()>0) {
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
                
                t.commit();
                return true;
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        t.commit();
        return false;
    }
    
    protected File postProcess(File csvFile){
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
        String temp = csvExport.getCsvLineSwitch(o, datasets);
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
    
    public void addParameter(String name, WMessage l, IFormField f){
        addLineToTable(parameterTable_, new Label(l), f);
        parameters_.put(name, f);
    }
    
    public File getResultDir(){
        File wivDir = new File(RegaDBSettings.getInstance().getPropertyValue("regadb.query.resultDir") + File.separatorChar + "wiv");
        if(!wivDir.exists()){
            wivDir.mkdir();
        }
        return wivDir;
    }
    
    public File getOutputFile() 
    {
        File wivDir = getResultDir();
        return new File(wivDir.getAbsolutePath() + File.separatorChar + getFileName() + ".csv");
    }    
     
    public void setDownloadLink(File file){
        link_.label().setText(lt("Download Query Result [" + new Date(System.currentTimeMillis()).toString() + "]"));
        link_.setRef(new WFileResource("application/csv", file.getAbsolutePath()).generateUrl());
    }

    @Override
    public void cancel() {
        
    }

    @Override
    public WMessage deleteObject() {
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
        VL,T4;
        
        public int getCode(){
            if(this == VL)
                return 1;
            if(this == T4)
                return 2;
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
        if(StandardObjects.getViralLoadTestType().getDescription().equals(tt.getDescription()))
            return 1;
        
        if(StandardObjects.getCd4TestType().getDescription().equals(tt.getDescription()))
            return 2;
        
        return -1;
    }
    
    protected double parseValue(String value){
        return Double.parseDouble(value.replace("<", "").replace("=", "").replace(">", ""));
    }

    protected String getFormattedDecimal(String value){
        return getFormattedDecimal(value, 2);
    }
    
    protected String getFormattedDecimal(String value, int maxFractionDigits){
        double d =  parseValue(value);
        return getFormattedDecimal(d,maxFractionDigits);
    }
    
    protected String getFormattedDecimal(double value){
        return getFormattedDecimal(value,2);
    }
    
    protected String getFormattedDecimal(double value, int maxFractionDigits){
        DecimalFormat df = new DecimalFormat("##########.00");
        df.setMaximumFractionDigits(maxFractionDigits);
        String s = df.format(value).replace(".", ",");
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
        String s = null;
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
        	Date date = DateUtils.parseDate(pav.getValue());
        	if(attr.equals("ARRIVAL_B") || attr.equals("YEARTRANSF") || attr.equals("PROBYEAR"))
        		return getFormattedDate(date, "yyyy");
        	
       		return getFormattedDate(date);
        }
        
        if(attr.equals("PatCode"))
            return pav.getValue();
        

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
    	if(tr.getTest().getTestType().getDescription().equals(StandardObjects.getViralLoadTestType().getDescription()))
    		return getFormattedViralLoadLog10(tr.getValue());
    	else
    		return getFormattedDecimal(tr.getValue(),2);
    }
    
    protected String getFormattedViralLoadLog10(String value){
        double d = java.lang.Math.log10(parseValue(value));
        return getFormattedDecimal(d,2);
    }
    
    protected Table getArlEpidemiologyTable(List<Patient> patients){
    	Map<String, Integer> position = new HashMap<String, Integer>();
        List<Integer> length = new ArrayList<Integer>();
        int i=0;
        length.add(13); position.put("PatCode", i++);
        length.add(10); position.put("REF_LABO", i++);
        length.add(8);  position.put("TestResult.testDate", i++);
        length.add(8);  position.put("Patient.birthDate", i++);
        length.add(1);  position.put("Gender", i++);
        length.add(1);  position.put("HivType.TestResult.value", i++);
        length.add(0);  position.put("VL.TestResult.value", i++);
        length.add(3);  position.put("NATION", i++);
        length.add(3);  position.put("COUNTRY", i++);
        length.add(2);  position.put("RESID_B", i++);
        length.add(3);  position.put("ORIGIN", i++);
        length.add(4);  position.put("ARRIVAL_B", i++);
        length.add(1);  position.put("SEXCONTACT", i++);
        length.add(4);  position.put("SEXPARTNER", i++);
        length.add(3);  position.put("NATPARTNER", i++);
        length.add(1);  position.put("BLOODBORNE", i++);
        length.add(4);  position.put("YEARTRANSF", i++);
        length.add(3);  position.put("TRANCOUNTR", i++);
        length.add(1);  position.put("CHILD", i++);
        length.add(1);  position.put("PROFRISK", i++);
        length.add(4);  position.put("PROBYEAR", i++);
        length.add(3);  position.put("PROBCOUNTR", i++);
        length.add(4);  position.put("CD4.TestResult.value", i++);
        length.add(1);  position.put("STAD_CLIN", i++);
        length.add(1);  position.put("REASONTEST", i++);
        length.add(8);  position.put("FORM_OUT", i++);
        length.add(8);  position.put("FORM_IN", i++);
        length.add(3);  position.put("LABO", i);
        
        Table res = new Table();
        String [] row;
        
        for(Patient p : patients){
            
            TestResult tr;
            
            tr = getFirstTestResult(p, new TestType[]{WivObjects.getGenericwivConfirmation().getTestType()});
            if(tr != null){
                row = new String[position.size()];

                TestNominalValue tnv = tr.getTestNominalValue();
                if(tnv != null){
                    String hivTypeCode = getHivTypeCode(tnv.getValue());
                    if(hivTypeCode != null)
                        row[position.get("HivType.TestResult.value")] = hivTypeCode;
                    else
                        continue;
                }
                else
                    continue;
                
            	
	            row[position.get("Patient.birthDate")] = getFormattedDate(p.getBirthDate());
	            
	            tr = getFirstTestResult(p, new TestType[]{StandardObjects.getViralLoadTestType(),StandardObjects.getViralLoadLog10TestType()});
	            if(tr != null){
	            	row[position.get("VL.TestResult.value")] = getFormattedViralLoadResult(tr);
	                row[position.get("TestResult.testDate")] = getFormattedDate(tr.getTestDate());
	            }
	            
	            tr = getFirstTestResult(p, new TestType[]{StandardObjects.getCd4TestType()});
	            if(tr != null){
	            	row[position.get("CD4.TestResult.value")] = getFormattedDecimal(tr.getValue(),0);
	            }
	            else
	            	row[position.get("CD4.TestResult.value")] = "U";
	            
	
	            
	            for(PatientAttributeValue pav : p.getPatientAttributeValues()){
	                Integer pos = position.get(pav.getAttribute().getName());
	                if(pos != null){
	                    String s = null;
	
	                    s = getFormattedString(pav);
	                    if(row[pos] != null)
	                        s = row[pos] + s;
	 
	                    row[pos] = s;
	                }
	                
	            }
	            
	            //fill empty fields with question marks
	            for(int j=0; j<row.length; ++j){
	                if(row[j] == null){
	                    row[j] = getPadding(length.get(j));
	                }
	            }
	
	            ArrayList<String> lRow = new ArrayList<String>(Arrays.asList(row));
	            lRow.add("");
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
}
