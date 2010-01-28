package be.kuleuven.rega.research.tce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.PatientUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

public class TCEQuery extends Query<Patient,TCE> {

	protected TCEQuery(IQuery<TCE> nextQuery) {
		super(nextQuery);
	}

	public void process(Patient p) {
		List<Therapy> therapies;
		List<Therapy> formerTherapies = new ArrayList<Therapy>();

		therapies = TherapyUtils.sortTherapiesByStartDate(p.getTherapies());
		boolean therapyStopDateNull = false;

		for(int i = 0; i<therapies.size()-1; i++) {
			if(therapies.get(i).getStopDate()==null) {
				System.err.println("Excluded patient " + PatientUtils.getDatasource(p).getDescription() + "_" + p.getPatientId()+ " since he has therapies with stopdate=null");
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
			tce.getDrugs().addAll(TherapyUtils.getGenericDrugs(t));
			tce.setPatient(p);
			if(!containsIgnoredDrugs(tce)){
				getNextQuery().process(tce);
			}

			formerTherapies.add(t);
		}
		formerTherapies.clear();

	}
	
	public boolean containsIgnoredDrugs(TCE tce){
		List<String> ignored = Arrays.asList("PI","NRTI","NNRTI","Unknown","ADV","aAPA","R82913","CPV","MVC");
		for(DrugGeneric dg : tce.getDrugs()){
			if(ignored.contains(dg.getGenericId())){
				return true;
			}
		}
		return false;
	}

	


}
