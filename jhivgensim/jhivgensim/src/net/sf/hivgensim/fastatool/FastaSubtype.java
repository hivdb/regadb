package net.sf.hivgensim.fastatool;

import java.io.FileNotFoundException;

import net.sf.hivgensim.services.SubtypeService;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.service.wts.ServiceException;

public class FastaSubtype extends FastaTool {

	private Genome g;
	private Test t;

	public FastaSubtype(String inputFilename,String outputFilename) throws FileNotFoundException {
		super(inputFilename,outputFilename);
		Login l = null;
		try {
			l = Login.authenticate("gbehey0", "bla123");
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
		Transaction trans = l.createTransaction();
		g = trans.getGenome("HIV-1");
		t = trans.getTest("Rega Subtype Tool");		
	}

	@Override
	protected void afterProcessing() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeProcessing() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void processSequence(FastaSequence fs) {
		NtSequence ntseq = new NtSequence();
		ntseq.setNucleotides(fs.getSequence().replaceAll("-",""));
		try {
			SubtypeService ss = new SubtypeService(ntseq,t,g);
			ss.launch();
			getOut().print(fs.getId().replace(">","").trim() + ",");
			getOut().println(ss.getResult());
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws FileNotFoundException{
		System.setProperty("http.proxyHost", "www-proxy");
		System.setProperty("http.proxyPort", "3128");	
		FastaSubtype fs = new FastaSubtype("/home/gbehey0/nt-alignment.fasta","/home/gbehey0/out.csv");
		fs.processFastaFile();
	}

}
