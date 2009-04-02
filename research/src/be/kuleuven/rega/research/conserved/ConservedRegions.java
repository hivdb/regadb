package be.kuleuven.rega.research.conserved;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.hivgensim.preprocessing.Utils;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.TestResult;

public abstract class ConservedRegions extends QueryOutput<Sequence, ConservedRegionsOutput> {	
	public Map<Integer, Integer> start = new HashMap<Integer, Integer>();
	public Map<Integer, Integer> end = new HashMap<Integer, Integer>();
	
	public ConservedRegions(ConservedRegionsOutput out) {
		super(out);
	}

	public void close() {
		getOut().close();
	}

	public void process(Sequence input) {
		int startI = input.sequence.getFirstAaPos();
		int endI = input.sequence.getLastAaPos();
		
		Integer startCount = start.get(startI);
		Integer endCount = end.get(endI);
		if(startCount==null) {
			startCount=1;
		} else {
			startCount++;
		}
		if(endCount==null) {
			endCount=1;
		} else {
			endCount++;
		}
		start.put(startI, startCount);
		end.put(endI, endCount);
		
		
		if(selectSequence(input)) {
			getOut().addPrevalence(input.group, input.sequence);
		}
	}
	
	public abstract boolean selectSequence(Sequence s);
	
	public static void main(String [] args) {
		File snapshotDir = new File(args[0]);
		List<File> files = new ArrayList<File>();
		Collections.addAll(files, snapshotDir.listFiles());
		
		Protein p = Utils.getProtein("HIV-1", "pol", "PR");
		ConservedRegions cr = new ConservedRegions(new ConservedRegionsOutput(p)) {
			//naive or treated
			public boolean selectSequence(Sequence s) {
				return true;
			}
		};
		SequencesExperience se = new SequencesExperience(cr, p) {
			public String getGroup(NtSequence ntseq, List<DrugGeneric> genericDrugs) {
				for (TestResult tr : ntseq.getTestResults()) {
					if (tr.getTest().getDescription().equals("Rega Subtype Tool")) {
						return tr.getValue();
					}
				}
				return null;
			}
		};
		QueryInput input = new FromSnapshot(files, se);
		input.run();
		cr.close();
		
		if(false) {
			System.err.println("min");
			for(Map.Entry<Integer, Integer> e : cr.start.entrySet()) {
				System.err.println(e.getKey()+":"+e.getValue());
			}
			
			System.err.println("max");
			for(Map.Entry<Integer, Integer> e : cr.end.entrySet()) {
				System.err.println(e.getKey()+":"+e.getValue());
			}
		}
	}
}
