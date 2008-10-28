package net.sf.regadb.ui.framework.forms.fields;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;

public class DrugCommercialComboBox extends ComboBox<DrugCommercial> {

    public DrugCommercialComboBox(InteractionState state, IForm form) {
        super(state, form);
        // TODO Auto-generated constructor stub
    }

    public void fill(Transaction t, Genome genome){     
        if(genome != null){
            addItems(t.getCommercialDrugsSorted(new Genome("HIV-2A","")));
        }
        addItems(t.getCommercialDrugsSorted());
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
        Set<String> genomes = new HashSet<String>();
        for(DrugGeneric dg : dc.getDrugGenerics())
            for(Genome g : dg.getGenomes())
                genomes.add(g.getOrganismName());
        
        StringBuffer sb = new StringBuffer();
        sb.append(dc.getName());
        if(genomes.size() > 0){
            boolean first=true;
            sb.append(" (");
            for(String s : genomes){
                if(!first)
                    sb.append(", ");
                else
                    first = false;
                sb.append(s);
            }
            sb.append(")");
        }
        return sb.toString();
    }
}
