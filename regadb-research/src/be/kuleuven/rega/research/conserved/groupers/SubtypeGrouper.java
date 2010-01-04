package be.kuleuven.rega.research.conserved.groupers;

import java.util.List;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
import be.kuleuven.rega.research.conserved.Grouper;

public class SubtypeGrouper implements Grouper {
	public String getGroup(NtSequence ntseq, List<DrugGeneric> genericDrugs) {
		for (TestResult tr : ntseq.getTestResults()) {
			if (tr.getTest().getDescription().equals("Rega Subtype Tool")) {
				return tr.getValue();
			}
		}
		return null;
	}
}
