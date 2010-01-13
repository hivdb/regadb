package be.kuleuven.rega.research.tce;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.utils.ViralIsolateUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;

public class SequenceExtractorForAlgorithm {

	public SequenceExtractorForAlgorithm(final ArrayList<String> viIds, String uid, String passwd){
		QueryInput qi = new FromDatabase(uid, passwd,new IQuery<Patient>() {

			public void process(Patient input) {			
				for(ViralIsolate vi : input.getViralIsolates()){
					if(!viIds.contains(vi.getSampleId())){
						continue;
					}
					System.out.println(vi.getSampleId()+","+ViralIsolateUtils.getConcatenatedNucleotideSequence(vi));
				}
			}
			
			public void close(){}
			
		});
		qi.run();
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		if(args.length != 3){
			System.err.println("Usage: SequenceExtractor vi_ids.csv uid passwd");
			System.exit(1);
		}
		Table t = Table.readTable(args[0]);
		new SequenceExtractorForAlgorithm(t.getColumn(0),args[1],args[2]);		
	}

}
