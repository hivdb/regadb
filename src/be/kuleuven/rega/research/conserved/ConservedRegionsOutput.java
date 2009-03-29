package be.kuleuven.rega.research.conserved;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Protein;

public class ConservedRegionsOutput {
	//Map<Subtype, Map<MutationPosition, prevalence>> 
	private Map<String, Map<Integer, MutationsPrevalence>> regionsPrevalencePerSubtype = new HashMap<String, Map<Integer, MutationsPrevalence>>(); 
	private Protein protein;
	public ConservedRegionsOutput(Protein protein) {
		this.protein = protein;
	}
	
	public void addPrevalence(String subtype, AaSequence aaseq) {
		if(subtype==null)
			return;
		
		Map<Integer, MutationsPrevalence> prevalence = regionsPrevalencePerSubtype.get(subtype);
		if(prevalence==null) {
			int amountAAs = ((protein.getStopPosition()-protein.getStartPosition())/3);
			prevalence = new HashMap<Integer, MutationsPrevalence>();
			for(int i = 1; i<=amountAAs; i++) {
				prevalence.put(i, new MutationsPrevalence());
			}
			
			regionsPrevalencePerSubtype.put(subtype, prevalence);
		}
		
		for(AaMutation aamut : aaseq.getAaMutations()) {
			int pos = aamut.getId().getMutationPosition();
			prevalence.get(pos).addMutation(aamut);
		}
	}

	public void close() {
		for(Map.Entry<String, Map<Integer, MutationsPrevalence>> e : regionsPrevalencePerSubtype.entrySet()) {
			System.out.println(e.getKey());
			for(int i = 0; i<e.getValue().size(); i++) {
				int pos = i+1;
				//int p = e.getValue().get(pos);
				//System.out.println("\t"+(pos)+":"+p);
			}
		}
	}
}
