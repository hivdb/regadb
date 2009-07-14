package net.sf.regadb.ui.framework.forms.fields;

import java.util.Collection;

import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.IForm;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.settings.Filter;

public class TestTypeComboBox extends ComboBox<TestType>{

    public TestTypeComboBox(InteractionState state, IForm form) {
        super(state, form);
    }

    public void fill(Transaction t, boolean omitEmpties){
    	Collection<TestType> testTypes;
    	if(omitEmpties)
    		testTypes = t.getUsedTestsTypes();
    	else
    		testTypes = t.getTestTypes();

    	for(TestType tt : testTypes)
            addItem(new DataComboMessage<TestType>(tt, getLabel(tt)));
        sort();
    }
    
    public void fill(Transaction t, boolean omitEmpties, Filter organismFilter){
    	Collection<TestType> testTypes;
    	if(omitEmpties)
    		testTypes = t.getUsedTestsTypes();
    	else
    		testTypes = t.getTestTypes();

    	for(TestType tt : testTypes){
        	if(organismFilter==null || tt.getGenome()==null) {
        		addItem(new DataComboMessage<TestType>(tt, getLabel(tt)));
        	} else if(organismFilter.compareRegexp(tt.getGenome().getOrganismName())) {
        		addItem(new DataComboMessage<TestType>(tt, getLabel(tt)));
        	}
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
