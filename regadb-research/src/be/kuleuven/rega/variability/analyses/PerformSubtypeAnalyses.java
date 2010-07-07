package be.kuleuven.rega.variability.analyses;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.SubtypeAnalysis;
import net.sf.regadb.util.settings.RegaDBSettings;
import be.kuleuven.rega.variability.fasta.Operations;

public class PerformSubtypeAnalyses {
	private Genome genome;
	
	public static void main(String [] args) throws IOException {
		if (args.length != 3) {
			System.err.println("PerformSubtypeAnalyses organism fasta-file output-file");
			return;
		}
		
		RegaDBSettings.createInstance();
		PerformSubtypeAnalyses subtype = new PerformSubtypeAnalyses(); 
		subtype.run(args[0], args[1], args[2]);
	}
	
	public void run(String organism, String fasta, String output) throws IOException {
		Map<String, String> sequences = 
			Operations.readFasta(new File(fasta), null);
		genome = new Genome();
		genome.setOrganismName(organism);
		
		FileWriter outputWriter = new FileWriter(new File(output));
		for (Map.Entry<String, String> s : sequences.entrySet()) {
			TestResult tr = performSubtypeAnalysis(s.getKey(), s.getValue());
			outputWriter.append("\"" + s.getKey() + "\",");
			outputWriter.append("\"" + tr.getValue() + "\"\n");
			
			System.err.println(s.getKey() + "->" + tr.getValue());
		}
		outputWriter.close();
	}
	
	public TestResult performSubtypeAnalysis(String label, String sequence) {
		NtSequence ntSequence = new NtSequence();
		ntSequence.setLabel(label);
		ntSequence.setNucleotides(sequence);
		
	    SubtypeAnalysis subtypeAnalyis = new SubtypeAnalysis(ntSequence, RegaDBWtsServer.getSubtypeTest(), genome);
	    try {
	        subtypeAnalyis.launch();
	    } catch (ServiceException e) {
	        e.printStackTrace();
	    }
	    return subtypeAnalyis.getTestResult();
	}
}
