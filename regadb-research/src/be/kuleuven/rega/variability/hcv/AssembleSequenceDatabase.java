package be.kuleuven.rega.variability.hcv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import be.kuleuven.rega.variability.fasta.Operations;
import be.kuleuven.rega.variability.fasta.Operations.AnnotationParser;

public class AssembleSequenceDatabase {
	static class EuAnnotationParser implements AnnotationParser {
		public String parse(String annotation) {
        	String accession = annotation.split("\\|")[2].split(" ")[0];
        	int _pos = -1;
        	if ((_pos = accession.indexOf('_')) != -1 && _pos > 4) {
        		accession = accession.substring(0, _pos);
        	}
        	return accession;
		}
	}
	
	static class LaAnnotationParser implements AnnotationParser {
		public String parse(String annotation) {
        	String [] parts = annotation.split("\\.");
        	return parts[parts.length - 1];
		}
	}
	
	public static void main(String [] args) throws IOException {
		String protein = args[0];
		String baseDir = args[1];
		File logFile = new File(baseDir + "log.txt");
		logFile.delete();
		
		Map<String, String> euDb = 
			Operations.readFasta(new File(baseDir + "eu/" + protein + ".fasta"), 
					new EuAnnotationParser());
        
        Map<String, String> jpDb = 
        	Operations.readFasta(new File(baseDir + "jp/" + protein + ".fasta"),
					null);
		
        Map<String, String> laDb = 
        	Operations.readFasta(new File(baseDir + "la/" + protein + ".fasta"),
					new LaAnnotationParser());
        
        
        addDatabase(logFile, euDb, jpDb, "jp");
        
        addDatabase(logFile, euDb, laDb, "la");
        
        Operations.writeFasta(new File(baseDir + "combined/" + protein + ".fasta"), euDb);
	}
	
	public static void addDatabase(File logFile, Map<String, String> base, Map<String, String> toAdd, String databaseName) throws IOException {
		FileWriter fw = new FileWriter(logFile, true);
		fw.write("***" + databaseName + "\n");
		
		for (Map.Entry<String, String> e : toAdd.entrySet()) {
			if (base.get(e.getKey()) == null) {
				base.put(e.getKey(), e.getValue());
				fw.write(e.getKey() + "\n");
			}
		}
		fw.close();
	}
}
