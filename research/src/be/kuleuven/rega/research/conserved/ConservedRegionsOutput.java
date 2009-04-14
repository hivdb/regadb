package be.kuleuven.rega.research.conserved;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Protein;

public class ConservedRegionsOutput {
	//Map<Subtype, Map<MutationPosition, prevalence>> 
	private Map<String, Map<Integer, MutationsPrevalence>> regionsPrevalencePerGroup = new HashMap<String, Map<Integer, MutationsPrevalence>>(); 
	private Map<String, Integer> sequencesPerGroup = new HashMap<String, Integer>();
	
	private Protein protein;
	
	private List<ConservedRegionsOutputter> outputters = new ArrayList<ConservedRegionsOutputter>(); 
	
	public ConservedRegionsOutput(Protein protein) {
		this.protein = protein;
	}
	
	public void addOutputter(ConservedRegionsOutputter outputter) {
		outputters.add(outputter);
	}
	
	public void addPrevalence(String subtype, AaSequence aaseq) {
		if(subtype==null)
			return;
		
		Map<Integer, MutationsPrevalence> prevalence = regionsPrevalencePerGroup.get(subtype);
		if(prevalence==null) {
			int amountAAs = ((protein.getStopPosition()-protein.getStartPosition())/3);
			prevalence = new HashMap<Integer, MutationsPrevalence>();
			for(int i = 1; i<=amountAAs; i++) {
				prevalence.put(i, new MutationsPrevalence());
			}
			
			regionsPrevalencePerGroup.put(subtype, prevalence);
		}
				
		for(AaMutation aamut : aaseq.getAaMutations()) {
			int pos = aamut.getId().getMutationPosition();
			prevalence.get(pos).addMutation(aamut);
		}
		
		Integer amountSeqs = sequencesPerGroup.get(subtype);
		if(amountSeqs==null) {
			amountSeqs = 0;
		} 
		sequencesPerGroup.put(subtype, ++amountSeqs);
	}

	public void close() {
		for(Map.Entry<String, Map<Integer, MutationsPrevalence>> e : regionsPrevalencePerGroup.entrySet()) {
//			System.out.println(e.getKey() + " " +sequencesPerGroup.get(e.getKey()));
//			for(int i = 0; i<e.getValue().size(); i++) {
//				int pos = i+1;
//				MutationsPrevalence mp = e.getValue().get(pos);
//				System.out.println("\t"+pos + " " +mp.totalMutations());
//			}
			
			for(ConservedRegionsOutputter cro : outputters) {
				cro.export(e.getKey(), e.getValue(), sequencesPerGroup.get(e.getKey()));
			}
		}
	}
}
