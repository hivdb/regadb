package net.sf.regadb.system.cron.jobs;

import java.util.List;
import java.util.ResourceBundle;

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
import net.sf.regadb.util.settings.ContaminationConfig.Distribution;
import net.sf.regadb.util.settings.EmailConfig;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;
import org.hibernate.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ContaminationDetectionJob implements Job {
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//TODO localized resource loading
		ResourceBundle resourceBundle = ResourceBundle.getBundle("net.sf.regadb.ui.i18n.resources.regadb");
		String subject = resourceBundle.getString("jobs.contamination.subject");
		String message = resourceBundle.getString("jobs.contamination.message");
		
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
		List<Distribution> distributions = RegaDBSettings.getInstance().getContaminationConfig().getDistributions();
		SequenceDb sequenceDb = SequenceDb.getInstance(RegaDBSettings.getInstance().getSequenceDatabaseConfig().getPath());
		ContaminationDetection cd = new ContaminationDetection(distributions, sequenceDb);
		for (int i = 0; i < sequenceIds.size(); ++i) {
			NtSequence ntSeq = t.getSequence(sequenceIds.get(i));
			System.err.println("sample id:" + ntSeq.getViralIsolate().getSampleId());
			Double cf = cd.clusterFactor(ntSeq, t);
			if (cf == null)
				continue;
			
			ContaminationConfig cc = RegaDBSettings.getInstance().getContaminationConfig();
			if (cc.isSendMail() && cf <= cc.getThreshold()) {
				EmailConfig ec = RegaDBSettings.getInstance().getInstituteConfig().getEmailConfig();
                if (ec != null) {
	                try {
	                	String patientId 
	                		= new Patient(ntSeq.getViralIsolate().getPatient(), Privileges.READONLY.getValue()).getPatientId();
	                	
						MailUtils.sendMail(ec.getHost(), ec.getFrom(), ec.getTo(), 
									subject, 
									message
										.replace("{1}",patientId)
										.replace("{2}",ntSeq.getViralIsolate().getSampleId())
										.replace("{3}",ntSeq.getLabel())
										.replace("{4}",cf+""));
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
		RegaDBSettings.createInstance();
		ContaminationDetectionJob j = new ContaminationDetectionJob();
		try {
			j.execute(null);
		} catch (JobExecutionException e) {
			e.printStackTrace();
		}
	}
}
