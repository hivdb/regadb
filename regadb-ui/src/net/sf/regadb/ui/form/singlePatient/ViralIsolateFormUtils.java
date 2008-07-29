package net.sf.regadb.ui.form.singlePatient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.witty.wt.WTableCell;
import net.sf.witty.wt.WText;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import static net.sf.witty.wt.WResource.lt;

public class ViralIsolateFormUtils {
    public static void putResistanceTableResult(TestResult tr, final WTableCell cell, boolean onlyIfCurrentValueIsNA, final boolean canShowMutations)
    {
        //make sure we do not override a sensible value
        if(onlyIfCurrentValueIsNA) {
            if(!((WText)cell.children().get(0)).text().value().equals("NA"))
                return;
        }
        
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
                        toReturn.setToolTipMessage(lt(remarks));
                        cell.setToolTipMessage(lt(remarks));
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
        if (!mutation.text().keyOrValue().equals("")) {
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
}
