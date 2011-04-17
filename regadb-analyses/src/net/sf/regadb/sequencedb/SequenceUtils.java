package net.sf.regadb.sequencedb;

public class SequenceUtils {
	public static class SequenceDistance {
		public int numberOfDifferences;
		public int numberOfPositions;
		
		public double distance() {
			if (numberOfPositions != 0)
				return (double)numberOfDifferences / numberOfPositions;
			else 
				return 0.0;
		}
	}
	
	public static SequenceDistance distance(String alignment1, String alignment2, int from, int to) {
		if (alignment1.length() != alignment2.length())
			throw new RuntimeException("Alignments differ in size");
		
		SequenceDistance f = new SequenceDistance();
		
		for (int i = from; i < to; i++) {
			if (alignment1.charAt(i) != '-' && alignment2.charAt(i) != '-') {
				f.numberOfPositions++;
				if (alignment1.charAt(i) != alignment2.charAt(i))
					f.numberOfDifferences++;
			}
		}
		
		return f;
	}
}
