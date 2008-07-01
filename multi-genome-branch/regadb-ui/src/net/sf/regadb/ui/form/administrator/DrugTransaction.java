package net.sf.regadb.ui.form.administrator;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.io.importXML.IDrugTransaction;

public class DrugTransaction implements IDrugTransaction {
    private Transaction t_;
    
    public DrugTransaction(Transaction t) {
        t_ = t;
    }

    public DrugClass getDrugClass(String id) {
        return t_.getDrugClass(id);
    }

    public DrugCommercial getDrugCommercial(String id) {
        return t_.getCommercialDrug(id);
    }

    public DrugGeneric getDrugGeneric(String id) {
        return t_.getDrugGeneric(id);
    }

    public void save(DrugClass dc) {
        t_.save(dc);
    }

    public void save(DrugGeneric dc) {
        t_.save(dc);
    }

    public void save(DrugCommercial dc) {
        t_.save(dc);        
    }
}
