package net.sf.hivgensim.queries;

import java.util.ArrayList;
import java.util.Date;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.NtSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;

public class GetDrugClassNaiveSequences extends Query<Patient,NtSequence> {

	private String[] drugclasses = new String[]{"Unknown","PI","NRTI","NNRTI","INI","EI"};
	private SelectionWindow selectionWindow = null;
	
	public GetDrugClassNaiveSequences(IQuery<NtSequence> nextQuery) {
		super(nextQuery);
	}

	public GetDrugClassNaiveSequences(String[] drugclasses,IQuery<NtSequence> nextQuery) {
		super(nextQuery);
		this.drugclasses = drugclasses;
	}
	
	public GetDrugClassNaiveSequences(String[] drugclasses, IQuery<NtSequence> nextQuery, SelectionWindow sw) {
		this(drugclasses, nextQuery);
		this.selectionWindow = sw;
	}

	public void process(Patient p) {
		ArrayList<NtSequence> allAcceptableNaiveSequences = new ArrayList<NtSequence>();
		Date sampleDate;
		for(ViralIsolate vi : p.getViralIsolates()){
			sampleDate = vi.getSampleDate();
			if(sampleDate == null){
				continue;
			}
			for(NtSequence seq : vi.getNtSequences()){
				boolean seqIsNaive = true;
				for(Therapy t : p.getTherapies()){
					if(t.getStartDate().before(sampleDate) ||
							//what to do if start == stop == sample ???
							//or stop == null?
							//for now consider them non-naive 
							(t.getStartDate().equals(sampleDate) && t.getStopDate() != null && t.getStopDate().equals(sampleDate))								
					){
						if(TherapyUtils.hasClassExperience("Unknown", t)){
							seqIsNaive = false;
						}
						for(String dc : drugclasses){
							if(TherapyUtils.hasClassExperience(dc,t)){
								seqIsNaive = false;
							}
						}
					}
				}
				if(seqIsNaive && (selectionWindow == null || selectionWindow.isAcceptable(seq))){
					allAcceptableNaiveSequences.add(seq);
				}
			}
		}
		NtSequence latest = NtSequenceUtils.getLatestNtSequence(allAcceptableNaiveSequences);
		if(latest != null){
			getNextQuery().process(latest);
		}
	}
}
