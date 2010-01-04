package net.sf.hivgensim.queries;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.NtSequenceUtils;
import net.sf.regadb.db.NtSequence;

public class CleanSequences extends Query<NtSequence,NtSequence> {

	private SelectionWindow[] windows;

	protected CleanSequences(IQuery<NtSequence> nextQuery) {
		super(nextQuery);
	}

	public CleanSequences(SelectionWindow[] windows,IQuery<NtSequence> nextQuery){
		super(nextQuery);
		this.windows = windows;
	}
	
	public CleanSequences(SelectionWindow window, IQuery<NtSequence> nextQuery){
		this(new SelectionWindow[]{window},nextQuery);
	}

	@Override
	public void process(NtSequence seq) {
		boolean allswok = true;
		for(SelectionWindow sw : windows){
			boolean swok = false;
			if(NtSequenceUtils.coversRegion(seq, 
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
			getNextQuery().process(seq);
		}
	}
}