package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;

public class GenomeComboBox extends ComboBox<Genome>{

    public GenomeComboBox(InteractionState state, IForm form) {
        super(state, form);
    }

    public void fill(Transaction t){
        for(Genome genome : t.getGenomes()){
            addItem(new DataComboMessage<Genome>(genome, genome.getOrganismName()));
        }
        sort();
        addNoSelectionItem();
    }
}
