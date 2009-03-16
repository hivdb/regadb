package net.sf.regadb.ui.form.singlePatient;

import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.CheckBox;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.DrugCommercialComboBox;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.editableTable.IEditableTable;
import net.sf.regadb.util.frequency.Frequency;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WWidget;

public class ICommercialDrugSelectionEditableTable implements IEditableTable<TherapyCommercial>
{
    private FormWidget form_;
    private Therapy therapy_;
    private Transaction transaction_;
    private static final String [] headers_ = { "editableTable.drug.colName.drug",
                                                "editableTable.drugCommercial.colName.dosage",
                                                "editableTable.drugCommercial.colName.frequency",
                                                "editableTable.drugCommercial.colName.frequency_type",
                                                "editableTable.drugCommercial.colName.placebo",
                                                "editableTable.drugCommercial.colName.blind"};
    
    private static final int[] colWidths = {30,20,15,15,10,10};
    
    public ICommercialDrugSelectionEditableTable(FormWidget form, Therapy therapy)
    {
        form_ = form;
        therapy_ = therapy;
    }

    public void addData(WWidget[] widgets)
    {
        DrugCommercial dc = ((ComboBox<DrugCommercial>)widgets[0]).currentValue();
        TherapyCommercial tc = new TherapyCommercial(   new TherapyCommercialId(therapy_, dc),
                                                        getDosage((TextField)widgets[1]),
                                                        ((CheckBox)widgets[4]).isChecked(),
                                                        ((CheckBox)widgets[5]).isChecked(),
                                                        getFrequency(widgets[2],widgets[3]));
        therapy_.getTherapyCommercials().add(tc);
    }

    public void changeData(TherapyCommercial tc, WWidget[] widgets)
    {
        DrugCommercial dc = ((ComboBox<DrugCommercial>)widgets[0]).currentValue();
        tc.setDayDosageUnits(getDosage(((TextField)widgets[1])));        
        tc.getId().setDrugCommercial(dc);

        tc.setFrequency(getFrequency(widgets[2],widgets[3]));
        tc.setPlacebo(((CheckBox)widgets[4]).isChecked());
        tc.setBlind(((CheckBox)widgets[5]).isChecked());
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
        transaction_.delete(tc);
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
        DrugCommercialComboBox combo = new DrugCommercialComboBox(InteractionState.Viewing, form_);
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
        
        if(tc!=null)
        {        
            if(tc.getDayDosageUnits()!=null)
            {
                tf.setText(tc.getDayDosageUnits()+"");
            }
            combo.selectItem(tc.getId().getDrugCommercial());
            
            cb_placebo.setChecked(tc.isPlacebo());
            cb_blind.setChecked(tc.isBlind());
            
            setFrequency(tc.getFrequency(), tf_freq, combo_freq);
        }
        
        return widgets;
    }

    public void setTransaction(Transaction transaction) 
    {
        this.transaction_ = transaction;
    }

    public WWidget[] addRow() 
    {
        DrugCommercialComboBox combo = new DrugCommercialComboBox(form_.getInteractionState(), form_);
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
        DrugCommercial dc = (((ComboBox<DrugCommercial>)widgets[0]).currentValue());
        
        if(!((TextField)widgets[1]).validate())
        {
            ((TextField)widgets[1]).flagErroneous();
            return null;
        }
        
        Double units = getDosage((TextField)widgets[1]);
        
        TherapyCommercial tc = new TherapyCommercial(   new TherapyCommercialId(null, dc),
                                                        units,
                                                        ((CheckBox)widgets[4]).isChecked(),
                                                        ((CheckBox)widgets[5]).isChecked(),
                                                        getFrequency(widgets[2],widgets[3]));
        
        return getWidgets(tc);
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

	public int[] getColumnWidths() {
		return colWidths;
	}
}