package net.sf.regadb.system.cron.jobs;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import net.sf.regadb.contamination.ContaminationDetection;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.session.HibernateUtil;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.util.mail.MailUtils;
import net.sf.regadb.util.settings.ContaminationConfig;
import net.sf.regadb.util.settings.EmailConfig;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;
import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import eu.webtoolkit.jwt.WString;

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
			
			ContaminationConfig cc = RegaDBSettings.getInstance().getContaminationConfig();
			if (cc.isSendMail() && cf > cc.getThreshold()) {
				EmailConfig ec = RegaDBSettings.getInstance().getInstituteConfig().getEmailConfig();
                if (ec != null) {
	                try {
	                	String patientId 
	                		= new Patient(ntSeq.getViralIsolate().getPatient(), Privileges.READONLY.getValue()).getPatientId();
						MailUtils.sendMail(ec.getHost(), ec.getFrom(), ec.getTo(), 
									WString.tr("jobs.contamination.subject"), 
									WString.tr("jobs.contamination.message")
										.arg(patientId)
										.arg(ntSeq.getViralIsolate().getSampleId())
										.arg(ntSeq.getLabel())
										.arg(cf));
					} catch (AddressException e) {
						e.printStackTrace();
					} catch (MessagingException e) {
						e.printStackTrace();
					}
                }
			}
			
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
