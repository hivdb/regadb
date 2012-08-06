package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.tools.MutationHelper;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.util.date.DateUtils;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WText;

public class ViralIsolateMutationEvolution extends FormWidget {
    private Patient patient_;
    
    private SimpleTable viralIsolatesTable_;
    
    
    public ViralIsolateMutationEvolution(WString formName, Patient patient) {
        super(formName, InteractionState.Viewing);
        patient_ = patient;

        init();
    }
    
    public void init() {
        viralIsolatesTable_ = new SimpleTable(this);
        viralIsolatesTable_.setStyleClass(viralIsolatesTable_.getStyleClass() + " viral-isolate-table");
        
        
        viralIsolatesTable_.setHeaders(tr("form.viralIsolate.evolution.mutation.table.header.sampleIdDate"),
        		tr("form.viralIsolate.evolution.mutation.table.header.protein"),
                tr("form.viralIsolate.evolution.mutation.table.header.region"),
                tr("form.viralIsolate.evolution.mutation.table.header.mutations"),
                tr("form.viralIsolate.evolution.mutation.table.header.changes"));
        viralIsolatesTable_.setWidths(15,10,10,45,20);
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        
        List<ViralIsolate> vis = t.getViralIsolatesSortedOnDate(patient_);
        
        int rowCounter = 1;
        WText sampleId;
        WText protein;
        WText region;
        WText mutations;
        WText changes;
        
        List<AaSequence> aaseqs;
        for(int i = 0; i<vis.size(); i++) {
            aaseqs = getAaSeqsForViralIsolateSortedByProtein(vis.get(i));
            
            sampleId = new WText();
            sampleId.setText(vis.get(i).getSampleId() + "<br/>" + DateUtils.format(vis.get(i).getSampleDate()));
            viralIsolatesTable_.getElementAt(rowCounter, 0).addWidget(sampleId);
            viralIsolatesTable_.getElementAt(rowCounter, 0).setRowSpan(Math.max(aaseqs.size(),1));
            
            for(AaSequence aaseq : getAaSeqsForViralIsolateSortedByProtein(vis.get(i))) {
                protein = new WText();
                protein.setText(aaseq.getProtein().getAbbreviation());
                viralIsolatesTable_.getElementAt(rowCounter, 1).addWidget(protein);
                
                region = new WText();
                region.setText(aaseq.getFirstAaPos() + " - " + aaseq.getLastAaPos());
                viralIsolatesTable_.getElementAt(rowCounter, 2).addWidget(region);
                
                mutations = new WText();
                mutations.setText(MutationHelper.getWildtypeMutationList(aaseq));
                viralIsolatesTable_.getElementAt(rowCounter, 3).addWidget(mutations);
                
                changes = new WText();
                String changesS = "";
                if(i-1>=0) {
                    changesS = diff(aaseq, vis.get(i-1));
                }
                changes.setText(changesS);
                viralIsolatesTable_.getElementAt(rowCounter, 4).addWidget(changes);
                
                rowCounter++;
            }
        }
        
        addControlButtons();
    }
    
    private String diff(AaSequence aaseq, ViralIsolate vi) {
        List<AaSequence> aaseqs = getAaSeqsForViralIsolateSortedByProtein(vi);
        String result;
        for(AaSequence s : aaseqs) {
            result = MutationHelper.getAaMutationDifferenceList(s, aaseq);
            if(result!=null) {
                return result;
            }
        }
        return "";
    }
    
    private List<AaSequence> getAaSeqsForViralIsolateSortedByProtein(ViralIsolate vi) {
        List<AaSequence> aaseqs = new ArrayList<AaSequence>();
        
        for(NtSequence ntseq : vi.getNtSequences()) {
            for(AaSequence aaseq : ntseq.getAaSequences()) {
                aaseqs.add(aaseq);
            }
        }
        
        Collections.sort(aaseqs, new Comparator<AaSequence>() {
            public int compare(AaSequence s1, AaSequence s2) {
                return s1.getProtein().getAbbreviation().compareTo(s2.getProtein().getAbbreviation());
            }
        });
        
        return aaseqs;
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

	@Override
	public void redirectAfterSave() {
	}

	@Override
	public void redirectAfterCancel() {
	}
}
