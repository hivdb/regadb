package be.kuleuven.rega.research.lowLevelViralLoad;

import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.utils.TestUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.hivgensim.queries.framework.utils.ViralIsolateUtils;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.date.DateUtils;

public class LowLevelViralLoadQuery extends Query<Patient, LowLevelViralLoadData> {

	public static double MIN_VIRAL_LOAD = 50;
	public static double MAX_VIRAL_LOAD = 500;
	
	public static int DAY_DIFF_VIRAL_LOAD_THERAPY = 30;
	public static int DAY_DIFF_VIRAL_LOAD_SAMPLE = 30;

	private Set<Integer> viralLoadCopiesTests = new TreeSet<Integer>();
	private Set<Integer> viralLoadLog10Tests = new TreeSet<Integer>();

	private Set<Integer> piDrugGenerics = new TreeSet<Integer>();
	private Set<Integer> piDrugCommercials = new TreeSet<Integer>();
	
	protected LowLevelViralLoadQuery(IQuery<LowLevelViralLoadData> nextQuery, Transaction t) {
		super(nextQuery);
		loadStaticIndexLists(t);
	}
	
	public void process(Patient p){

		TreeSet<ViralIsolate> viralIsolates = new TreeSet<ViralIsolate>(ViralIsolateUtils.viralIsolateSortComparator);
		for(ViralIsolate vi : p.getViralIsolates()){
			viralIsolates.add(vi);
		}
		if(viralIsolates.size() < 2) //need 2 sequences
			return;
		
		TreeSet<Therapy> therapies = new TreeSet<Therapy>(
				TherapyUtils.therapySortComparator);
		for(Therapy therapy : p.getTherapies()){
			if(isPITherapy(therapy))
				therapies.add(therapy);
		}
		if(therapies.size() == 0) //need PI therapy
			return;

		TestResult undetectableViralLoad = null;
		TreeSet<TestResult> lowViralLoads = new TreeSet<TestResult>(
				TestUtils.testResultSortComparator);

		TreeSet<TestResult> viralLoads = new TreeSet<TestResult>(
				TestUtils.testResultSortComparator);
		for(TestResult testResult : p.getTestResults()){
			if(isViralLoadCopies(testResult)
					|| isViralLoadLog10(testResult))
				viralLoads.add(testResult);
		}
		
		for(TestResult viralLoad : viralLoads){
			if(undetectableViralLoad == null
					&& isUndetectableViralLoad(viralLoad)){
				undetectableViralLoad = viralLoad;
			}
			else if(undetectableViralLoad != null
					&& isLowViralLoad(viralLoad)){
				lowViralLoads.add(viralLoad);
			}
		}
		if(lowViralLoads.size() == 0) //need a low vl after an undetectable vl
			return;
		
		for(TestResult tr : lowViralLoads){
			
			//find a therapy during or no more than x days before vl date 
			for(Therapy th : therapies){
				Double diff = null;
				if(th.getStopDate() != null)
					diff = DateUtils.getDayDifference(th.getStopDate(), tr.getTestDate());
				
				if(tr.getTestDate().after(th.getStartDate())
						&& (diff == null || Math.round(diff) < (long)DAY_DIFF_VIRAL_LOAD_THERAPY)){
					
					//get viral isolate closest to vl date (within x days)
					ViralIsolate closestViralIsolate = getClosestViralIsolate(
							tr.getTestDate(), viralIsolates, DAY_DIFF_VIRAL_LOAD_SAMPLE);
					
					if(closestViralIsolate != null){
						ViralIsolate prevIsolate = getPrevious(closestViralIsolate, viralIsolates);
						if(prevIsolate != null){
							LowLevelViralLoadData data = new LowLevelViralLoadData();
							data.patient = p;
							data.lowViralLoad = tr;
							data.lowViralLoadIsolate = closestViralIsolate;
							data.preLowViralLoadIsolate = prevIsolate;
							data.therapy = th;
							data.undetectableViralLoad = undetectableViralLoad;
							
							System.out.println("match "+ p.getPatientId());
							getNextQuery().process(data);
							return;
						}
					}
				}
			}
		}
	}
	
