package net.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

public class GetSequencesById implements IQuery<Patient>{
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yy");
	private ArrayList<Integer> ids;
	private PrintStream ps;
	private HashMap<String,Character> datasetEncoding;
	
	public GetSequencesById(File file) throws FileNotFoundException{
		ps = new PrintStream(new File("naive_short_ds.fasta"));
		ids = new ArrayList<Integer>();
		
		Scanner s = new Scanner(file);
		String id;
		while(s.hasNextLine()){
			id = s.nextLine().trim().split(",")[0];
			ids.add(Integer.valueOf(id));
		}
		
		datasetEncoding = new HashMap<String,Character>();
		datasetEncoding.put("brescia", 'b');
		datasetEncoding.put("irsicaixa", 'i');
		datasetEncoding.put("telaviv", 't');
		datasetEncoding.put("ARCA", 'c');
		datasetEncoding.put("AREVIR V2", 'e');
		datasetEncoding.put("KAROLINSKA", 'k');




		
		
		
	}
		
	public void process(Patient input) {
		String dataset = input.getDatasets().iterator().next().getDescription();
		for(ViralIsolate vi : input.getViralIsolates()){
			for(NtSequence seq : vi.getNtSequences()){
				if(ids.contains(seq.getNtSequenceIi())){
					ps.println(">"+seq.getNtSequenceIi()+"-"+sdf.format(seq.getViralIsolate().getSampleDate())+"-"+datasetEncoding.get(dataset));
					ps.println(seq.getNucleotides());
				}
			}
		}
	}
	
	public void close() {
		ps.flush();
		ps.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		QueryInput query = new FromSnapshot(new File("/home/gbehey0/snapshot"), new GetSequencesById(new File("naive_ids.csv")));
		query.run();
	}

}
