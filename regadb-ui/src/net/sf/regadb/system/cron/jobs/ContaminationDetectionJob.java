package net.sf.regadb.system.cron.jobs;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.contamination.ContaminationDetection;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;
import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ContaminationDetectionJob implements Job {
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		RegaDBSettings.createInstance();
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction t  = new Transaction(null, session);
		Test ccfTest = t.getTest(StandardObjects.getContaminationClusterFactorTest().getDescription());
		
		Query q = t.createQuery("select id from NtSequence ntseq where ntseq.aaSequences.size > 0 and " +
				"ntseq.id not in " +
				"(select tr.ntSequence.id from TestResult tr where tr.test = :test)");
		q.setParameter("test", ccfTest);
		
		List<Integer> sequenceIds = q.list();

		t.commit();
		t  = new Transaction(null, session);
		SequenceDb seqDb = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());
		for (int i = 0; i < sequenceIds.size(); i++) {
			NtSequence ntSeq = t.getSequence(sequenceIds.get(i));
			System.err.println("sample id:" + ntSeq.getViralIsolate().getSampleId());
			Double cf = ContaminationDetection.clusterFactor(ntSeq, seqDb);
			if (cf == null)
				continue;
			
			System.err.println("cf:" + cf);
			TestResult tr = new TestResult();
			tr.setNtSequence(ntSeq);
			ntSeq.getTestResults().add(tr);
			tr.setTest(ccfTest);
			tr.setValue(cf+"");
			
			t.save(tr);
			
            if (i % 50 == 0) {
            	t.commit();
            	t.clearCache();
            	t.flush();
            	t = new Transaction(null, session);
            }
		}
		t.commit();
	}
	
	public static void main(String [] args) {
		ContaminationDetectionJob j = new ContaminationDetectionJob();
		try {
			j.execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
	}
}
