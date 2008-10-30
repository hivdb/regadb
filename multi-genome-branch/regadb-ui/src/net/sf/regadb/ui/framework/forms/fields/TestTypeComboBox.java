package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;

public class TestTypeComboBox extends ComboBox<TestType>{

    public TestTypeComboBox(InteractionState state, IForm form) {
        super(state, form);
    }

    public void fill(Transaction t, boolean omitEmpties){
        for(TestType tt : t.getTestTypes()){
            if(!omitEmpties || t.hasTests(tt))
                addItem(new DataComboMessage<TestType>(tt, getLabel(tt)));
        }
        sort();
    }
    
    public void fill(Transaction t, Genome g, boolean omitEmpties){
        for(TestType tt : t.getTestTypes()){
            if(!omitEmpties || t.hasTests(tt))
                if(Equals.isSameGenome(tt.getGenome(),g))
                    addItem(new DataComboMessage<TestType>(tt, getLabel(tt)));
        }
        sort();
    }
    
    public void selectItem(TestType testType){
        selectItem(getLabel(testType));
    }
    
    public static String getLabel(TestType testType){
        String label = testType.getDescription();
        if(testType.getGenome() != null)
            label += " ("+ testType.getGenome().getOrganismName() +")";
        return label;
    }
}
