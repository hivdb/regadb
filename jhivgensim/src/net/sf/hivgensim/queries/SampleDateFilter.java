package net.sf.hivgensim.queries;

import java.util.Date;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.regadb.db.AaSequence;

public class SampleDateFilter extends Query<AaSequence, AaSequence> {

	private Date begin;
	private Date end;

	public SampleDateFilter(Date begin, Date end, IQuery<AaSequence> nextQuery){
		super(nextQuery);
		this.begin = begin;
		this.end = end;
	}
	
	@Override
	public void process(AaSequence input) {
		Date sampleDate = input.getNtSequence().getViralIsolate().getSampleDate();
		if(sampleDate.before(begin)){
			return;
		}
		if(sampleDate.after(end)){
			return;
		}
		getNextQuery().process(input);
	}

}
