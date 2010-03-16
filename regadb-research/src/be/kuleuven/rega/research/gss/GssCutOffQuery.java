package be.kuleuven.rega.research.gss;

import java.io.File;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.TableQueryOutput.TableOutputType;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.DateUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.hivgensim.queries.framework.utils.ViralIsolateUtils;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.util.settings.RegaDBSettings;

public class GssCutOffQuery extends Query<Patient,FailedTherapy> {
	
//	public static List<DrugGeneric> drugs; 
	
	private String[] ignoredDrugs = {"DDC","MVC","Unknown","PI","NRTI","NNRTI","aAPA","ADV"};
	
	public GssCutOffQuery(IQuery<FailedTherapy> nextQuery) {
		super(nextQuery);
	}
	
	public void process(Patient p) {
		int nb = 0;
		for(Therapy t : TherapyUtils.sortTherapiesByStartDate(p.getTherapies())) {
			FailedTherapy ft = new FailedTherapy(p, t, nb++);
			if(check(ft)){
				getNextQuery().process(ft);
			}
		}
	}
	
	public boolean check(FailedTherapy ft){		
		if(ft.getStartDate() == null || ft.getStopDate() == null){
			return false;
		}
		if(DateUtils.daysBetween(ft.getStartDate(), ft.getStopDate()) < 90){
			return false;
		}
		//contains unknown drugs
		for(String d : ignoredDrugs){
			if(TherapyUtils.containsDrugGeneric(ft.getTherapy(), d)){
				return false;
			}
		}		
		//no viral isolate		
		ViralIsolate vi = ViralIsolateUtils.closestToDate(ft.getPatient().getViralIsolates(), ft.getStartDate());
		if(vi==null || !DateUtils.betweenInterval(vi.getSampleDate(), DateUtils.addDaysToDate(ft.getStartDate(),-90), DateUtils.addDaysToDate(ft.getStartDate(),30))) {
			return false;
		}
		ft.setBeginViralIsolate(vi);
		vi = ViralIsolateUtils.closestToDate(ft.getPatient().getViralIsolates(), ft.getStopDate());
		if(vi==null || !DateUtils.betweenInterval(vi.getSampleDate(), DateUtils.addDaysToDate(ft.getStopDate(),-30), DateUtils.addDaysToDate(ft.getStopDate(),90))) {
			return false;
		}
		ft.setEndViralIsolate(vi);
		return true;
	}
	
	public static void main(String[] args) {
		RegaDBSettings.createInstance();
//		System.out.println("start");
//		drugs = DrugGenericUtils.getDrugsSortedOnResistanceRanking(DrugGenericUtils.prepareRegaDrugGenerics(),true);
//		System.out.println("start");
		new FromSnapshot(new File("/home/gbehey0/temp/20100315-snapshot"), new GssCutOffQuery(new FailedTherapyOutput(new Table(), new File("/home/gbehey0/out.csv"), TableOutputType.CSV))).run();
		
	}

}
