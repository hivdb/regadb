package net.sf.regadb.io.db.drugs;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Genome;
import net.sf.regadb.io.importXML.IDrugTransaction;

public class ImportDrugTransaction implements IDrugTransaction {
    private List<DrugClass> drugClasses = new ArrayList<DrugClass>();
    private List<DrugGeneric> drugGeneric = new ArrayList<DrugGeneric>();
    private List<DrugCommercial> drugCommercial = new ArrayList<DrugCommercial>();
    
    public DrugClass getDrugClass(String id) {
        for(DrugClass dc : drugClasses) {
            if(dc.getClassId().equals(id))
                return dc;
        }
        return null;
    }

    public DrugCommercial getDrugCommercial(String id) {
        for(DrugCommercial dc : drugCommercial) {
            if(dc.getName().equals(id)) {
                return dc;
            }
        }
        return null;
    }

    public DrugGeneric getDrugGeneric(String id) {
        for(DrugGeneric dg : drugGeneric) {
            if(dg.getGenericId().equals(id)) {
                return dg;
            }
        }
        return null;
    }

    public void save(DrugClass dc) {
        drugClasses.add(dc);
    }

    public void save(DrugGeneric dc) {
        drugGeneric.add(dc);
    }

    public void save(DrugCommercial dc) {
        drugCommercial.add(dc);
    }

    public List<DrugCommercial> getDrugCommercial() {
        return drugCommercial;
    }

    public List<DrugGeneric> getDrugGeneric() {
        return drugGeneric;
    }

    public Genome getGenome(String organismName) {
        // TODO Auto-generated method stub
        return null;
    }
}
