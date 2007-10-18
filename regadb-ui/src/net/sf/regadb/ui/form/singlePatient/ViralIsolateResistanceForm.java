package net.sf.regadb.ui.form.singlePatient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.importXML.ResistanceInterpretationParser;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WBorder;
import net.sf.witty.wt.WColor;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WGroupBox;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WPushButton;
import net.sf.witty.wt.WTable;
import net.sf.witty.wt.WTableCell;
import net.sf.witty.wt.WText;
import net.sf.witty.wt.WBorder.Style;
import net.sf.witty.wt.WBorder.Width;
import net.sf.witty.wt.core.utils.WHorizontalAlignment;
import net.sf.witty.wt.core.utils.WLength;
import net.sf.witty.wt.core.utils.WLengthUnit;
import net.sf.witty.wt.core.utils.WSide;

public class ViralIsolateResistanceForm extends WContainerWidget
{
    private ViralIsolateForm viralIsolateForm_;
    
    private WGroupBox resistanceGroup_;
    private WTable resistanceTable_;
    private WPushButton refreshButton_;
    
    public ViralIsolateResistanceForm(ViralIsolateForm viralIsolateForm)
    {
        super();
        viralIsolateForm_ = viralIsolateForm;

        init();
    }

    public void init()
    {
        resistanceGroup_ = new WGroupBox(tr("form.viralIsolate.editView.group.resistance"), this);
        
        WTable wrapper = new WTable(resistanceGroup_);
        
        resistanceTable_ = new WTable(wrapper.elementAt(0, 0));
        
        refreshButton_ = new WPushButton(tr("form.viralIsolate.editView.resistance.refreshButton"), wrapper.elementAt(0, 1));
        refreshButton_.setMargin(new WLength(15), WSide.Left);
        refreshButton_.clicked.addListener(new SignalListener<WMouseEvent>()
                {
                    public void notify(WMouseEvent a) 
                    {
                        Transaction t = RegaDBMain.getApp().createTransaction();
                        t.refresh(viralIsolateForm_.getViralIsolate());
                        t.commit();
                        
                        loadTable();
                    }
                });

        loadTable();
    }
    
    private void loadTable()
    {
        resistanceTable_.clear();
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugClass> sortedDrugClasses_  = t.getDrugClassesSortedOnResistanceRanking();
        
        //drug names - column position
        HashMap<String, Integer> algoColumn = new HashMap<String, Integer>();
        int col = 0;
        resistanceTable_.putElementAt(0, col, new WText());
        col = resistanceTable_.numColumns();
        resistanceTable_.putElementAt(0, col, new WText());
        int maxWidth = 0;
        for(Test test : t.getTests())
        {
            if(StandardObjects.getGssId().equals(test.getTestType().getDescription()) && test.getAnalysis()!=null)
            {
                col = resistanceTable_.numColumns();
                resistanceTable_.putElementAt(0, col, new TableHeader(lt(test.getDescription())));
                algoColumn.put(test.getDescription(), col);
                maxWidth += test.getDescription().length();
            }
        }
        
        //drug names - row position
        HashMap<String, Integer> drugColumn = new HashMap<String, Integer>();
        
        List<DrugGeneric> genericDrugs;
        int row;
        boolean firstGenericDrugInThisClass;
        for(DrugClass dc : sortedDrugClasses_)
        {
            genericDrugs = t.getDrugGenericSortedOnResistanceRanking(dc);
            firstGenericDrugInThisClass = true;
            for(DrugGeneric dg : genericDrugs)
            {
                row = resistanceTable_.numRows();
                if(firstGenericDrugInThisClass)
                {
                    resistanceTable_.putElementAt(row, 0, new TableHeader(lt(dc.getClassId()+ ":")));
                    firstGenericDrugInThisClass = false;
                }
                resistanceTable_.putElementAt(row, 1, new TableHeader(lt(dg.getGenericId())));
                drugColumn.put(dg.getGenericId(), row);
            }
        }
        
        //clear table
        for(int i = 1; i < resistanceTable_.numRows(); i++)
        {
            for(int j = 2; j< resistanceTable_.numColumns(); j++)
            {
                putResistanceTableResult(null, resistanceTable_.elementAt(i, j), false);
            }
        }
        
        Integer colN;
        Integer rowN;
        for(TestResult tr : viralIsolateForm_.getViralIsolate().getTestResults())
        {            
            colN = algoColumn.get(tr.getTest().getDescription());
            rowN = drugColumn.get(getFixedGenericId(tr));
            if(colN!=null && rowN!=null) {
                putResistanceTableResult(tr, resistanceTable_.elementAt(rowN, colN), false);
            }
            rowN = drugColumn.get(getFixedGenericId(tr)+"/r");
            if(colN!=null && rowN!=null) {
                putResistanceTableResult(tr, resistanceTable_.elementAt(rowN, colN), true);
            }
        }
        
        resistanceTable_.resize(new WLength(maxWidth+maxWidth/2, WLengthUnit.FontEx), new WLength());
        resistanceTable_.setCellPadding(4);
    }
    
    private String getFixedGenericId(TestResult tr) {
        String genericId = tr.getDrugGeneric().getGenericId();
        if(genericId.startsWith("APV"))
            return genericId.replace("APV", "FPV");
        else
            return genericId;
    }
    
    private void putResistanceTableResult(TestResult tr, final WTableCell cell, boolean onlyIfCurrentValueIsNA)
    {
        //make sure we do not override a sensible value
        if(onlyIfCurrentValueIsNA) {
            if(!((WText)cell.children().get(0)).text().value().equals("NA"))
                return;
        }
        
        cell.clear();
        
        final WText toReturn = new WText();
        
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
}
