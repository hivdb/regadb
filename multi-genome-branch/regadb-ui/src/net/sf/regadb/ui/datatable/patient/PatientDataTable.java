package net.sf.regadb.ui.datatable.patient;

import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.ui.framework.widgets.datatable.DataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.util.pair.Pair;

public class PatientDataTable extends DataTable<Pair<Patient,PatientAttributeValue>> {
    public PatientDataTable(IDataTable<Pair<Patient,PatientAttributeValue>> dataTableInterface, int amountOfPageRows){
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
