package net.sf.hivgensim.queries;

import java.util.HashSet;
import java.util.Set;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;

public class GetDrugClassExperienced extends Query<Patient,NtSequence> {

	private String drugclass;
	private SelectionWindow selectionWindow;

	public  GetDrugClassExperienced(String drugclass, IQuery<NtSequence> nextQuery){
		super(nextQuery);
		this.drugclass = drugclass;
	}

	public GetDrugClassExperienced(String drugclass, SelectionWindow sw, IQuery<NtSequence> nextQuery){
		this(drugclass,nextQuery);
		this.selectionWindow = sw;
	}

	public void process(Patient p) {
		NtSequence latest = null;
		Set<DrugGeneric> history = new HashSet<DrugGeneric>();
		Set<DrugGeneric> regimen = new HashSet<DrugGeneric>();

		for(Therapy t : TherapyUtils.sortTherapiesByStartDate(p.getTherapies())){
			regimen.clear();
			for(TherapyGeneric tg : t.getTherapyGenerics()){
				if(tg.getId().getDrugGeneric().getDrugClass().getClassId().equals(drugclass)){
					regimen.add(tg.getId().getDrugGeneric());
				}
			}
			for(TherapyCommercial tc : t.getTherapyCommercials()){
				for(DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics()){
					if(dg.getDrugClass().getClassId().equals(drugclass)){
						regimen.add(dg);
					}
				}
			}
			history.addAll(regimen);
			if(!regimen.isEmpty()){
				for(NtSequence seq : TherapyUtils.getAllSequencesDuringTherapy(p, t)){
					if(selectionWindow == null || selectionWindow.isAcceptable(seq)){
						if(latest == null || latest.getViralIsolate().getSampleDate().before(seq.getViralIsolate().getSampleDate())){
							latest = seq;
						}
					}
				}				
			}
		}
		if(latest != null){
			getNextQuery().process(latest);
		}
	}

}
