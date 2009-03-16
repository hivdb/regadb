package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DrugGenericComboBox;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.regadb.util.frequency.Frequency;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WWidget;

public class IGenericDrugSelectionEditableTable implements IEditableTable<TherapyGeneric>
{
    private FormWidget form_;
    private Therapy therapy_;
    private Transaction transaction_;
    private static final String [] headers_ = { "editableTable.drug.colName.drug",
                                                "editableTable.drugGeneric.colName.dosage",
                                                "editableTable.drugGeneric.colName.frequency",
                                                "editableTable.drugGeneric.colName.frequency_type",
                                                "editableTable.drugGeneric.colName.placebo",
                                                "editableTable.drugGeneric.colName.blind"};

    private static final int[] colWidths = {30,20,15,15,10,10};
    
    
    public IGenericDrugSelectionEditableTable(FormWidget form, Therapy therapy)
    {
        form_ = form;
        therapy_ = therapy;
    }
    
    public void addData(WWidget[] widgets)
    {
        DrugGeneric dg = ((ComboBox<DrugGeneric>)widgets[0]).currentValue();
        TherapyGeneric tg = new TherapyGeneric( new TherapyGenericId(therapy_, dg),
                                                getDosage(((TextField)widgets[1])),
                                                ((CheckBox)widgets[4]).isChecked(),
                                                ((CheckBox)widgets[5]).isChecked(),
                                                getFrequency(widgets[2],widgets[3]));
        therapy_.getTherapyGenerics().add(tg);
    }

    public void changeData(TherapyGeneric tg, WWidget[] widgets)
    {
        DrugGeneric dg = ((ComboBox<DrugGeneric>)widgets[0]).currentValue();
        tg.setDayDosageMg(getDosage(((TextField)widgets[1])));
        tg.getId().setDrugGeneric(dg);

        tg.setFrequency(getFrequency(widgets[2],widgets[3]));
        tg.setPlacebo(((CheckBox)widgets[4]).isChecked());
        tg.setBlind(((CheckBox)widgets[5]).isChecked());
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
        DrugGenericComboBox combo = new DrugGenericComboBox(InteractionState.Viewing, form_);
        TextField tf = new TextField(form_.getInteractionState(), form_, FieldType.DOUBLE);
        CheckBox cb_placebo = new CheckBox(form_.getInteractionState(), form_);
        CheckBox cb_blind = new CheckBox(form_.getInteractionState(), form_);
        TextField tf_freq = new TextField(form_.getInteractionState(), form_, FieldType.DOUBLE);
        ComboBox<Frequency> combo_freq = createFrequencyComboBox();
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        combo.fill(t, RegaDBSettings.getInstance().getInstituteConfig().getOrganismFilter());
        t.commit();
        
        WWidget[] widgets = new WWidget[6];
        widgets[0] = combo;
        widgets[1] = tf;
        widgets[2] = tf_freq;
        widgets[3] = combo_freq;
        widgets[4] = cb_placebo;
        widgets[5] = cb_blind;
        
        if(tg.getDayDosageMg()!=null)
        {
            tf.setText(tg.getDayDosageMg()+"");
        }
        combo.selectItem(tg.getId().getDrugGeneric());
        
        cb_placebo.setChecked(tg.isPlacebo());
        cb_blind.setChecked(tg.isBlind());

        setFrequency(tg.getFrequency(), tf_freq, combo_freq);
        
        return widgets;
    }
    
    public void setTransaction(Transaction transaction) 
    {
        this.transaction_ = transaction;
    }

    public WWidget[] addRow() 
    {
        DrugGenericComboBox combo = new DrugGenericComboBox(form_.getInteractionState(), form_);
        TextField tf = new TextField(form_.getInteractionState(), form_, FieldType.DOUBLE);
        CheckBox cb_placebo = new CheckBox(form_.getInteractionState(), form_);
        CheckBox cb_blind = new CheckBox(form_.getInteractionState(), form_);
        TextField tf_freq = new TextField(form_.getInteractionState(), form_, FieldType.DOUBLE);
        ComboBox<Frequency> combo_freq = createFrequencyComboBox();
        
        Transaction t = RegaDBMain.getApp().createTransaction();
        combo.fill(t, RegaDBSettings.getInstance().getInstituteConfig().getOrganismFilter());
        t.commit();
        
        WWidget[] widgets = new WWidget[6];
        widgets[0] = combo;
        widgets[1] = tf;
        widgets[2] = tf_freq;
        widgets[3] = combo_freq;
        widgets[4] = cb_placebo;
        widgets[5] = cb_blind;
        
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
        
        TherapyGeneric tg = new TherapyGeneric( new TherapyGenericId(null, dg),
                                                units,
                                                ((CheckBox)widgets[4]).isChecked(),
                                                ((CheckBox)widgets[5]).isChecked(),
                                                getFrequency(widgets[2],widgets[3]));
        
        return getWidgets(tg);
    }

    public void flush() 
    {
        transaction_.flush();
    }
    
    private ComboBox<Frequency> createFrequencyComboBox()
    {
        ComboBox<Frequency> cmbb = new ComboBox<Frequency>(form_.getInteractionState(),form_);
        
        cmbb.addItem(new DataComboMessage<Frequency>(Frequency.DAYS,    Frequency.DAYS.toString()));
        cmbb.addItem(new DataComboMessage<Frequency>(Frequency.WEEKS,   Frequency.WEEKS.toString()));
        cmbb.addItem(new DataComboMessage<Frequency>(Frequency.MONTHS,  Frequency.MONTHS.toString()));
        cmbb.addItem(new DataComboMessage<Frequency>(Frequency.YEARS,   Frequency.YEARS.toString()));
        cmbb.addItem(new DataComboMessage<Frequency>(Frequency.PERDAYS, Frequency.PERDAYS.toString()));
        cmbb.addItem(new DataComboMessage<Frequency>(Frequency.PERWEEKS,Frequency.PERWEEKS.toString()));
        cmbb.addItem(new DataComboMessage<Frequency>(Frequency.PERMONTHS,Frequency.PERMONTHS.toString()));
        cmbb.addItem(new DataComboMessage<Frequency>(Frequency.PERYEARS, Frequency.PERYEARS.toString()));
        
        return cmbb;
    }
    
    private Long getFrequency(WWidget value, WWidget combo_freq){
        TextField tf = (TextField)value;
        if(tf.text().equals(""))
        {
            return (long)Frequency.getDefaultFrequency();
        }
        else
        {
            double fval =  Double.parseDouble(tf.text());
            Frequency freq = ((ComboBox<Frequency>)combo_freq).currentValue();
            return (long)freq.timesToInterval(fval);
        }
    }
    
    private void setFrequency(Long value, TextField tf, ComboBox<Frequency> combo_freq)
    {
        if(value == null || value == 0){
            value = (long)Frequency.getDefaultFrequency();
        }
        Frequency f = Frequency.getFrequency((double)value);
        if(f != null){
            tf.setText(java.lang.Math.round(f.getX()) +"");
            combo_freq.selectItem(f.toString());
        }
    }
    
    private String getGenericDrugRepresentation(DrugGeneric dg) {
    	return dg.getGenericName() + " ("+dg.getGenericId()+")";
    }

	public int[] getColumnWidths() {
		return colWidths;
	}
}
