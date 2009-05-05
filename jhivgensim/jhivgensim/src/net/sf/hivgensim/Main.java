package net.sf.hivgensim;

import java.io.File;
import java.io.FileNotFoundException;

import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.*;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.output.SequencesToFasta;

public class Main {
	
	public static void main(String[] args) throws FileNotFoundException{
//		try {
//			FastaSubtype.main(args);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		//longitudinal kristof
//		Table t = new Table();
//		QueryInput q = 	new FromDatabase("admin","admin",
//					new GetPatientsFromDatasets(new String[]{"stanford","egazmoniz","ghb"},
//				    new GetLongitudinalSequencePairs(new String[]{"NFV"},false,
//				    new CheckForRegion("HIV-1","PR",
//				    new SequencePairsTableOutput(t,new File("/home/gbehey0/long.out"),TableQueryOutput.TableOutputType.CSV)
//				    )))); 
//		q.run();
		
		
		
//		
//		try {
//			new CrossSectionalEstimate().run();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
			
//		SequencesToCsv.main(new String[]{});
		
		QueryInput q = 	new FromSnapshot(new File("/home/gbehey0/snapshot.replicator"),
						new GetDrugClassNaiveSequences(new String[]{"PI"},
						new SequencesToFasta(new File("/home/gbehey0/tessst"))));
		q.run();
	
		
		
		
		
//		new MutationTableForConsensusNetwork().run();
		
	}

//	private final static String workDir = "/home/gbehey0/regadb";
//
//	public static void main(String[] args){
//		System.out.println("Clearing working directory");
//		clearDirectory(new File(workDir));
//		Jarbuilder.run(
//				workDir + File.separator + "build",
//				workDir + File.separator + "report",
//				"/home/gbehey0/workspace",false);
//
//
//	}
//
//	public static void clearDirectory(File path) {
//		if(path.exists()) {
//			File[] files = path.listFiles();
//			for(int i=0; i<files.length; i++) {
//				if(files[i].isDirectory()) {
//					deleteDirectory(files[i]);
//				}
//				else {
//					files[i].delete();
//				}
//			}
//		}		
//	}
//
//	public static void deleteDirectory(File path){
//		clearDirectory(path);
//		path.delete();
//	}


}
