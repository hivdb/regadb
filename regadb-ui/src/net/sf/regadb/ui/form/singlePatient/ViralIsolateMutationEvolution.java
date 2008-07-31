package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.regadb.analysis.functions.MutationHelper;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class ViralIsolateMutationEvolution extends FormWidget {
    private Patient patient_;
    
    private SimpleTable viralIsolatesTable_;
    
    
    public ViralIsolateMutationEvolution(WMessage formName, boolean literal, Patient patient) {
        super(formName, InteractionState.Viewing, literal);
        patient_ = patient;

        init();
    }
    
    public void init() {
        viralIsolatesTable_ = new SimpleTable(this);
        viralIsolatesTable_.setStyleClass(viralIsolatesTable_.styleClass() + " viral-isolate-table");
        
        
        viralIsolatesTable_.setHeaders(tr("viralIsolate.mutation.sample"),
        		tr("viralIsolate.protein"),
                tr("viralIsolate.mutation.region"),
                tr("viralIsolate.mutation.plural"),
                tr("viralIsolate.mutation.changes"));
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
            sampleId.setText(lt(vis.get(i).getSampleId() + "<br>" + DateUtils.getEuropeanFormat(vis.get(i).getSampleDate())));
            viralIsolatesTable_.putElementAt(rowCounter, 0, sampleId);
            viralIsolatesTable_.elementAt(rowCounter, 0).setRowSpan(Math.max(aaseqs.size(),1));
            
            for(AaSequence aaseq : getAaSeqsForViralIsolateSortedByProtein(vis.get(i))) {
                protein = new WText();
                protein.setText(lt(aaseq.getProtein().getAbbreviation()));
                viralIsolatesTable_.putElementAt(rowCounter, 1, protein);
                
                region = new WText();
                region.setText(lt(aaseq.getFirstAaPos() + " - " + aaseq.getLastAaPos()));
                viralIsolatesTable_.putElementAt(rowCounter, 2, region);
                
                mutations = new WText();
                mutations.setText(lt(MutationHelper.getWildtypeMutationList(aaseq)));
                viralIsolatesTable_.putElementAt(rowCounter, 3, mutations);
                
                changes = new WText();
                String changesS = "";
                if(i-1>=0) {
                    changesS = diff(aaseq, vis.get(i-1));
                }
                changes.setText(lt(changesS));
                viralIsolatesTable_.putElementAt(rowCounter, 4, changes);
                
                rowCounter++;
            }
        }
        
        addControlButtons();
    }
    
    private String diff(AaSequence aaseq, ViralIsolate vi) {
        List<AaSequence> aaseqs = getAaSeqsForViralIsolateSortedByProtein(vi);
        String result;
        for(AaSequence s : aaseqs) {
            result = MutationHelper.getMutationDifferenceList(s, aaseq);
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
    public WMessage deleteObject() {
        return null;
    }

    @Override
    public void redirectAfterDelete() {
    }

    @Override
    public void saveData() {
    }
}
