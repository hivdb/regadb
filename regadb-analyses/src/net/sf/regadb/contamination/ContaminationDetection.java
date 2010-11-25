package net.sf.regadb.contamination;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.sequencedb.SequenceUtils.SequenceDistance;

//TODO check whether it is OK to do this on the sequences
//TODO which distribution for Fi
public class ContaminationDetection {
	static interface DistributionFunction {
		public double f(double x);
	}

	static class NormalDistributionFunction implements DistributionFunction {
		private double double_sigma_square;
		private double mu;
		
		private double e_prefix;
		
		NormalDistributionFunction(double mu, double sigma) {
			this.double_sigma_square = 2 * sigma * sigma;
			this.mu = mu;
			
			e_prefix = 1 / Math.sqrt(double_sigma_square * Math.PI);
		}
		
		public double f(double x) {
			double xnorm = x - mu;
			double pow = xnorm * xnorm / double_sigma_square;
			return e_prefix * Math.pow(Math.E, -pow);
		}
	}
	
	static class LogNormalDistributionFunction extends NormalDistributionFunction {

		LogNormalDistributionFunction(double logmu, double logsigma) {
			super(logmu, logsigma);
		}

		public double f(double x) {
			x = Math.max(1E-6, x);
			return super.f(Math.log(x));
		}
	}
	
	public static double clusterFactor(NtSequence ntSeq, SequenceDb db) {
		//TODO outputtype stuff
		SequenceDistancesQuery distances = new SequenceDistancesQuery(ntSeq, null);
		db.query(ntSeq.getViralIsolate().getGenome(), distances);
		
		Patient p = new Patient(ntSeq.getViralIsolate().getPatient(), Privileges.READONLY.getValue());
		Set<Integer> intraPatientSeqs = new HashSet<Integer>();
		for (ViralIsolate vi : p.getViralIsolates()) 
			for (NtSequence ntseq : vi.getNtSequences()) 
				intraPatientSeqs.add(ntseq.getNtSequenceIi());
		
		DistributionFunction Fi = new LogNormalDistributionFunction(-3.896448912, 0.747342409);
		DistributionFunction Fo = new LogNormalDistributionFunction(-2.6001244697, 0.3277675448);
		
		double[] Si = new double[intraPatientSeqs.size()];
		double[] So = new double[distances.getSequenceDistances().size() - intraPatientSeqs.size()];
		
		int Si_index = 0;
		int So_index = 0;
		for (Map.Entry<Integer, SequenceDistance> e : distances.getSequenceDistances().entrySet()) {
			if (e.getKey() == ntSeq.getNtSequenceIi())
				continue;
			
			double d = (double)e.getValue().numberOfDifferences / e.getValue().numberOfPositions;
			
			if (intraPatientSeqs.contains(ntSeq.getNtSequenceIi())) {
				Si[Si_index] = d;
				Si_index++;
			} else {
				So[So_index] = d;
				So_index++;
			}
		}
		
		return (sum(Si, Fi) + sum(So, Fo)) - (sum(So, Fi) + sum(Si, Fo));
	}
	
	private static double sum(double [] distances, DistributionFunction df) {
		double sum = 0.0;
		for (double d : distances) {
			sum += Math.log(df.f(d));
		}
		return sum;
	}
}
