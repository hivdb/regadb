package net.sf.hivgensim.queries.output.tce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;

public class TCEQuery extends Query<Patient,TCE> {

	protected TCEQuery(IQuery<TCE> nextQuery) {
		super(nextQuery);
	}

	public void process(Patient p) {
		List<Therapy> therapies;
		List<Therapy> formerTherapies = new ArrayList<Therapy>();

		therapies = sortTherapiesByStartDate(p.getTherapies());
		boolean therapyStopDateNull = false;

		for(int i = 0; i<therapies.size()-1; i++) {
			if(therapies.get(i).getStopDate()==null) {
				System.err.println("Excluded patient " + getDatasource(p).getDescription() + "_" + p.getPatientId()+ " since he has therapies with stopdate=null");
				therapyStopDateNull = true;
				break;
			}

		}
		if(therapyStopDateNull) {
			return;
		}

		for(Therapy t : therapies) {
			TCE tce = new TCE();

			tce.setStartDate(t.getStartDate());
			tce.getTherapiesBefore().addAll(formerTherapies);
			tce.getDrugs().addAll(getGenericDrugs(t));
			tce.setPatient(p);

			getNextQuery().process(tce);

			formerTherapies.add(t);
		}
		formerTherapies.clear();

	}	

	//TODO
	//utility func?
	public Dataset getDatasource(Patient p) {
		for (Dataset ds : p.getDatasets()) {
			if (ds.getClosedDate() == null) {
				return ds;
			}
		}

		return null;
	}

	//TODO
	//utility func?
	private List<DrugGeneric> getGenericDrugs(Therapy t) {
		List<DrugGeneric> drugGenerics = new ArrayList<DrugGeneric>();

		for(TherapyGeneric tg : t.getTherapyGenerics()) {
			drugGenerics.add(tg.getId().getDrugGeneric());
		}

		for(TherapyCommercial tc : t.getTherapyCommercials()) {
			for(DrugGeneric  dg : tc.getId().getDrugCommercial().getDrugGenerics()) {
				drugGenerics.add(dg);
			}
		}

		return drugGenerics;
	}

	//TODO
	//utility func?
	private List<Therapy> sortTherapiesByStartDate(Set<Therapy> therapies){
		List<Therapy> sortedTherapies = new ArrayList<Therapy>(therapies);

		Collections.sort(sortedTherapies, new Comparator<Therapy>() {
			public int compare(Therapy t1, Therapy t2) {
				return t1.getStartDate().compareTo(t2.getStartDate());
			}
		});

		return sortedTherapies;
	}
}
