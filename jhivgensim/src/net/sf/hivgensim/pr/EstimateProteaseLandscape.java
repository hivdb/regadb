package net.sf.hivgensim.pr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Set;

import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.preprocessing.Utils;
import net.sf.hivgensim.queries.CleanSequences;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.util.settings.RegaDBSettings;

public class EstimateProteaseLandscape {

	public static void main(String[] args) throws FileNotFoundException {
		RegaDBSettings.createInstance();
		QueryInput qi;
		Set<String> mutations = Utils.getAllMutations(new SelectionWindow[]{SelectionWindow.PR_WINDOW_REGION});
		Set<String> drugs = DrugGenericUtils.getPI();

		final MutationTable mt = new MutationTable(mutations,drugs);
		final PrintStream naiveFastaStream = new PrintStream(new File("/home/gbehey0/naive.fasta"));
		final PrintStream treatedFastaStream = new PrintStream(new File("/home/gbehey0/treated.fasta"));

		System.err.println("inserting naive sequences in mutation table");
		qi = new FromDatabase("admin","admin",
				new GetDrugClassNaiveSequences(new String[]{"PI"},
						new CleanSequences(new SelectionWindow[]{SelectionWindow.PR_WINDOW_CLEAN},
								new IQuery<NtSequence>() {
									public void close() {naiveFastaStream.flush();naiveFastaStream.close();}
									public void process(NtSequence input) {
										mt.addSequence(input, new SelectionWindow[]{SelectionWindow.PR_WINDOW_REGION});
										naiveFastaStream.println(">"+input.getViralIsolate().getSampleId());
										naiveFastaStream.println(net.sf.hivgensim.preprocessing.Utils.getAlignedNtSequenceString(input, SelectionWindow.PR_WINDOW_REGION));
									}})));
		qi.run();
		System.err.println("inserting treated sequences in mutation table");
		qi = new FromDatabase("admin","admin",
				new GetTreatedWithDrugClass("PI",
						new CleanSequenceExperience(new SelectionWindow[]{SelectionWindow.PR_WINDOW_CLEAN},
								new IQuery<SequenceExperience>() {
									public void close() {treatedFastaStream.flush();treatedFastaStream.close();}
									public void process(SequenceExperience input) {
										mt.addSequence(input.getSequence(), new SelectionWindow[]{SelectionWindow.PR_WINDOW_REGION},input.getInfoCols());
										treatedFastaStream.println(">"+input.getSequence().getViralIsolate().getSampleId());
										treatedFastaStream.println(net.sf.hivgensim.preprocessing.Utils.getAlignedNtSequenceString(input.getSequence(), SelectionWindow.PR_WINDOW_REGION));
									}})));
		qi.run();
		System.err.println("exporting table");
		mt.exportAsCsv(new FileOutputStream(new File("/home/gbehey0/PI.csv")));
		
		
	}

}
