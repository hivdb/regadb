package be.kuleuven.rega.research.conserved;

import java.util.Date;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.DateUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;

public class SequencesExperience extends Query<Patient, Sequence> {
	private Protein protein;
	private Grouper grouper;

	public SequencesExperience(IQuery<Sequence> nextQuery, Protein protein, Grouper grouper) {
		super(nextQuery);
		this.protein = protein;
		this.grouper = grouper;
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
							
							if (DateUtils.betweenOrEqualsInterval(vi
									.getSampleDate(), 
									t.getStartDate(), 
									endDate)) {
								seq.drugs.addAll(TherapyUtils.getGenericDrugs(t));
								break;
							}
						}
						
						seq.group = grouper.getGroup(ntseq, seq.drugs);

						getNextQuery().process(seq);
					}
				}
			}
		}
	}
}