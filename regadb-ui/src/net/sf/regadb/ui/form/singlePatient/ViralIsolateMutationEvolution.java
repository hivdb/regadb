package net.sf.regadb.ui.form.singlePatient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.regadb.analysis.functions.MutationHelper;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.regadb.util.date.DateUtils;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

public class ViralIsolateMutationEvolution extends FormWidget {
    private Patient patient_;
    
    private WTable viralIsolatesTable_;
    
    
    public ViralIsolateMutationEvolution(WMessage formName, Patient patient) {
        super(formName, InteractionState.Viewing);
        patient_ = patient;

        init();
    }
    
    public void init() {
        viralIsolatesTable_ = new WTable(this);
        viralIsolatesTable_.setStyleClass("vi-mutation-evolution-table");
        
        String [] headers = {"form.viralIsolate.evolution.mutation.table.header.sampleIdDate",
                             "form.viralIsolate.evolution.mutation.table.header.protein",
                             "form.viralIsolate.evolution.mutation.table.header.region",
                             "form.viralIsolate.evolution.mutation.table.header.mutations",
                             "form.viralIsolate.evolution.mutation.table.header.changes"};
        
        for(String header : headers) {
            viralIsolatesTable_.putElementAt(0, viralIsolatesTable_.numColumns(), new TableHeader(tr(header)));
        }
        
        ViralIsolate[] vis = new ViralIsolate[patient_.getViralIsolates().size()];
        vis = patient_.getViralIsolates().toArray(vis);
        
        int rowCounter = 1;
        WText sampleId;
        WText protein;
        WText region;
        WText mutations;
        WText changes;
        
        List<AaSequence> aaseqs;
        for(int i = 0; i<vis.length; i++) {
            aaseqs = getAaSeqsForViralIsolateSortedByProtein(vis[i]);
            
            sampleId = new WText();
            sampleId.setText(lt(vis[i].getSampleId() + "<br>" + DateUtils.getEuropeanFormat(vis[i].getSampleDate())));
            viralIsolatesTable_.putElementAt(rowCounter, 0, sampleId);
            viralIsolatesTable_.elementAt(rowCounter, 0).setRowSpan(aaseqs.size());
            viralIsolatesTable_.elementAt(rowCounter, 0).setStyleClass("table-cell-center");
            
            for(AaSequence aaseq : getAaSeqsForViralIsolateSortedByProtein(vis[i])) {
                protein = new WText();
                protein.setText(lt(aaseq.getProtein().getAbbreviation()));
                viralIsolatesTable_.putElementAt(rowCounter, 1, protein);
                viralIsolatesTable_.elementAt(rowCounter, 1).setStyleClass("table-cell-center");
                
                region = new WText();
                region.setText(lt(aaseq.getFirstAaPos() + " - " + aaseq.getLastAaPos()));
                viralIsolatesTable_.putElementAt(rowCounter, 2, region);
                viralIsolatesTable_.elementAt(rowCounter, 2).setStyleClass("table-cell-center");
                
                mutations = new WText();
                mutations.setText(lt(MutationHelper.getWildtypeMutationList(aaseq)));
                viralIsolatesTable_.putElementAt(rowCounter, 3, mutations);
                //viralIsolatesTable_.elementAt(rowCounter, 3).setStyleClass("table-cell-center");
                
                changes = new WText();
                String changesS = "";
                if(i-1>=0) {
                    changesS = diff(aaseq, vis[i-1]);
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