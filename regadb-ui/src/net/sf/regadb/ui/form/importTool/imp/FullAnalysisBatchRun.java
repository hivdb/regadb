package net.sf.regadb.ui.form.importTool.imp;

import java.util.List;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.sequencedb.SequenceDb;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.BlastAnalysis.UnsupportedGenomeException;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.ServiceException.ServiceUnavailableException;
import net.sf.regadb.ui.form.batchtest.BatchRun;
import net.sf.regadb.ui.form.batchtest.BatchTestStatus;
import eu.webtoolkit.jwt.WString;

public class FullAnalysisBatchRun extends Thread implements BatchRun {
	private String name;
	private BatchTestStatus status;
	private Login login;
	private SequenceDb sequenceDb;
	
	private List<Patient> patients;
	private int processedPatients = 0;
	private int failedPatients = 0;
	
	public FullAnalysisBatchRun(Login login, SequenceDb sequenceDb, List<Patient> patients, String name) {
		this.login = login;
		this.sequenceDb = sequenceDb;
		this.name = name;
		this.patients = patients;
	}
	
	@Override
	public CharSequence getRunName() {
		return name;
	}

	@Override
	public boolean isRunning() {
		return status == BatchTestStatus.RUNNING 
				|| status == BatchTestStatus.CANCELING;
	}

	@Override
	public CharSequence getStatusMessage() {
		String key = "";
		
		if ( status == BatchTestStatus.RUNNING ) key = "form.batchtest.running.status.run";
		else if ( status == BatchTestStatus.DONE ) key = "form.batchtest.running.status.done";
		else if ( status == BatchTestStatus.FAILED ) key = "form.batchtest.running.status.failed";
		else if ( status == BatchTestStatus.CANCELING ) key = "form.batchtest.running.status.canceling";
		else if ( status == BatchTestStatus.CANCELED ) key = "form.batchtest.running.status.canceled";
		
		return WString.tr(key);
	}

	@Override
	public CharSequence getPercent() {
		String s = this.processedPatients + "/" + this.patients.size();
		if (this.failedPatients > 0) {
			WString failed = WString.tr("form.importTool.failed-analyses");
			s += failed.arg(this.failedPatients).toString();
		}
		return s;
	}

	@Override
	public BatchTestStatus getStatus() {
		return status;
	}

	@Override
	public void cancel() {
		status = BatchTestStatus.CANCELING;
	}

	@Override
	public boolean isTest(Test t) {
		return false;
	}
	
	@Override
	public void run() {
		status = BatchTestStatus.RUNNING;
		
		for (Patient p : patients) {
			if (status == BatchTestStatus.CANCELING) {
				status = BatchTestStatus.CANCELED;
				return;
			}
			
			Login copiedLogin = login.copyLogin();		
			try {
				Transaction tr = copiedLogin.createTransaction();
				Patient pp = tr.getPatient(p.getPatientIi());
			
				tr.commit();
			}  catch (Exception e) {
				System.err.println("Batch Import error: error while processing isolates (blast)");
				e.printStackTrace();
				status = BatchTestStatus.FAILED;
			} finally {
				copiedLogin.closeSession();
			}
		}
		
		for (Patient p : patients) {
			if (status == BatchTestStatus.CANCELING) {
				status = BatchTestStatus.CANCELED;
				return;
			}
			
			Login copiedLogin = login.copyLogin();
			
			Patient pp = null;
			
			{
				Transaction tr = copiedLogin.createTransaction();
				pp = tr.getPatient(p.getPatientIi());
			}
	
			try {
				for (ViralIsolate vi : pp.getViralIsolates()) {
					System.err.println("Processing isolate \"" + vi.getSampleId() + "\" of patient \"" + p.getPatientId() + "\"");
					
					{
						Transaction tr = copiedLogin.createTransaction();
						Genome genome = blast(vi.getNtSequences().iterator().next(), login);
						vi.setGenome(tr.getGenome(genome.getOrganismName()));
						
						tr.save(vi);
						tr.commit();
					}
					
					NonThreadedFullAnalysis analysis = new NonThreadedFullAnalysis(vi, vi.getGenome(), sequenceDb);
					analysis.launch(copiedLogin);
				}
			} catch (Exception e) {
				System.err.println("Batch Import error: error while processing isolates");
				System.err.println("Batch Import error: error while processing patient \"" + pp.getPatientId() + "\"");
				e.printStackTrace();
				this.failedPatients++;
			} finally {
				copiedLogin.closeSession();
			}
			
			++processedPatients;
		}
		
		status = BatchTestStatus.DONE;
	}
	
	private Genome blast(NtSequence ntseq, Login login){
	    Genome genome = null;
	    //TODO check ALL sequences?
	    
        if(ntseq != null){
            BlastAnalysis blastAnalysis = new BlastAnalysis(ntseq, login.getUid());
            try{
                blastAnalysis.launch();
                genome = blastAnalysis.getGenome();
            }
            catch(UnsupportedGenomeException e){
                return null;
            }
            catch(ServiceUnavailableException e){
                return null;
            }
            catch(ServiceException e){
                e.printStackTrace();
            }            
        }
        return genome;
	}
}
