package net.sf.hivgensim.pr;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.NtSequenceUtils;

public class CleanSequenceExperience extends Query<SequenceExperience,SequenceExperience> {

	private SelectionWindow[] windows;

	protected CleanSequenceExperience(IQuery<SequenceExperience> nextQuery) {
		super(nextQuery);
	}

	public CleanSequenceExperience(SelectionWindow[] windows,IQuery<SequenceExperience> nextQuery){
		super(nextQuery);
		this.windows = windows;
	}

	@Override
	public void process(SequenceExperience seqExp) {
		boolean allswok = true;
		for(SelectionWindow sw : windows){
			boolean swok = false;
			if(NtSequenceUtils.coversRegion(seqExp.getSequence(), 
					sw.getProtein().getOpenReadingFrame().getGenome().getOrganismName(),
					sw.getProtein().getAbbreviation())){
				swok = true;
			}
			if(!swok){
				allswok = false;
				break;
			}
		}
		if(allswok){
			getNextQuery().process(seqExp);
		}
	}
}
