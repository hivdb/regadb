package net.sf.hivgensim.queries;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryImpl;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;

public class CleanSequences extends QueryImpl<NtSequence, NtSequence> {

	private SelectionWindow[] windows;
	
	protected CleanSequences(Query<NtSequence> inputQuery) {
		super(inputQuery);
	}
	
	public CleanSequences(Query<NtSequence> inputQuery, SelectionWindow[] windows){
		super(inputQuery);
		this.windows = windows;
	}

	@Override
	public void populateOutputList() {
		for(NtSequence seq : inputQuery.getOutputList()){
			boolean allswok = true;
			for(SelectionWindow sw : windows){
				boolean swok = false;
				for(AaSequence aaseq : seq.getAaSequences()){
					if(aaseq.getProtein().getProteinIi() == sw.getProtein().getProteinIi()){
						boolean aaseqcomplete = true;
						for(AaMutation aamut : aaseq.getAaMutations()){
							if(sw.contains(aamut) && aamut.getAaReference().equals("")){
								aaseqcomplete = false;
							}
						}
						if(aaseqcomplete){
							swok = true;
						}
					}
				}
				if(!swok){
					allswok = false;
				}
			}
			if(allswok){
				outputList.add(seq);
			}
		}		
	}

}
