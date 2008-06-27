package net.sf.regadb.io.db.uzbrussel;

import java.util.Date;

import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;

public class Medication {
    public Date start;
    public Date stop;
    public Double quantity;
    public DrugGeneric dg;
    public DrugCommercial dc;
}
