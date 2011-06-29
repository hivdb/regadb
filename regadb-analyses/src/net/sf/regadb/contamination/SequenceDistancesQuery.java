package net.sf.regadb.contamination;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.sequencedb.SequenceQuery;
import net.sf.regadb.sequencedb.SequenceUtils;
import net.sf.regadb.sequencedb.SequenceUtils.SequenceDistance;

public class SequenceDistancesQuery implements SequenceQuery {
	public static class Range {
		public Range() {
			
		}
		
		public Range(String orf, int start, int end) {
			this.orf = orf;
			this.start = start;
			this.end = end;
		}
		
		String orf;
		int start;
		int end;
	}
	
	public enum OutputType {
		IntraPatient,
		ExtraPatient;
	}
	
	private Range range;
	
	private Map<Integer, SequenceDistance> sequenceDistances = new HashMap<Integer, SequenceDistance>();
	
	private NtSequence query;
	private Patient queryPatient;
	private OutputType outputType;
	private Map<String, String> alignments = new HashMap<String, String>();
	
	public SequenceDistancesQuery(NtSequence query, OutputType outputType, Range range) {
		this.query = query;
		this.queryPatient = new Patient(query.getViralIsolate().getPatient(), Privileges.READONLY.getValue());
		
		this.outputType = outputType;
		
		this.range = range;
		
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
	
	public void process(OpenReadingFrame orf, int patientId, int isolateId, int sequenceId, File alignment) {
		if (sequenceId == query.getNtSequenceIi())
			return;
		
		if (range != null && !orf.getName().equals(range.orf))
			return;
			
		if (outputType == OutputType.IntraPatient && patientId != queryPatient.getPatientIi())
			return;
		else if (outputType == OutputType.ExtraPatient && patientId == queryPatient.getPatientIi())
			return;
		
		SequenceDistance f = sequenceDistances.get(sequenceId);

		String queryAlignment = alignments.get(orf.getName());
		if (queryAlignment != null) {
			try {
				SequenceDistance result;
				if (range != null)
					result = SequenceUtils.distance(queryAlignment, SequenceDb.readAlignment(alignment), range.start, range.end);
				else
					result = SequenceUtils.distance(queryAlignment, SequenceDb.readAlignment(alignment), 0, queryAlignment.length());
			
				if (result.numberOfPositions != 0) {
					if (f == null) {
						f = new SequenceDistance();
						sequenceDistances.put(sequenceId, f);
					}
					
					f.numberOfDifferences += result.numberOfDifferences;
					f.numberOfPositions += result.numberOfPositions;
				}
			} catch (Exception e) {
				//System.err.println("woeps:" + patientId);
			}
		}
	}
	
	public Map<Integer, SequenceDistance> getSequenceDistances() {
		return sequenceDistances;
	}
}
