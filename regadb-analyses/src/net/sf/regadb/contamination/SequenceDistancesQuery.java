package net.sf.regadb.contamination;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.sequencedb.SequenceQuery;
import net.sf.regadb.sequencedb.SequenceUtils;
import net.sf.regadb.sequencedb.SequenceUtils.SequenceDistance;

public class SequenceDistancesQuery implements SequenceQuery {
	private Map<Integer, SequenceDistance> sequenceDistances = new HashMap<Integer, SequenceDistance>();
	
	private NtSequence query;
	private Map<String, String> alignments = new HashMap<String, String>();
	
	public SequenceDistancesQuery(NtSequence query) {
		this.query = query;
		for (AaSequence aaseq : query.getAaSequences()) {
			OpenReadingFrame orf = aaseq.getProtein().getOpenReadingFrame();
			String alignment = alignments.get(orf.getName());
			if (alignment == null) {
				alignment = 
					SequenceDb.alignmentToString(orf, query);
				alignments.put(orf.getName(), alignment);
			}
		}
	}
	
	public void process(OpenReadingFrame orf, int patientId, int isolateId, int sequenceId, String alignment) {
		SequenceDistance f = sequenceDistances.get(sequenceId);

		String queryAlignment = alignments.get(orf.getName());
		if (queryAlignment != null) {
			if (f == null) {
				f = new SequenceDistance();
				sequenceDistances.put(sequenceId, f);
			}

			try {
			SequenceDistance result = SequenceUtils.distance(queryAlignment, alignment);
			f.numberOfDifferences += result.numberOfDifferences;
			f.numberOfPositions += result.numberOfPositions;
			} catch (Exception e) {
				//System.err.println("woeps:" + patientId);
			}
		}
	}
	
	public Map<Integer, SequenceDistance> getSequenceDistances() {
		return sequenceDistances;
	}
}
