package net.sf.regadb.io.importXML;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;

public interface IDrugTransaction {
    public DrugClass getDrugClass(String id);
    public void save(DrugClass dc);
    
    public DrugGeneric getDrugGeneric(String id);
    public void save(DrugGeneric dc);
    
    public DrugCommercial getDrugCommercial(String id);
    public void save(DrugCommercial dc);
    
    public Genome getGenome(String organismName);
}
