package net.sf.regadb.ui.form.singlePatient;

import java.util.List;

import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.witty.wt.WWidget;

public class ICommercialDrugSelectionEditableTable implements IEditableTable<TherapyCommercial>
{
    private FormWidget form_;
    private Therapy therapy_;
    private Transaction transaction_;
    private static final String [] headers_ = {"editableTable.drug.colName.drug", "editableTable.drugCommercial.colName.dosage"};
    
    public ICommercialDrugSelectionEditableTable(FormWidget form, Therapy therapy)
    {
        form_ = form;
        therapy_ = therapy;
    }

    public void addData(WWidget[] widgets)
    {
        DrugCommercial dc = ((ComboBox<DrugCommercial>)widgets[0]).currentValue();
        TherapyCommercial tc = new TherapyCommercial(new TherapyCommercialId(therapy_, dc), getDosage((TextField)widgets[1]));
        therapy_.getTherapyCommercials().add(tc);
    }

    public void changeData(TherapyCommercial tc, WWidget[] widgets)
    {
        DrugCommercial dc = ((ComboBox<DrugCommercial>)widgets[0]).currentValue();
        tc.setDayDosageUnits(getDosage(((TextField)widgets[1])));
        tc.getId().setDrugCommercial(dc);
    }
    
    private Double getDosage(TextField tf)
    {
        if(tf.text().equals(""))
        {
            return null;
        }
        else
        {
            return Double.parseDouble(tf.text());
        }
    }

    public void deleteData(TherapyCommercial tc)
    {
        therapy_.getTherapyCommercials().remove(tc);
        if(tc!=null)
        {
            transaction_.delete(tc);
        }
    }

    public InteractionState getInteractionState()
    {
        return form_.getInteractionState();
    }

    public String[] getTableHeaders()
    {
        return headers_;
    }

    public WWidget[] getWidgets(TherapyCommercial tc)
    {
        ComboBox<DrugCommercial> combo = new ComboBox<DrugCommercial>(InteractionState.Viewing, form_);
        TextField tf = new TextField(form_.getInteractionState(), form_, FieldType.DOUBLE);
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugCommercial> commercialDrugs = t.getCommercialDrugs();
        for(DrugCommercial dc: commercialDrugs)
        {
            combo.addItem(new DataComboMessage<DrugCommercial>(dc, dc.getName()));
        }
        t.commit();
        
        WWidget[] widgets = new WWidget[2];
        widgets[0] = combo;
        widgets[1] = tf;
        
        if(tc!=null)
        {        
            if(tc.getDayDosageUnits()!=null)
            {
                tf.setText(tc.getDayDosageUnits()+"");
            }
            combo.selectItem(tc.getId().getDrugCommercial().getName());
        }
        
        return widgets;
    }

    public void setTransaction(Transaction transaction) 
    {
        this.transaction_ = transaction;
    }

    public WWidget[] addRow() 
    {
        ComboBox<DrugCommercial> combo = new ComboBox<DrugCommercial>(form_.getInteractionState(), form_);
        TextField tf = new TextField(form_.getInteractionState(), form_, FieldType.DOUBLE);
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugCommercial> commercialDrugs = t.getCommercialDrugs();
        for(DrugCommercial dc: commercialDrugs)
        {
            combo.addItem(new DataComboMessage<DrugCommercial>(dc, dc.getName()));
        }
        t.commit();
        
        WWidget[] widgets = new WWidget[2];
        widgets[0] = combo;
        widgets[1] = tf;
        
        return widgets;
    }

    public WWidget[] fixAddRow(WWidget[] widgets) 
    {
        DrugCommercial dc = (((ComboBox<DrugCommercial>)widgets[0]).currentValue());
        
        if(!((TextField)widgets[1]).validate())
        {
            ((TextField)widgets[1]).flagErroneous();
            return null;
        }
        
        Double units = getDosage((TextField)widgets[1]);
        
        TherapyCommercial tc = new TherapyCommercial(new TherapyCommercialId(null, dc), units);
        
        return getWidgets(tc);
    }

    public void flush() 
    {
        transaction_.flush();
    }
}