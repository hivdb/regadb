package be.kuleuven.rega.research.conserved;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.hivgensim.preprocessing.Utils;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.regadb.db.Protein;

public abstract class ConservedRegions extends QueryOutput<Sequence, ConservedRegionsOutput> {	
	public ConservedRegions(ConservedRegionsOutput out) {
		super(out);
	}

	public void close() {
		getOut().close();
	}

	public void process(Sequence input) {
		if(selectSequence(input)) {
			getOut().addPrevalence(input.subType, input.sequence);
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
		SequencesExperience se = new SequencesExperience(cr, p);
		QueryInput input = new FromSnapshot(files, se);
		input.run();
		cr.close();
	}
}
