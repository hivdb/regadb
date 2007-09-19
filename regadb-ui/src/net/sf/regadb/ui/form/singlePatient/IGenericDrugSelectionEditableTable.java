package net.sf.regadb.ui.form.singlePatient;

import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.EditableTable;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.witty.wt.WWidget;

public class IGenericDrugSelectionEditableTable implements IEditableTable<TherapyGeneric>
{
    private FormWidget form_;
    private Therapy therapy_;
    private Transaction transaction_;
    private static final String [] headers_ = {"editableTable.drug.colName.drug", "editableTable.drugGeneric.colName.dosage"};

    public IGenericDrugSelectionEditableTable(FormWidget form, Therapy therapy)
    {
        form_ = form;
        therapy_ = therapy;
    }
    
    public void addData(WWidget[] widgets)
    {
        DrugGeneric dg = ((ComboBox<DrugGeneric>)widgets[0]).currentValue();
        TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(therapy_, dg), getDosage(((TextField)widgets[1])));
        therapy_.getTherapyGenerics().add(tg);
    }

    public void changeData(TherapyGeneric tg, WWidget[] widgets)
    {
        DrugGeneric dg = ((ComboBox<DrugGeneric>)widgets[0]).currentValue();
        tg.setDayDosageMg(getDosage(((TextField)widgets[1])));
        tg.getId().setDrugGeneric(dg);
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
    
    public void deleteData(TherapyGeneric tg)
    {
        therapy_.getTherapyGenerics().remove(tg);
        transaction_.delete(tg);
    }

    public InteractionState getInteractionState()
    {
        return form_.getInteractionState();
    }

    public String[] getTableHeaders()
    {
        return headers_;
    }

    public WWidget[] getWidgets(TherapyGeneric tg)
    {
        ComboBox<DrugGeneric> combo = new ComboBox<DrugGeneric>(InteractionState.Viewing, form_);
        TextField tf = new TextField(form_.getInteractionState(), form_, FieldType.DOUBLE);
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugGeneric> genericDrugs = t.getGenericDrugs();
        for(DrugGeneric dg: genericDrugs)
        {
            combo.addItem(new DataComboMessage<DrugGeneric>(dg, dg.getGenericName()));
        }
        combo.sort();
        
        t.commit();
        
        WWidget[] widgets = new WWidget[2];
        widgets[0] = combo;
        widgets[1] = tf;
        
        if(tg.getDayDosageMg()!=null)
        {
            tf.setText(tg.getDayDosageMg()+"");
        }
        combo.selectItem(tg.getId().getDrugGeneric().getGenericName());
        
        return widgets;
    }
    
    public void setTransaction(Transaction transaction) 
    {
        this.transaction_ = transaction;
    }

    public WWidget[] addRow() 
    {
        ComboBox<DrugGeneric> combo = new ComboBox<DrugGeneric>(form_.getInteractionState(), form_);
        TextField tf = new TextField(form_.getInteractionState(), form_, FieldType.DOUBLE);
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        List<DrugGeneric> genericDrugs = t.getGenericDrugs();
        for(DrugGeneric dg: genericDrugs)
        {
            combo.addItem(new DataComboMessage<DrugGeneric>(dg, dg.getGenericName()));
        }
        combo.sort();
        t.commit();
        
        WWidget[] widgets = new WWidget[2];
        widgets[0] = combo;
        widgets[1] = tf;
        
        return widgets;
    }

    public WWidget[] fixAddRow(WWidget[] widgets) 
    {
        DrugGeneric dg = ((ComboBox<DrugGeneric>)widgets[0]).currentValue();
        
        if(!((TextField)widgets[1]).validate())
        {
            ((TextField)widgets[1]).flagErroneous();
            return null;
        }
        
        Double units = getDosage((TextField)widgets[1]);
        
        TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(null, dg), units);
        
        return getWidgets(tg);
    }

    public void flush() 
    {
        transaction_.flush();
    }
}
