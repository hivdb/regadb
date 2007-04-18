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
        DrugCommercial dc = ((DataComboMessage<DrugCommercial>)((ComboBox)widgets[0]).currentText()).getValue();
        TherapyCommercial tc = new TherapyCommercial(new TherapyCommercialId(therapy_, dc), Double.parseDouble(((TextField)widgets[1]).text()));
        therapy_.getTherapyCommercials().add(tc);
    }

    public void changeData(TherapyCommercial tc, WWidget[] widgets)
    {
        DrugCommercial dc = ((DataComboMessage<DrugCommercial>)((ComboBox)widgets[0]).currentText()).getValue();
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
        ComboBox combo = new ComboBox(form_.getInteractionState(), form_);
        TextField tf = new TextField(form_.getInteractionState(), form_);
        
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
            tf.setText(tc.getDayDosageUnits()+"");
            combo.selectItem(new DataComboMessage<DrugCommercial>(tc.getId().getDrugCommercial(), tc.getId().getDrugCommercial().getName()));
        }
        
        return widgets;
    }

    public void setTransaction(Transaction transaction) 
    {
        this.transaction_ = transaction;
    }
}