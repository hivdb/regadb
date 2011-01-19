package net.sf.regadb.contamination;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.sequencedb.SequenceUtils;
import net.sf.regadb.sequencedb.SequenceUtils.SequenceDistance;
import net.sf.regadb.util.settings.ContaminationConfig.Distribution;

import org.hibernate.Query;

public class ContaminationDetection {
	private List<Distribution> distributions;
	private SequenceDb sequenceDb;
	
	private List<DistributionFunction> Fi = new ArrayList<DistributionFunction>();
	private List<DistributionFunction> Fo = new ArrayList<DistributionFunction>();
	
	public ContaminationDetection(List<Distribution> distributions, SequenceDb sequenceDb) {
		this.distributions = distributions;
		this.sequenceDb = sequenceDb;
		
		for (Distribution ds : distributions) {
			Fi.add(new LogNormalDistributionFunction(ds.Di_mu, ds.Di_sigma));
			Fo.add(new LogNormalDistributionFunction(ds.Do_mu, ds.Do_sigma));
		}
	}
	
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
	
	public Double clusterFactor(NtSequence ntSeq, Transaction transaction) {		
		Patient p = new Patient(ntSeq.getViralIsolate().getPatient(), Privileges.READONLY.getValue());
		
		Query interPatientQuery = transaction.createQuery("select s.id from NtSequence s where s.viralIsolate.patient.id != :patient_ii");
		interPatientQuery.setParameter("patient_ii", p.getPatientIi());
		Set<Integer> So = new HashSet<Integer>(interPatientQuery.list());
		Set<Integer> Si = new HashSet<Integer>();
		for (ViralIsolate vi : p.getViralIsolates()) 
			for (NtSequence nt : vi.getNtSequences()) 
				if (nt.getNtSequenceIi() != ntSeq.getNtSequenceIi())
					Si.add(nt.getNtSequenceIi());

		int id = ntSeq.getNtSequenceIi();
				
		return (averageLogFdk(id, Si, Fi) + averageLogFdk(id, So, Fo)) - (averageLogFdk(id, So, Fi) + averageLogFdk(id, Si, Fo));
	}
	
	private double averageLogFdk(int querySequenceId, Set<Integer> sequencesIds, List<DistributionFunction> dfs) {
		double sum = 0.0;
		for (int sequenceId : sequencesIds) {
			sum += logFdk(querySequenceId, sequenceId, dfs);
		}
		
		if (sequencesIds.size() > 0)
			return sum / sequencesIds.size();
		else
			return 0.0;
	}
	
	private double logFdk(int querySequenceId, int sequenceId, List<DistributionFunction> dfs) {
		double v = 0.0;
		for (int i = 0; i < distributions.size(); i++) {
			Distribution ds = distributions.get(i);
			SequenceDistance d = getDistance(querySequenceId, sequenceId, ds);

			if (d != null)
				v += d.numberOfPositions * Math.log(dfs.get(i).f(d.distance()));
		}
		
		return v;
	}
	
	private SequenceDistance getDistance(int querySequenceId, int sequenceId, Distribution ds) {
		String query = sequenceDb.getSequence(ds.organism, ds.orf, querySequenceId);
		String sequence = sequenceDb.getSequence(ds.organism, ds.orf, sequenceId);
		if (query == null && sequence == null)
			return SequenceUtils.distance(query, sequence, ds.start, ds.end);
		else 
			return null;
	}
}