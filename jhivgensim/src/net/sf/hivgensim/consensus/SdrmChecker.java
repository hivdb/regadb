package net.sf.hivgensim.consensus;

import java.io.IOException;

import net.sf.hivgensim.mutationlists.ConsensusMutationList;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;

	
	public class SdrmChecker extends Query<AaSequence, AaSequence> {
		private ConsensusMutationList mutations;

		public SdrmChecker( IQuery<AaSequence> nextQuery){
			super(nextQuery);
			try {
				mutations = ConsensusMutationList.retrieveListFromURL("http://cpr-mirr.stanford.edu/cpr/components/hiv_prrt/lists/sdrm_2009");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			
		}

		@Override
		public void process(AaSequence input) {
			for(AaMutation mut : input.getAaMutations()){
				boolean tdr = mutations.containsMutation(input.getProtein().getAbbreviation(), mut.getId().getMutationPosition(), mut.getAaMutation());
				if(tdr){
					return;
				}
			}
			getNextQuery().process(input);
		}
		
		
}
