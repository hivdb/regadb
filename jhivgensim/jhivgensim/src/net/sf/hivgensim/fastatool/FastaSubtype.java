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

	public FastaSubtype(String inputFilename,String outputFilename,String uid, String passwd, String organism) throws FileNotFoundException {
		super(inputFilename,outputFilename);
		Login l = null;
		try {
			l = Login.authenticate(uid, passwd);
		} catch (WrongUidException e) {
			e.printStackTrace();
		} catch (WrongPasswordException e) {
			e.printStackTrace();
		} catch (DisabledUserException e) {
			e.printStackTrace();
		}
		Transaction trans = l.createTransaction();
		g = trans.getGenome(organism);
		t = trans.getTest("Rega Subtype Tool");		
	}

	@Override
	protected void afterProcessing() {

	}

	@Override
	protected void beforeProcessing() {

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
		if(args.length != 4 && args.length != 5){
			System.err.println("Usage: FastaSubtype in.fasta out.csv uid passwd [organism]");
			System.err.println("default organism = HIV-1");
			System.exit(0);
		}
		String organism = args.length == 5 ? args[4] : "HIV-1";
		FastaSubtype fs = new FastaSubtype(args[0],args[1],args[2],args[3],organism);
		fs.processFastaFile();
	}

}
