package be.kuleuven.rega.research.conserved;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.sf.hivgensim.preprocessing.Utils;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.input.FromSnapshot;
import net.sf.regadb.db.Protein;
import be.kuleuven.rega.research.conserved.groupers.SubtypeGrouper;
import be.kuleuven.rega.research.conserved.output.MutationSetOutputter;
import be.kuleuven.rega.research.conserved.selector.ClassExperienceSelector;

public class ConservedRegions extends QueryOutput<Sequence, ConservedRegionsOutput> {	
	private Selector sequenceSelector;
	
	private Map<Integer, Integer> start = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> end = new HashMap<Integer, Integer>();
	
	public ConservedRegions(ConservedRegionsOutput out, Selector sequenceSelector) {
		super(out);
		this.sequenceSelector = sequenceSelector;
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
		
		
		if(sequenceSelector.selectSequence(input)) {
			getOut().addPrevalence(input.group, input.sequence);
		}
	}
	
	public void printMinMax() {
		System.err.println("min");
		for(Map.Entry<Integer, Integer> e : start.entrySet()) {
			System.err.println(e.getKey()+":"+e.getValue());
		}
		
		System.err.println("max");
		for(Map.Entry<Integer, Integer> e : end.entrySet()) {
			System.err.println(e.getKey()+":"+e.getValue());
		}
	}
	
	public static void main(String [] args) {
		Protein p = Utils.getProtein("HIV-1", "pol", "PR");
		
		ConservedRegionsOutput cro = new ConservedRegionsOutput(p);
		//cro.addOutputter(new JMolOutputter(new File("/home/plibin0/research/conserved/hiv-subtype/jmol")));
		MutationSetOutputter mso = new MutationSetOutputter(new File("/home/plibin0/research/TPV/pi.csv"), "24M", "55R", "74P", "83D");
		cro.addOutputter(mso);
		
		ClassExperienceSelector s ;
		//s = new TreatmentSelector(TreatmentSelector.Mode.Treated);
		s = new ClassExperienceSelector("PI");
		s.excludeDrug("TPV");
		ConservedRegions cr = new ConservedRegions(cro, s);
		
		SequencesExperience se = new SequencesExperience(cr, p, new SubtypeGrouper());
		
		//new File(args[0]).listFiles()
		QueryInput input = new FromSnapshot(new File(args[0]), se);
		
		input.run();
		//cr.printMinMax();
		mso.writeTable();
	}
}
