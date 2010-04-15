package net.sf.regadb.ui.form.singlePatient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WComboBox;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class ViralIsolateTransmittedResistanceForm extends WContainerWidget {
    private ViralIsolateForm viralIsolateForm_;
    
    private WPushButton refreshButton_;
    private WComboBox algorithmCB;
    private WText interpretation;
    
    public ViralIsolateTransmittedResistanceForm(ViralIsolateForm viralIsolateForm) {
    	super();
    	
    	viralIsolateForm_ = viralIsolateForm;
    	
    	init();
    }
    
    private void init() {
	    WTable wrapper = new SimpleTable(this);
	    wrapper.getElementAt(0, 1).setStyleClass("navigation");
	    
	    refreshButton_ = new WPushButton(tr("form.viralIsolate.editView.resistance.refreshButton"), wrapper.getElementAt(0, 1));
	    refreshButton_.clicked().addListener(this, new Signal1.Listener<WMouseEvent>()
	            {
	                public void trigger(WMouseEvent a) 
	                {
	                	fillComboBox();
	                }
	            });
	    
	    Label algorithmL = new Label(tr("form.viralIsolate.editView.resistance.algorithm"));
	    wrapper.getElementAt(0, 0).addWidget(algorithmL);
	    algorithmCB = new WComboBox(wrapper.getElementAt(0, 0));
	    algorithmL.setBuddy(algorithmCB);
	    algorithmCB.changed().addListener(this, new Signal.Listener(){
			public void trigger() {
				showInterpretation();
			}
	    });
	    
	    interpretation = new WText(wrapper.getElementAt(1, 0));
	    
	    fillComboBox();
    }
    
    private void fillComboBox() {
    	algorithmCB.setEnabled(true);
    	
        Transaction t = RegaDBMain.getApp().createTransaction();
        t.refresh(viralIsolateForm_.getViralIsolate());

        Set<String> algorithms = new TreeSet<String>();
        for (TestResult tr : viralIsolateForm_.getViralIsolate().getTestResults()) {
        	if (tr.getTest().getTestType().getDescription().equals(StandardObjects.getTDRDescription())) {
        		algorithms.add(tr.getTest().getDescription());
        	}
        }

        WString formerSelection = algorithmCB.getCurrentText();
        algorithmCB.clear();
        for (String a : algorithms) {
        	algorithmCB.addItem(a);
        }
        
        if (formerSelection != null && algorithmCB.getCount() > 0) {
	        for (int i = 0; i < algorithmCB.getCount(); i++) {
	        	if (algorithmCB.getItemText(i).equals(formerSelection)) {
	        		algorithmCB.setCurrentIndex(i);
	        		break;
	        	}
	        }
	        showInterpretation();
        } else if (algorithmCB.getCount() > 0){
        	algorithmCB.setCurrentIndex(0);
        	showInterpretation();
        } else {
        	algorithmCB.setEnabled(false);
        }
        
        t.commit();
    }
    
	private void showInterpretation() {
		Transaction t = RegaDBMain.getApp().createTransaction();
		
		final StringBuffer interpretations = new StringBuffer();
		
		Collection<String> drugClasses = 
			ViralIsolateResistanceForm.getRelevantDrugClassIds(t, viralIsolateForm_.getViralIsolate().getViralIsolateIi());
		for (String dc : drugClasses) {
			for (TestResult tr : viralIsolateForm_.getViralIsolate().getTestResults()) {
				if (tr.getDrugGeneric().getDrugClass().getClassId().equals(dc)
						&&tr.getTest().getTestType().getDescription().equals(StandardObjects.getTDRDescription())) {
					interpretations.append("<b>" + dc + ":</b> ");
					
					ResistanceInterpretationParser parser = new ResistanceInterpretationParser() {
						@Override
						public void completeScore(String drug, int level, double gss,
								String description, char sir, ArrayList<String> mutations,
								String remarks) {
							interpretations.append(description);
							if (mutations.size() != 0) {
								interpretations.append(" (");
							
								for (String m : mutations) {
									interpretations.append(m);
									interpretations.append(" ");
								}
								interpretations.replace(interpretations.length() - 1 , interpretations.length(), ")");
							}
						}
					};
					try {
						parser.parse(new InputSource(new ByteArrayInputStream(tr.getData())));
					} catch (SAXException e) {
						System.err.println("Parsing of resistance test failed");
					} catch (IOException e) {
						System.err.println("Parsing of resistance test failed");
					}
					
					interpretations.append("<br/>");
				}
			}
		}
		
		interpretation.setText(interpretations.toString());
	}
}
