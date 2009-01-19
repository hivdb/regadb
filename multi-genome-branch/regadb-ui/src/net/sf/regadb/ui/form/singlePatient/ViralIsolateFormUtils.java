package net.sf.regadb.ui.form.singlePatient;

import static eu.webtoolkit.jwt.WString.lt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.ui.framework.widgets.UIUtils;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import eu.webtoolkit.jwt.WTableCell;
import eu.webtoolkit.jwt.WText;

public class ViralIsolateFormUtils {
    public static void putResistanceTableResult(TestResult tr, final WTableCell cell, boolean onlyIfCurrentValueIsNA, final boolean canShowMutations)
    {
        //make sure we do not override a sensible value
        if(onlyIfCurrentValueIsNA) {
            if(!((WText)cell.children().get(0)).text().value().equals("NA"))
                return;
        }
        
        //JWT: Possible jwt problem
        while (cell.children().size() > 0) {
        	cell.removeWidget(cell.children().get(0));
        }
        
        final WText toReturn = new WText(lt(""));
        final WText mutation = new WText(lt(""));
        
        if(tr==null)
        {
            toReturn.setText(lt("NA"));
            cell.setStyleClass("resistance-NA");
        }
        else
        {
            ResistanceInterpretationParser inp = new ResistanceInterpretationParser() {
                @Override
                public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) {
                    mutations = combineMutations(mutations);
                    if(gss == 0.0)
                    {
                        toReturn.setText(lt("R"));
                        cell.setStyleClass("resistance-R");
                    }
                    else if(gss == 0.5 || gss == 0.75)
                    {
                        toReturn.setText(lt("I"));
                        cell.setStyleClass("resistance-I");
                    }
                    else if(gss == 1.0 || gss == 1.5)
                    {
                        toReturn.setText(lt("S"));
                        cell.setStyleClass("resistance-S");
                    }
                    else 
                    {
                        toReturn.setText(lt("Cannot interprete"));
                        cell.setStyleClass("resistance-X");
                    }
                    if(remarks!=null && !remarks.equals("null")) {
                    	cell.setStyleClass(cell.styleClass() + " resistance-remarks");
                        toReturn.setToolTip(lt(remarks));
                        cell.setToolTip(lt(remarks));
                    }
                    if(canShowMutations && mutations.size()>0) {
                        StringBuffer currentValue = new StringBuffer();
                        currentValue.append(" (");
                        for(String mut : mutations) {
                            currentValue.append(mut + " ");
                        }
                        currentValue.replace(currentValue.length()-1, currentValue.length(), ")");
                        mutation.setText(lt(currentValue.toString()));
                        mutation.setStyleClass("mutations");
                    }
                }
            };
            try {
                inp.parse(new InputSource(new ByteArrayInputStream(tr.getData())));
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cell.setStyleClass("resistance-cell " + cell.styleClass());
        cell.addWidget(toReturn);
        if (!UIUtils.keyOrValue(mutation.text()).equals("")) {
            cell.addWidget(mutation);
        }
    }
    
    public static String getFixedGenericId(TestResult tr) {
        String genericId = tr.getDrugGeneric().getGenericId();
        if(genericId.startsWith("APV"))
            return genericId.replace("APV", "FPV");
        else
            return genericId;
    }
    
    public static Genome getGenome(ViralIsolate vi){
        if(vi != null){
            Set<NtSequence> ntseqs = vi.getNtSequences();
            if(ntseqs != null && ntseqs.size() > 0){
                Set<AaSequence> aaseqs = ntseqs.iterator().next().getAaSequences();
                if(aaseqs != null && aaseqs.size() > 0){
                    return aaseqs.iterator().next().getProtein().getOpenReadingFrame().getGenome();
                }
            }
        }
        return null;
    }
    
    public static ArrayList<String> combineMutations(ArrayList<String> mutations){
        Map<String,StringBuilder> positions = new HashMap<String,StringBuilder>();
        
        for(String mut : mutations){
            StringBuilder pre = new StringBuilder();
            StringBuilder pos = new StringBuilder();
            StringBuilder suf = new StringBuilder();
            
            for(int i=0; i<mut.length(); ++i){
                char c = mut.charAt(i);
                if(Character.isDigit(c))
                    pos.append(c);
                else if(pos.length() > 0)
                    suf.append(c);
                else
                    pre.append(c);
            }
            
            if(pos.length() > 0){
                StringBuilder sb = positions.get(pos.toString());
                if(sb == null)
                    positions.put(pos.toString(), new StringBuilder(mut));
                else
                    sb.append(suf);
            }
        }
        
        ArrayList<String> r = new ArrayList<String>();
        for(Map.Entry<String, StringBuilder> pos : positions.entrySet())
            r.add(pos.getValue().toString());
        return r;
    }
}
