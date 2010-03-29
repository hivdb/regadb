package net.sf.hivgensim.queries;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Protein;

public class SequenceProteinFilter extends Query<NtSequence, AaSequence> {

	private Protein protein;

	public SequenceProteinFilter(Protein protein, IQuery<AaSequence> next){
		super(next);
		this.protein = protein;
	}

	@Override
	public void process(NtSequence input) {
		for(AaSequence aaseq : input.getAaSequences()){
			aaseq.setNtSequence(input);
			if(AaSequenceUtils.coversRegion(aaseq, 
					protein.getOpenReadingFrame().getGenome().getOrganismName(), 
					protein.getAbbreviation())){
				getNextQuery().process(aaseq);
			}
		}

	}

}
