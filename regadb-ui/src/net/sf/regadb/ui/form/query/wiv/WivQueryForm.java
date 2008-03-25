package net.sf.regadb.ui.form.query.wiv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DatasetAccess;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.exportCsv.ExportToCsv;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.DateField;
import net.sf.regadb.ui.framework.forms.fields.IFormField;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WAnchor;
import net.sf.witty.wt.WFileResource;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.i8n.WMessage;

import org.hibernate.Query;

//public abstract class WivQueryForm extends QueryDefinitionRunForm implements SignalListener<WMouseEvent>{
public abstract class WivQueryForm extends FormWidget implements SignalListener<WMouseEvent>{
    private WGroupBox generalGroup_;
    private WGroupBox parameterGroup_;
    
    private WTable generalTable_;
    private WTable parameterTable_;
    
    private Label description_;
    private Label status_;
    
    private WAnchor link_;
    private WPushButton run_;
    
    private String query_;
    private String filename_;
    
    private SimpleDateFormat sdf_ = new SimpleDateFormat("yyyy-MM-dd");
    
    private HashMap<String,IFormField> parameters_ = new HashMap<String,IFormField>();
    
    public WivQueryForm(WMessage formName, WMessage description, WMessage filename){
        super(formName,InteractionState.Viewing);
        
        filename_ = filename.value();
        description_ = new Label(description);
        
//        QueryDefinition qd = new QueryDefinition();
//        qd.setName(formName.value());
//        qd.setDescription(description.value());
//        
//        QueryDefinitionParameter qdp = new QueryDefinitionParameter();
//        qdp.setName(tr("form.query.wiv.label.startDate").value());
//        QueryDefinitionParameterType qdpt = new QueryDefinitionParameterType();
//        qdpt.setId(QueryDefinitionParameterTypes.DATE.getValue());
//        qdp.setQueryDefinitionParameterType(qdpt);
//        
//        qd.getQueryDefinitionParameters().add(qdp);
//        
//        QueryDefinitionRun qdr = new QueryDefinitionRun();
//        
//        QueryDefinitionRunParameter qdrp = new QueryDefinitionRunParameter();
//        qdrp.setQueryDefinitionParameter(qdp);
//        
//        qdr.getQueryDefinitionRunParameters().add(qdrp);
//        
//        qdr.setName(formName.value());
//        qdr.setQueryDefinition(qd);
//        
//        
//        super.setQueryDefinitionRun(qdr);
//        
//        super.init();
//        
//        super.fillData();
//        
//        super.addControlButtons();
        
        init();
    }
    
    public void init(){
        generalGroup_   = new WGroupBox(tr("form.query.definition.run.general"), this);
        parameterGroup_     = new WGroupBox(tr("form.query.definition.run.parameters"), this);
        
        generalTable_ = new WTable(generalGroup_);
        parameterTable_ = new WTable(parameterGroup_);
        
        link_ = new WAnchor("dummy", lt(""));
        run_ = new WPushButton(tr("form.query.wiv.pushbutton.run"));

        generalTable_.putElementAt(0, 0, description_);
        generalTable_.putElementAt(1, 0, run_);
        generalTable_.putElementAt(2, 0, link_);
        
        run_.clicked.addListener(this);
        
    }
    
    public void notify(WMouseEvent a) 
    {
        if(getQuery() != null){
            
            File wivDir = new File(RegaDBSettings.getInstance().getPropertyValue("regadb.query.resultDir") + File.separatorChar + "wiv");
            if(!wivDir.exists()){
                wivDir.mkdir();
            }
            File csvFile =  new File(wivDir.getAbsolutePath() + File.separatorChar + filename_ + ".csv");
            
            if(process(csvFile)){
                File output = postProcess(csvFile);
                
                link_.label().setText(lt("Download Query Result [" + new Date(System.currentTimeMillis()).toString() + "]"));
                link_.setRef(new WFileResource("text/csv", output.getAbsolutePath()).generateUrl());
            }
        }
    }
    
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
    
    protected String getCentreName(){
        return RegaDBSettings.getInstance().getPropertyValue("centre.name");
    }
    
    protected String getPatCode(Date birthDate, String firstName, String lastName, String gender){
        String res="";
        
        if(birthDate != null){
            res = getFormattedDate(birthDate);
        }
        else{
            res = "????????";
        }
        
        res += getFormattedLastName(lastName);
        
        if(gender != null){
            if(gender.equals("male"))
                res += "M";
            else if(gender.equals("female"))
                res += "F";
            else
                res += "?";
        }
        else
            res += "?";

        return res;
    }
    
    private String[] specialPrefixes = {"VAN","DE","DU","DES","LA","LE"}; 
    
    protected String getFormattedLastName(String name){
        if(name != null){
            name = name.toUpperCase();
            
            if(name.length() > 2){
                boolean found=false;
                for(String pfx : specialPrefixes){
                    if(name.startsWith(pfx)){
                        
                        
                        found=true;
                    }
                }
                
                
                return name;
            }
            else
                return name;
        }
        else
            return "??";
    }
    
    protected String getFormattedDate(Date date){
        if(date != null)
            return (new SimpleDateFormat("yyyyMMdd")).format(date);
        else
            return "????????";
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
        String number = value.replace("<", "");
        number = number.replace("=", "");
        return Double.parseDouble(number.replace(">", ""));
    }
    
    protected String getFormattedDecimal(String value){
        return value.replace('.', ',');
    }
    
    protected String getFormattedDecimal(double value){
        String s = value +"";
        return getFormattedDecimal(s);
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
}
