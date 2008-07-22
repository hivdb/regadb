package net.sf.regadb.ui.form.singlePatient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.witty.wt.WBorder;
import net.sf.witty.wt.WColor;
import net.sf.witty.wt.WTableCell;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WBorder.Style;
import net.sf.witty.wt.WBorder.Width;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;

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
        
        if(tr==null)
        {
            toReturn.setText(lt("NA"));
            cell.decorationStyle().setBackgroundColor(WColor.black);
            cell.decorationStyle().setForegroundColor(WColor.white);
            toReturn.decorationStyle().setBackgroundColor(WColor.black);
            toReturn.decorationStyle().setForegroundColor(WColor.white);
        }
        else
        {
            ResistanceInterpretationParser inp = new ResistanceInterpretationParser() {
                @Override
                public void completeScore(String drug, int level, double gss, String description, char sir, ArrayList<String> mutations, String remarks) {
                    if(gss == 0.0)
                    {
                        toReturn.setText(lt("R"));
                        cell.decorationStyle().setBackgroundColor(WColor.red);
                        cell.decorationStyle().setForegroundColor(WColor.white);
                        toReturn.decorationStyle().setBackgroundColor(WColor.red);
                        toReturn.decorationStyle().setForegroundColor(WColor.white);
                    }
                    else if(gss == 0.5 || gss == 0.75)
                    {
                        toReturn.setText(lt("I"));
                        cell.decorationStyle().setBackgroundColor(WColor.yellow);
                        cell.decorationStyle().setForegroundColor(WColor.black);
                        toReturn.decorationStyle().setBackgroundColor(WColor.yellow);
                        toReturn.decorationStyle().setForegroundColor(WColor.black);
                    }
                    else if(gss == 1.0 || gss == 1.5)
                    {
                        toReturn.setText(lt("S"));
                        cell.decorationStyle().setBackgroundColor(WColor.green);
                        cell.decorationStyle().setForegroundColor(WColor.black);
                        toReturn.decorationStyle().setBackgroundColor(WColor.green);
                        toReturn.decorationStyle().setForegroundColor(WColor.black);
                    }
                    else 
                    {
                        toReturn.setText(lt("Cannot interprete"));
                        cell.decorationStyle().setBackgroundColor(WColor.black);
                        cell.decorationStyle().setForegroundColor(WColor.white);
                        toReturn.decorationStyle().setBackgroundColor(WColor.black);
                        toReturn.decorationStyle().setForegroundColor(WColor.white);
                    }
                    if(remarks!=null && !remarks.equals("null")) {
                        cell.decorationStyle().setBackgroundColor(WColor.lightGray);
                        cell.decorationStyle().setForegroundColor(WColor.black);
                        toReturn.decorationStyle().setBackgroundColor(WColor.lightGray);
                        toReturn.decorationStyle().setForegroundColor(WColor.black);
                        toReturn.setToolTipMessage(lt(remarks));
                        cell.setToolTipMessage(lt(remarks));
                    }
                    if(canShowMutations && mutations.size()>0) {
                        StringBuffer currentValue = new StringBuffer(toReturn.text().value());
                        currentValue.append(" (");
                        for(String mut : mutations) {
                            currentValue.append(mut + " ");
                        }
                        currentValue.replace(currentValue.length()-1, currentValue.length(), ")");
                        toReturn.setText(lt(currentValue.toString()));
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

        cell.decorationStyle().setBorder(new WBorder(Style.Solid, Width.Thin, WColor.black));
        cell.setContentAlignment(WHorizontalAlignment.AlignCenter);
        cell.addWidget(toReturn);
    }
    
    public static String getFixedGenericId(TestResult tr) {
        String genericId = tr.getDrugGeneric().getGenericId();
        if(genericId.startsWith("APV"))
            return genericId.replace("APV", "FPV");
        else
            return genericId;
    }
}
