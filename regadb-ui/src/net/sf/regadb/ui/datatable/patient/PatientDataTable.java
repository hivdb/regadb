package net.sf.regadb.ui.datatable.patient;

import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;

public class PatientDataTable extends DataTable<Object[]> {
    public PatientDataTable(IDataTable<Object[]> dataTableInterface, int amountOfPageRows){
        super(dataTableInterface, amountOfPageRows);
    }

    @SuppressWarnings("unchecked")
    public boolean stillExists(Object obj) {
// disabled because somehow hibernate 3.3.1.GA doesn't always like this    	
//        Pair<Patient,PatientAttributeValue> pair = (Pair<Patient,PatientAttributeValue>)obj;
//        Transaction trans = RegaDBMain.getApp().createTransaction();
//        boolean state = trans.stillExists(pair.getKey());
//        trans.commit();
//        return state;
    	return true;
    }
}
