package net.sf.hivgensim.pr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import net.sf.hivgensim.fastatool.FastaConcat;
import net.sf.hivgensim.fastatool.FastaToNexus;
import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.RemoveMixtures;
import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.framework.Filter;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.output.SequencesToCsv;
import net.sf.hivgensim.queries.output.SequencesToFasta;
import net.sf.hivgensim.selection.RelativeFrequency;
import net.sf.hivgensim.services.BnLearner;
import net.sf.hivgensim.services.Estimate;
import net.sf.hivgensim.services.Paup;
import net.sf.hivgensim.treecluster.TreeNode;
import net.sf.hivgensim.treecluster.TreeParser;
import net.sf.hivgensim.treecluster.TreeWeights;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.tools.MutPos;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ProteaseLandscapeEstimator {

	private File workDir;

	private String naive = "naive_all_subtypes_with_sample_date.csv";
	private String treated = "treated.fasta";
	private String phylo = "phylo.fasta";
	private String nex = "phylo.nex";
	private String tree = "tree.phy";
	private String weights = "weights50.csv";

	private String mt_full = "treated.csv";
	private String mt_rf = "treated.rf";
	private String mt_clean = "treated.clean";
	private String mt_fisher = "treated.fisher";
	private String mt_treated = "treated.mut";
	private String mt_mix = "treated.mix";
	private String mt_nomix = "treated.nomix";
	private String mt_final = "mut_treated.csv";

	private String idt = "mut_treated.idt";
	private String vd = "mut_treated.vd";
	private String str = "mut_treated.str";

	private String mutations = "mutations";
	private String positions = "positions";
	private String wildtypes = "wildtypes";

	private String doublepositions = "doublepositions";
	private String mutagenesis = "mutagenesis";

	private String landscape = "best.cft";
	private String diag = "estimate.diag";

	private String[] drugClasses = new String[]{"PI"};
	private String protein = "PR";
	private String organism = "HIV-1";

	private QueryInput query;
	private SelectionWindow clean;
	private SelectionWindow region;

	public ProteaseLandscapeEstimator(File workingDirectory){
		this.workDir = workingDirectory;

		initializeDatasource();		
		initializeWindows(protein);
	}

	private void initializeDatasource(){
		query = new FromSnapshot(new File("/home/gbehey0/snapshot"),null);
	}

	private void initializeWindows(String protein){
		if("PR".equals(protein)){
			clean = SelectionWindow.PR_WINDOW_CLEAN;
			region = SelectionWindow.PR_WINDOW_REGION;
		} else if("RT".equals(protein)){
			clean = SelectionWindow.RT_WINDOW_CLEAN;
			region = SelectionWindow.RT_WINDOW_REGION;
		} else {
			throw new IllegalArgumentException(protein);
		}
	}

	private File file(String filename){
		return new File(workDir + File.separator + filename);
	}

	private String name(String filename){
		return workDir + File.separator + filename;
	}

	public void start() throws IOException{
		System.err.println("query");
		query();
		//		System.err.println("phylo");
		//		phylo();
//		System.err.println("processing mutation table");
//		variableSelection();
		//		System.err.println("learning network");
		//		network();
		//		System.err.println("estimating landscape");
		//		estimate();
	}

	private void query() throws FileNotFoundException {
		query.setNextQuery(
				new Filter<Patient>(new Filter.FilterCondition<Patient>(){

					public boolean matchesCondition(Patient input) {
						for(Dataset ds : input.getDatasets()){
							if(!ds.getDescription().equals("EgazMoniz") && !ds.getDescription().equals("stanford")){
								return true;
							}
						}
						return false;
					}

				},
				new GetDrugClassNaiveSequences(drugClasses,
//				new Filter<NtSequence>(new Filter.FilterCondition<NtSequence>(){
//						public boolean matchesCondition(NtSequence input) {
//						for(TestResult tr : input.getTestResults()){
//							if(tr.getTest().getDescription().equals(StandardObjects.getSubtypeTestDescription()) && tr.getValue().equals("HIV-1 Subtype B")){
//								return true;
//							}
//						}
//						return false;
//					}
//				},		
				new SequencesToCsv(file(naive))
				)));
		query.run();

//		query.setNextQuery(
//				new Filter<Patient>(new Filter.FilterCondition<Patient>(){
//
//					public boolean matchesCondition(Patient input) {
//						for(Dataset ds : input.getDatasets()){
//							if(ds.getDescription().equals("EgazMoniz") || ds.getDescription().equals("stanford")){
//								
//								return true;
//							}
//						}
//						return false;
//					}
//
//				},
//				new GetDrugClassExperienced("PI",
//						new Filter<NtSequence>(new Filter.FilterCondition<NtSequence>(){
//							public boolean matchesCondition(NtSequence input) {
//							for(TestResult tr : input.getTestResults()){
//								if(tr.getTest().getDescription().equals(StandardObjects.getSubtypeTestDescription()) && tr.getValue().equals("HIV-1 Subtype B")){
//									return true;
//								}
//							}
//							return false;
//						}
//					},		
//				new SequencesToFasta(file(treated))
//				))));
//		query.run();
	}

	private void phylo() throws FileNotFoundException{
		FastaConcat fc = new FastaConcat(name(naive),name(treated),name(phylo));		
		fc.processFastaFile();
		FastaToNexus ftn = new FastaToNexus(name(phylo),name(nex));
		ftn.convert();

//		Paup p = new Paup();
//		p.run(name(phylo),name(tree));
//		try{
//			TreeParser tp = new TreeParser(name(tree));
//			TreeNode root = tp.parseTree();
//			TreeWeights tw = new TreeWeights(TreeWeights.getWeightFor(protein));
//			tw.calculateWeights(root);
//			PrintStream out = new PrintStream(new FileOutputStream(file(weights)));
//			out.println(root.printWeights());
//			out.flush();
//			out.close();
//		}catch(Exception e){
//			e.printStackTrace();
//			throw new Error("treeparsing or -weighting failed");
//		}
	}

	private void variableSelection() throws IOException {
		RelativeFrequency rf = new RelativeFrequency(file(mt_full), file(mt_rf), 0.01); rf.select();
		MutationTable mt = new MutationTable(name(mt_rf));
		mt.removeInsertions();
		mt.removeDeletions();
		mt.removeUnknownMutations();
//		mt.removeMutationsOutsideRange(1,220);
		//TODO remove certain mutations: known/transmission/... ?
		mt.exportAsCsv(new FileOutputStream(file(mt_clean)));
//		FisherTest ft = new FisherTest(file(mt_clean), file(mt_fisher), drugs[0], 0.05);
//		ft.select();
//		mt = new MutationTable(name(mt_fisher));
//		int col = mt.findColumn(drugs[0]);
//		mt.deleteRowsWithValue(col, "n");
//		mt.deleteColumns(drugs[0]);
		mt.exportAsCsv(new FileOutputStream(name(mt_treated)));		
		ArrayList<String> mutations = MutPos.execute(new String[]{name(mt_treated),name(this.mutations),name(positions),name(wildtypes)});
		mt.selectColumns(mutations);
		mt.exportAsCsv(new FileOutputStream(name(mt_mix)),',', false);
		RemoveMixtures rm = new RemoveMixtures(mt);
		rm.removeMixtures();
		mt.exportAsCsv(new FileOutputStream(name(mt_nomix)),',', false);
		mt.deleteColumn(0);
		mt.exportAsCsv(new FileOutputStream(name(mt_final)),',', false);
		mt.exportAsVdFiles(new FileOutputStream(name(vd)), new FileOutputStream(name(idt)));
	}

	private void network(){
		BnLearner bnl = new BnLearner();
		bnl.run(name(vd),name(idt),name(str));
	}

	private void estimate() throws FileNotFoundException{
		//TODO better way
		PrintStream out = new PrintStream(file(doublepositions));
		out.println("215 215\n151 151\n");
		out.flush();
		out.close();
		//TODO better way		
		out = new PrintStream(file(mutagenesis));
		out.println("");
		out.flush();
		out.close();

		System.err.println("Estimation...");
		Estimate estimate = new Estimate();
		estimate.run(name(mt_final),name(naive),name(idt),name(str),name(vd),name(wildtypes),name(doublepositions),name(mutagenesis),name(weights),name(landscape),name(diag));
	}

	public static void main(String[] args) throws IOException {
		RegaDBSettings.createInstance();
		ProteaseLandscapeEstimator est = new ProteaseLandscapeEstimator(new File("/home/gbehey0/kristof/pi_fl"));
		est.start();
//		FastaToNexus ftn = new FastaToNexus("/home/gbehey0/kristof/pi_fl2/phylo_aligned_PR.fasta","/home/gbehey0/kristof/pi_fl2/phylo.nex");
//		ftn.convert();
//		try{
//			TreeParser tp = new TreeParser("/home/gbehey0/kristof/pi_fl2/tree.phy");
//			TreeNode root = tp.parseTree();
//			TreeWeights tw = new TreeWeights(TreeWeights.WEIGHT_FOR_PR);
//			tw.calculateWeights(root);
//			PrintStream out = new PrintStream(new FileOutputStream("/home/gbehey0/kristof/pi_fl2/weights.csv"));
//			out.print(root.printWeights());
//			out.flush();
//			out.close();
//		}catch(Exception e){
//			e.printStackTrace();
//			throw new Error("treeparsing or -weighting failed");
//		}
		

	}

}
