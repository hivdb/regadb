package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;

public class TestComboBox extends ComboBox<Test> {
    public TestComboBox(InteractionState state, IForm form) {
        super(state, form);
    }
    
    public void fill(Transaction t){
        for(Test tt : t.getTests()){
            addItem(new DataComboMessage<Test>(tt, getLabel(tt)));
        }
        sort();
    }
    
    public void fill(Transaction t, TestType ttt){
        for(Test tt : t.getTests()){
            if(Equals.isSameTestType(tt.getTestType(),ttt))
                addItem(new DataComboMessage<Test>(tt, getLabel(tt)));
        }
        sort();
    }
    
    public void selectItem(Test t){
        selectItem(getLabel(t));
    }
    
    public static String getLabel(Test t){
        Genome g = t.getTestType().getGenome();
        if(g == null)
            return t.getDescription();
        else
            return t.getDescription() +" ("+ g.getOrganismName() +")";
    }
}
