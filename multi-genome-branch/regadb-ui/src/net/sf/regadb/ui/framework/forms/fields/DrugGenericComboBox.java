package net.sf.regadb.ui.framework.forms.fields;

import java.util.Collection;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.settings.Filter;

public class DrugGenericComboBox extends ComboBox<DrugGeneric> {

    public DrugGenericComboBox(InteractionState state, IForm form) {
        super(state, form);
    }

    public void fill(Transaction t, Filter organismFilter){     
        if(organismFilter != null){
            addItems(t.getGenericDrugsSorted(organismFilter));
        } else {
        	addItems(t.getGenericDrugsSorted());
        }
    }
    
    private void addItems(Collection<DrugGeneric> items){
        for(DrugGeneric dg : items){
            addItem(new DataComboMessage<DrugGeneric>(dg, getLabel(dg)));
        }
    }
        
    public void selectItem(DrugGeneric dg){
        selectItem(getLabel(dg));
    }
    
    public String getLabel(DrugGeneric dg){
    	return dg.getGenericName();
    }
}