	private ViralIsolate getClosestViralIsolate(Date date, TreeSet<ViralIsolate> viralIsolates, int maxDiff){
		ViralIsolate closest = null;
		double closestDiff = 0;

		for(ViralIsolate viralIsolate : viralIsolates){
			double diff = Math.abs(DateUtils.getDayDifference(date, viralIsolate.getSampleDate()));
			if(diff <= maxDiff
					&& (closest == null || diff < closestDiff)){
				closest = viralIsolate;
				closestDiff = diff;
			}
		}
		
		return closest;
	}
	
	private ViralIsolate getPrevious(ViralIsolate viralIsolate, TreeSet<ViralIsolate> viralIsolates){
		SortedSet<ViralIsolate> head = viralIsolates.headSet(viralIsolate);
		if(head.size() > 0)
			return head.last();
		return null;
	}

	public boolean isLowViralLoad(TestResult tr){
		if(isViralLoadCopies(tr)){
			return tr.getValue().charAt(0) == '='
				&& isLowViralLoadCopies(Double.parseDouble(tr.getValue().substring(1)));
		} else if(isViralLoadLog10(tr)){
			return tr.getValue().charAt(0) == '='
				&& isLowViralLoadCopies(
						Math.pow(10, Double.parseDouble(tr.getValue().substring(1))));
		}
		
		return false;
	}
	
	public boolean isUndetectableViralLoad(TestResult tr){
		if(isViralLoadCopies(tr) || isViralLoadLog10(tr)){
			return tr.getValue().charAt(0) == '<'
				|| Double.parseDouble(tr.getValue().substring(1)) == 0;
		}
		return false;
	}
	
	public boolean isViralLoadCopies(TestResult tr){
		return viralLoadCopiesTests.contains(tr.getTest().getTestIi());
	}
	
	public boolean isViralLoadLog10(TestResult tr){
		return viralLoadLog10Tests.contains(tr.getTest().getTestIi());
	}
	
	public boolean isLowViralLoadCopies(double copies){
		return copies >= MIN_VIRAL_LOAD && copies <= MAX_VIRAL_LOAD;
	}
	
	public boolean isPITherapy(Therapy therapy){
		for(TherapyGeneric tg : therapy.getTherapyGenerics())
			if(piDrugGenerics.contains(tg.getId().getDrugGeneric().getGenericIi()))
				return true;
		for(TherapyCommercial tc : therapy.getTherapyCommercials())
			if(piDrugCommercials.contains(tc.getId().getDrugCommercial().getCommercialIi()))
				return true;
		return false;
	}
	
	private void loadStaticIndexLists(Transaction t){
		TestType testType;
		
		testType = t.getTestType(
				StandardObjects.getViralLoadDescription(),
				StandardObjects.getHiv1Genome().getOrganismName());
		for(Test test : t.getTests(testType))
			viralLoadCopiesTests.add(test.getTestIi());
		
		testType = t.getTestType(
				StandardObjects.getViralLoadLog10Description(),
				StandardObjects.getHiv1Genome().getOrganismName());
		for(Test test : t.getTests(testType))
			viralLoadLog10Tests.add(test.getTestIi());
		
		int drugClassIi = t.getDrugClass("PI").getDrugClassIi();
		for(DrugGeneric dg : t.getGenericDrugs()){
			if(dg.getDrugClass().getDrugClassIi() == drugClassIi)
				piDrugGenerics.add(dg.getGenericIi());
		}
		
		for(DrugCommercial dc : t.getCommercialDrugs()){
			for(DrugGeneric dg : dc.getDrugGenerics()){
				if(piDrugGenerics.contains(dg.getGenericIi())){
					piDrugCommercials.add(dc.getCommercialIi());
					break;
				}
			}
		}
	}
}
