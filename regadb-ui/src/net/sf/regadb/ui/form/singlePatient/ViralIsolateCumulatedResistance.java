package net.sf.regadb.ui.form.singlePatient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.sequencedb.SequenceUtils;
import net.sf.regadb.service.wts.ViralIsolateAnalysisHelper;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;

public class ViralIsolateCumulatedResistance extends FormWidget
{
    private ViralIsolateResistanceTable resistanceTable_;
    private WCheckBox showMutations_;
    private WCheckBox showAllAlgorithms_;
    
    private Collection<String> drugClasses = null;
    private TestType gssTestType = null;
    private ViralIsolate combinedIsolate = null;
    private Set<TestResult> cumulatedTestResults = new HashSet<TestResult>();
    private Set<Integer> loadedTestIis = new TreeSet<Integer>();
    
    private Patient patient_;
    
    public ViralIsolateCumulatedResistance(WString formName, Patient patient) {
        super(formName, InteractionState.Viewing);
        patient_ = patient;
        
        init();
    }

    public void init()
    {
        WTable wrapper = new SimpleTable(this);
        wrapper.getElementAt(0, 0).setStyleClass("navigation");
        wrapper.getElementAt(1, 0).setStyleClass("tablewrapper");
        resistanceTable_ = new ViralIsolateResistanceTable(wrapper.getElementAt(1, 0));
        new ViralIsolateResistanceLegend(wrapper.getElementAt(2, 0));
        showMutations_ = new WCheckBox(tr("form.viralIsolate.cumulatedResistance.showMutationsCB"), wrapper.getElementAt(0, 0));
        showMutations_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        
        showAllAlgorithms_ = new WCheckBox(tr("form.viralIsolate.cumulatedResistance.showAllAlgorithmsCB"), wrapper.getElementAt(0, 0));
        showAllAlgorithms_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
                {
                    public void trigger(WMouseEvent a)
                    {
                        refreshTable();
                    }
                });
        showAllAlgorithms_.setHidden(RegaDBSettings.getInstance().getInstituteConfig().getViralIsolateFormConfig().getAlgorithms() == null);
        
        refreshTable();
    }
    
    @SuppressWarnings("unchecked")
	private void loadTestResults() {
        Transaction t =  RegaDBMain.getApp().createTransaction();
        
        if(drugClasses == null){
	        List<String> proteins = t.createQuery("select distinct(p.abbreviation)" +
	        		" from AaSequence aas join aas.protein p join aas.ntSequence nt" +
	        		" where nt.viralIsolate.patient.id="+ patient_.getPatientIi()).list();
	        drugClasses = ViralIsolateFormUtils.getRelevantDrugClassIds(proteins);
        }
        
        if(combinedIsolate == null){
	        Set<ViralIsolate> vis = patient_.getViralIsolates();
	        combinedIsolate = SequenceUtils.combineViralIsolates(vis);
        }
        
        if(gssTestType == null){
            Genome genome = ((ViralIsolate)(patient_.getViralIsolates().toArray()[0])).getGenome();
    		gssTestType = (genome == null ? null : StandardObjects.getTestType(StandardObjects.getGssDescription(),genome));
        }
        
        for(Test test : resistanceTable_.getAlgorithms(t, gssTestType, showAllAlgorithms_.isChecked())) {
        	if(loadedTestIis.add(test.getTestIi())){
	            try {
					cumulatedTestResults.addAll(runViralIsolateResistanceTest(t, combinedIsolate, test));
				} catch (SAXException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        }

        t.commit();
    }
        
    private void refreshTable(){
    	loadTestResults();
        resistanceTable_.loadTable(drugClasses, showMutations_.isChecked(), showAllAlgorithms_.isChecked(), cumulatedTestResults, gssTestType);
    }
    
    private List<TestResult> runViralIsolateResistanceTest(final Transaction t, final ViralIsolate isolate, final Test test) throws SAXException, IOException{
    	final List<TestResult> testResults = new ArrayList<TestResult>();
    	
        byte[] result = ViralIsolateAnalysisHelper.runMutlist(isolate, test, 200);
    	
    	ResistanceInterpretationParser inp = new ResistanceInterpretationParser()
        {
            @Override
            public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) 
            {
                TestResult resistanceInterpretation = new TestResult();
                resistanceInterpretation.setDrugGeneric(t.getDrugGeneric(drug));
                resistanceInterpretation.setValue(gss+"");
                resistanceInterpretation.setTestDate(new Date(System.currentTimeMillis()));
                resistanceInterpretation.setTest(test);
                
                StringBuffer data = new StringBuffer();
                data.append("<interpretation><score><drug>");
                data.append(drug);
                data.append("</drug><level>");
                data.append(level);
                data.append("</level><description>");
                data.append(description);
                data.append("</description><sir>");
                data.append(sir);
                data.append("</sir><gss>");
                data.append(gss);
                data.append("</gss><mutations>");
                int size = mutations.size();
                for(int i = 0; i<size; i++)
                {
                    data.append(mutations.get(i));
                    if(i!=size-1)
                        data.append(' ');
                }
                data.append("</mutations><remarks>");
                data.append(remarks);
                data.append("</remarks></score></interpretation>");
                resistanceInterpretation.setData(data.toString().getBytes());
                
                testResults.add(resistanceInterpretation);
            }
        };
        
        inp.parse(new InputSource(new ByteArrayInputStream(result)));
        return testResults;
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
}

