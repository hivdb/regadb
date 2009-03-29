package be.kuleuven.rega.research.conserved;

import java.util.Date;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;

public class SequencesExperience extends Query<Patient, Sequence> {
	private Protein protein;

	public SequencesExperience(IQuery<Sequence> nextQuery, Protein protein) {
		super(nextQuery);
		this.protein = protein;
	}

	public void process(Patient input) {
		for (ViralIsolate vi : input.getViralIsolates()) {
			for (NtSequence ntseq : vi.getNtSequences()) {
				for (AaSequence aaseq : ntseq.getAaSequences()) {
					if (aaseq.getProtein().getAbbreviation().equals(protein.getAbbreviation())) {
						Sequence seq = new Sequence();
						seq.sequence = aaseq;
						for (Therapy t : input.getTherapies()) {
							Date endDate = t.getStopDate();
							if(endDate == null) 
								endDate = new Date();
							
							if (QueryUtils.betweenOrEqualsInterval(vi
									.getSampleDate(), 
									t.getStartDate(), 
									endDate)) {
								seq.drugs.addAll(QueryUtils.getGenericDrugs(t));
								break;
							}
						}
						
						for (TestResult tr : ntseq.getTestResults()) {
							if (tr.getTest().getDescription().equals("Rega Subtype Tool")) {
								seq.subType = tr.getValue();
							}
						}

						getNextQuery().process(seq);
					}
				}
			}
		}
	}
}
