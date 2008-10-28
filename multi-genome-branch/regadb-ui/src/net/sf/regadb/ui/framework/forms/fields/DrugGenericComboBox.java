package net.sf.regadb.ui.framework.forms.fields;

import java.util.Collection;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;

public class DrugGenericComboBox extends ComboBox<DrugGeneric> {

    public DrugGenericComboBox(InteractionState state, IForm form) {
        super(state, form);
    }

    public void fill(Transaction t, Genome genome){     
        if(genome != null){
            addItems(t.getGenericDrugsSorted(genome));
        }
        addItems(t.getGenericDrugsSorted());
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
        StringBuffer sb = new StringBuffer();
        sb.append(dg.getGenericName());
        if(dg.getGenomes().size() > 0){
            boolean first=true;
            sb.append(" (");
            for(Genome g : dg.getGenomes()){
                if(!first)
                    sb.append(", ");
                else
                    first = false;
                sb.append(g.getOrganismName());
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
