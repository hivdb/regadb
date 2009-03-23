package net.sf.regadb.ui.form.singlePatient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.regadb.analysis.functions.GenerateReport;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.singlePatient.chart.PatientChart;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WMemoryResource;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;

public class ViralIsolateReportForm extends WContainerWidget
{
    private ViralIsolateForm viralIsolateForm_;
    
    private WGroupBox reportGroup_;
    private FormTable reportTable_;
    private Label algorithmL_;
    private ComboBox<Test> algorithmCB_;
    private Label templateL_;
    private ComboBox<ResistanceInterpretationTemplate> resRepTemplateCB_;
    private WPushButton generateButton_;
    private Label reportL;
    private WAnchor reportA_;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public ViralIsolateReportForm(ViralIsolateForm viralIsolateForm)
    {
        super();
        viralIsolateForm_ = viralIsolateForm;

        init();
        
        filldata();
    }
    
    public void init()
    {
        reportGroup_ = new WGroupBox(tr("form.viralIsolate.editView.group.report"), this);
        reportGroup_.setStyleClass("groupbox");
        reportTable_ = new FormTable(reportGroup_);
        algorithmL_ = new Label(tr("form.viralIsolate.editView.report.algorithm"));
        algorithmCB_ = new ComboBox<Test>(InteractionState.Editing, viralIsolateForm_);
        reportTable_.addLineToTable(algorithmL_, algorithmCB_);
        templateL_ = new Label(tr("form.viralIsolate.editView.report.template"));
        resRepTemplateCB_ = new ComboBox<ResistanceInterpretationTemplate>(InteractionState.Editing, viralIsolateForm_);
        reportTable_.addLineToTable(templateL_, resRepTemplateCB_);
        Label generateLabel = new Label(tr("form.viralIsolate.editView.report.label.generate"));
        generateButton_ = new WPushButton(tr("form.viralIsolate.editView.report.generateButton"));
        reportTable_.addLineToTable(generateLabel, generateButton_);
        reportL = new Label(tr("form.viralIsolate.editView.report.report"));
        reportA_ = new WAnchor("dummy", lt(""));
        reportA_.setStyleClass("link");
        reportTable_.addLineToTable(reportL, reportA_);
        
        generateButton_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
        {
            public void trigger(WMouseEvent a) 
            {
                
                if( resRepTemplateCB_.currentItem() != null){
                    Transaction t = RegaDBMain.getApp().createTransaction();
                    Patient patient = RegaDBMain.getApp().getTree().getTreeContent().patientSelected.getSelectedItem();
                    File chartFile = getChart(t, patient);
                    GenerateReport report = new GenerateReport(resRepTemplateCB_.currentValue().getDocument(),
                                                               viralIsolateForm_.getViralIsolate(),
                                                               patient,
                                                               algorithmCB_.currentValue(),
                                                               t,
                                                               chartFile
                                                               );
                    
                    String fileName = viralIsolateForm_.getViralIsolate().getSampleId() + "_" +algorithmCB_.currentValue().getDescription()+"_"+resRepTemplateCB_.currentValue().getName().replace(' ', '_')+".rtf";
                    
                    reportA_.setText(lt(fileName));
                    WMemoryResource memResource = new WMemoryResource("application/rtf");
                    memResource.setData(report.getReport());
                    memResource.suggestFileName(fileName);
                    reportA_.setResource(memResource);
                    chartFile.delete();
                    t.commit();
                }
            }
        });
    }
    
    private void filldata()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Genome genome = ViralIsolateFormUtils.getGenome(viralIsolateForm_.getViralIsolate());
        if(genome != null){
            TestType testType = t.getTestType(StandardObjects.getGssTestType(genome));
            if(testType != null){
                for(Test test : t.getTests(testType))
                {
                    algorithmCB_.addItem(new DataComboMessage<Test>(test, test.getDescription()));
                }
                algorithmCB_.sort();
                
                for(ResistanceInterpretationTemplate rit : t.getResRepTemplates())
                {
                    resRepTemplateCB_.addItem(new DataComboMessage<ResistanceInterpretationTemplate>(rit, rit.getName()));
                }
                resRepTemplateCB_.sort();
            }
        }
        
        if(algorithmCB_.size() == 0){
            algorithmCB_.setEnabled(false);
            resRepTemplateCB_.setEnabled(false);
            generateButton_.setEnabled(false);
        }
        
        t.commit();
    }
    
    private File getChart(Transaction t, Patient patient)
    {
        PatientChart chartDrawer = new PatientChart(patient, t.getSettingsUser());
        chartDrawer.setSettings(700, 300, algorithmCB_.currentValue());
        t.attach(patient);
        File tmpFile = RegaDBMain.getApp().createTempFile("regadb-chart", ".png");
        chartDrawer.writePngChartToFile(800, tmpFile);
        
        return tmpFile;
    }
}
