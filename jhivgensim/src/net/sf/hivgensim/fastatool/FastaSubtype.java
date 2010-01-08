package net.sf.hivgensim.fastatool;

import java.io.FileNotFoundException;

import net.sf.hivgensim.services.SubtypeService;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Test;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.util.settings.RegaDBSettings;

public class FastaSubtype extends FastaTool {

	private Genome g;
	private Test t;

	public FastaSubtype(String inputFilename,String outputFilename, String organism) throws FileNotFoundException {
		super(inputFilename,outputFilename);
		if(organism.equals("HIV-1")){
			g = StandardObjects.getHiv1Genome();
		}else if(organism.equals("HIV-2A")){
			g = StandardObjects.getHiv2AGenome();
		}else if(organism.equals("HIV-2B")){
			g = StandardObjects.getHiv2BGenome();
		}else{
			throw new IllegalArgumentException("Organism name must be: HIV-1 or HIV-2A or HIV-2B");
		}
		t = RegaDBWtsServer.getSubtypeTest();				
	}

	protected void afterProcessing() {
		
	}

	protected void beforeProcessing() {

	}

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
		if(args.length != 2 && args.length != 3){
			System.err.println("Usage: FastaSubtype in.fasta out.csv [organism]");
			System.err.println("default organism = HIV-1");
			System.exit(0);
		}
		RegaDBSettings.createInstance();
		RegaDBSettings.getInstance().getProxyConfig().initProxySettings();
		String organism = args.length == 3 ? args[2] : "HIV-1";
		FastaSubtype fs = new FastaSubtype(args[0],args[1],organism);
		fs.processFastaFile();		
	}

}
