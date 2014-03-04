package net.sf.regadb.ui.form.singlePatient;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ResistanceInterpretationTemplate;
import net.sf.regadb.db.SettingsUser;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.UserAttribute;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.form.singlePatient.chart.Chart;
import net.sf.regadb.ui.form.singlePatient.chart.DefaultChart;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import eu.webtoolkit.jwt.SelectionMode;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WAnchor;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMemoryResource;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPainter;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WRasterPaintDevice;
import eu.webtoolkit.jwt.WSelectionBox;
import eu.webtoolkit.jwt.WString;

public class ViralIsolateReportForm extends WContainerWidget
{
    private ViralIsolateForm viralIsolateForm_;
    
    private FormTable reportTable_;
    private Label algorithmL_;
    private WSelectionBox algorithmCB_;
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
        reportTable_ = new FormTable(this);
        algorithmL_ = new Label(tr("form.viralIsolate.editView.report.algorithm"));
        algorithmCB_ = new WSelectionBox(viralIsolateForm_);
        algorithmCB_.setSelectionMode(SelectionMode.ExtendedSelection);
        reportTable_.addLineToTable(algorithmL_, algorithmCB_);
        templateL_ = new Label(tr("form.viralIsolate.editView.report.template"));
        resRepTemplateCB_ = new ComboBox<ResistanceInterpretationTemplate>(InteractionState.Editing, viralIsolateForm_);
        reportTable_.addLineToTable(templateL_, resRepTemplateCB_);
        Label generateLabel = new Label(tr("form.viralIsolate.editView.report.label.generate"));
        generateButton_ = new WPushButton(tr("form.viralIsolate.editView.report.generateButton"));
        reportTable_.addLineToTable(generateLabel, generateButton_);
        reportL = new Label(tr("form.viralIsolate.editView.report.report"));
        reportA_ = new WAnchor("dummy", "");
        reportA_.setStyleClass("link");
        reportTable_.addLineToTable(reportL, reportA_);
        
        generateButton_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
        {
            public void trigger(WMouseEvent a) 
            {
                
                if( resRepTemplateCB_.currentItem() != null){
                    Transaction t = RegaDBMain.getApp().createTransaction();
                    Patient patient = RegaDBMain.getApp().getTree().getTreeContent().patientTreeNode.getSelectedItem();
                    File chartFile = getChart(t, patient);
                    try {
	                    GenerateReport report = new GenerateReport(resRepTemplateCB_.currentValue().getDocument(),
	                                                               viralIsolateForm_.getViralIsolate(),
	                                                               patient,
	                                                               getAlgorithms(),
	                                                               ViralIsolateResistanceForm.getRelevantDrugClassIds(t, viralIsolateForm_.getViralIsolate().getViralIsolateIi()),
	                                                               t,
	                                                               chartFile
	                                                               );
	                    
	                    
	                    String fileName = "";
	                    if (viralIsolateForm_.getViralIsolate().getSampleId() != null)
	                    	fileName += viralIsolateForm_.getViralIsolate().getSampleId() + "_";
	                    fileName += resRepTemplateCB_.currentValue().getName().replace(' ', '_')+".rtf";
	                    
	                    reportA_.setText(fileName);
	                    WMemoryResource memResource = new WMemoryResource("application/rtf");
	                    memResource.setData(report.getReport());
	                    memResource.suggestFileName(fileName);
	                    reportA_.setResource(memResource); 
	                    t.commit();
                    } finally {
                    	chartFile.delete();
                    }
                }
            }
        });
    }
    
    private Collection<String> getAlgorithms(){
    	Set<String> algorithms = new TreeSet<String>();
    	if(algorithmCB_.getSelectionMode() == SelectionMode.ExtendedSelection){
	    	for(int i : algorithmCB_.getSelectedIndexes()){
	    		algorithms.add(((WString)algorithmCB_.getModel().getData(i, 0)).getValue());
	    	}
    	}
    	else
    		algorithms.add(algorithmCB_.getCurrentText().getValue());
    	return algorithms;
    }
    
    private void filldata()
    {
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        Genome genome = viralIsolateForm_.getViralIsolate().getGenome();
        if(genome != null){
            TestType testType = t.getTestType(StandardObjects.getGssTestType(genome));
            if(testType != null){
                for(Test test : t.getTests(testType))
                {
                    algorithmCB_.addItem(test.getDescription());
                }
//                algorithmCB_.sort();
            } else {
            	algorithmCB_.setHidden(true);
            }
                
            for(ResistanceInterpretationTemplate rit : t.getResRepTemplates())
            {
            	resRepTemplateCB_.addItem(new DataComboMessage<ResistanceInterpretationTemplate>(rit, rit.getName()));
            }
            resRepTemplateCB_.sort();
        }
        
        t.commit();
    }
    
    private File getChart(Transaction t, Patient patient)
    {
		int width = 700;
		int height = 500;
		
		SettingsUser su = RegaDBMain.getApp().getSettingsUser();
		if (su != null) {
	        for(UserAttribute ua : su.getUserAttributes()) {
	            if("chart.width".equals(ua.getName()) && ua.getValue()!=null && !ua.getValue().equals(""))
	            	width = Integer.parseInt(ua.getValue());
	            else if("chart.height".equals(ua.getName()) && ua.getValue()!=null && !ua.getValue().equals(""))
	                height = Integer.parseInt(ua.getValue());
	        }
		}
		
    	Chart chart = DefaultChart.createDefaultChart(null, patient, null, null, width, height);
    	
    	try {
    		File f = RegaDBMain.getApp().createTempFile("regadb-chart", ".png");
	    	FileOutputStream fos = new FileOutputStream(f);
	    	
			WRasterPaintDevice image = new WRasterPaintDevice("png", chart.getWidth(), chart.getHeight());
			
			WPainter painter = new WPainter(image);
			chart.paint(painter);
			painter.end();
			
			image.write(fos);
			fos.flush();
			fos.close();
			
			return f;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
}
