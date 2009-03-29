package be.kuleuven.rega.research.conserved;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.DrugGeneric;

public class Sequence {
	public AaSequence sequence;
	public List<DrugGeneric> drugs = new ArrayList<DrugGeneric>();
	public String subType;
}