package be.kuleuven.rega.research.tce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.DateUtils;
import net.sf.hivgensim.queries.framework.utils.PatientUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.hivgensim.queries.framework.utils.ViralIsolateUtils;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;

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
			
			if(precheck(tce)){
				getNextQuery().process(tce);
//				return;
			}
			

			formerTherapies.add(t);
		}
		formerTherapies.clear();

	}
	
	private TestType vltt = StandardObjects.getHiv1ViralLoadTestType();
	
	public boolean precheck(TCE tce){
		//has ignored drugs
		List<String> ignored = Arrays.asList("PI","NRTI","NNRTI","Unknown","ADV","aAPA","R82913","CPV","MVC","RTG","T20");
		for(DrugGeneric dg : tce.getDrugs()){
			if(ignored.contains(dg.getGenericId())){
				return false;
			}
		}
		//no viral isolate		
		ViralIsolate vi = ViralIsolateUtils.closestToDate(tce.getPatient().getViralIsolates(), tce.getStartDate());
		if(vi==null || !DateUtils.betweenInterval(vi.getSampleDate(), DateUtils.addDaysToDate(tce.getStartDate(),-90), DateUtils.addDaysToDate(tce.getStartDate(),7))) {
			return false;
		}
		//integrase sequence
		if(vi.getSampleId().equals("35606")){
			return false;
		}
		//no baseline
		if(tce.getTestResultBetweenInterval(-90, 7, vltt) == null){
			return false;
		}
		//no followup
		if(tce.getTestResultBetweenInterval(DateUtils.addDaysToDate(tce.getStartDate(),8*7), -30, 30, vltt) == null
			&&	tce.getTestResultBetweenInterval(DateUtils.addDaysToDate(tce.getStartDate(),12*7), -30, 30, vltt) == null
			&&	tce.getTestResultBetweenInterval(DateUtils.addDaysToDate(tce.getStartDate(),24*7), -30, 30, vltt) == null){
			return false;
		}
		return true;
	}

	


}
