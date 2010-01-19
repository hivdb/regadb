import java.io.File;
import java.util.List;
import java.util.Set;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.PatientUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

public class TestTimeToFailure {

	public static void main(String[] args) {
		final String drug = args[0];
		final String banedir = args[1];
		final String network = args[2];
		final String landscape = args[3];
		String snapshot = args[4];
		
		System.out.println("sample_id, actual, predicted");
		
		QueryInput in = new FromSnapshot(new File(snapshot), new IQuery<Patient>() {
			@Override
			public void process(Patient p) {
				List<Therapy> therapies = TherapyUtils.sortTherapiesByStartDate(p.getTherapies());

				for (Therapy t : therapies) {
					if (t.getStartDate() == null || t.getStopDate() == null) {
						continue;
					}
					if (TherapyUtils.hasDrugExperience(drug, t)) {
						Set<NtSequence> seqs = PatientUtils.getSequencesForProtein(p, t, 30, "RT");

						for (NtSequence seq : seqs) {
							long ttf = TimeToFailureService.timeToFailure(p, t);

							if (ttf > 8*7 && ttf < 2*365) {
								String aligned = seq.getNucleotides().replace("-", "");
								double predicted = TimeToFailureService.getTTFGens(aligned, banedir, network, landscape);
								System.out.println(seq.getViralIsolate().getSampleId()+", "+ttf+", "+predicted);
							}
						}
					}
				}
			}
			@Override public void close() {}
		});
		in.run();
		
	}
	
}
