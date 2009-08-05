package net.sf.regadb.ui.framework.forms.fields;

import java.util.Collection;

import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.settings.Filter;

public class DrugCommercialComboBox extends ComboBox<DrugCommercial> {

    public DrugCommercialComboBox(InteractionState state, IForm form) {
        super(state, form);
    }

    public void fill(Transaction t, Filter organismFilter){ 
        if(organismFilter != null){
            addItems(t.getCommercialDrugsSorted(organismFilter));
        } else {
        	addItems(t.getCommercialDrugsSorted());
        }
    }
    
    private void addItems(Collection<DrugCommercial> items){
        for(DrugCommercial dc : items){
            addItem(new DataComboMessage<DrugCommercial>(dc, getLabel(dc)));
        }
    }
        
    public void selectItem(DrugCommercial dc){
        selectItem(getLabel(dc));
    }
    
    public String getLabel(DrugCommercial dc){
        return dc.getName();
    }
}
