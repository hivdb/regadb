package net.sf.hivgensim.pr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Set;
import java.util.TreeSet;

import net.sf.hivgensim.preprocessing.MutationTable;
import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.CleanSequences;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.util.settings.RegaDBSettings;

public class EstimateProteaseLandscape {

	public static void main(String[] args) throws FileNotFoundException {
		RegaDBSettings.createInstance();
		QueryInput qi;
		Set<String> mutations = new TreeSet<String>();
		for(int i = 1; i < 100; i++){
			mutations.add("PR"+i+"A");
			mutations.add("PR"+i+"C");
			mutations.add("PR"+i+"D");
			mutations.add("PR"+i+"E");
			mutations.add("PR"+i+"F");
			mutations.add("PR"+i+"G");
			mutations.add("PR"+i+"H");
			mutations.add("PR"+i+"I");
			mutations.add("PR"+i+"K");
			mutations.add("PR"+i+"L");
			mutations.add("PR"+i+"M");
			mutations.add("PR"+i+"N");
			mutations.add("PR"+i+"P");
			mutations.add("PR"+i+"Q");
			mutations.add("PR"+i+"R");
			mutations.add("PR"+i+"S");
			mutations.add("PR"+i+"T");
			mutations.add("PR"+i+"V");
			mutations.add("PR"+i+"W");
			mutations.add("PR"+i+"Y");
			mutations.add("PR"+i+"ins");
			mutations.add("PR"+i+"del");
		}
		
		final MutationTable mt = new MutationTable(mutations);
		System.err.println("inserting naive sequences in mutation table");
		qi = new FromDatabase("admin","admin",
				new GetDrugClassNaiveSequences(new String[]{"PI"},
						new CleanSequences(new SelectionWindow[]{SelectionWindow.PR_WINDOW_CLEAN},
								new IQuery<NtSequence>() {
									public void close() {}
									public void process(NtSequence input) {
										mt.addSequence(input, new SelectionWindow[]{SelectionWindow.PR_WINDOW_REGION});
									}})));
		qi.run();
		System.err.println("inserting treated sequences in mutation table");
		qi = new FromDatabase("admin","admin",
				new GetTreatedWithDrugClass("PI",
						new CleanSequences(new SelectionWindow[]{SelectionWindow.PR_WINDOW_CLEAN},
								new IQuery<NtSequence>() {
									public void close() {}
									public void process(NtSequence input) {
										mt.addSequence(input, new SelectionWindow[]{SelectionWindow.PR_WINDOW_REGION});
									}})));
		qi.run();
		System.err.println("exporting table");
		mt.exportAsCsv(new FileOutputStream(new File("/home/gbehey0/PI.csv")));
	}

}
