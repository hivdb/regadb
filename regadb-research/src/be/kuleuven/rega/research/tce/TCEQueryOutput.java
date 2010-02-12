package be.kuleuven.rega.research.tce;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.TableQueryOutput;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.DateUtils;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.hivgensim.queries.framework.utils.PatientUtils;
import net.sf.hivgensim.queries.framework.utils.TestUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.hivgensim.queries.framework.utils.ViralIsolateUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.settings.RegaDBSettings;

public class TCEQueryOutput extends TableQueryOutput<TCE> {

//	private Genome genome = StandardObjects.getHiv1Genome();
	private TestType cd4tt = StandardObjects.getCd4TestType();
	private TestType vltt = StandardObjects.getHiv1ViralLoadTestType();

	private List<DrugGeneric> genericDrugs = DrugGenericUtils.prepareRegaDrugGenerics();
//	private List<Test> resistanceTests = Utils.getResistanceTests();
//	private List<DrugGeneric> resistanceGenericDrugs = DrugGenericUtils.getDrugsSortedOnResistanceRanking(genericDrugs, true);

	private SimpleDateFormat dateOutputFormat = new SimpleDateFormat("yyyy-MM-dd");

	private boolean first = true;

//	private String organism;
	
	private TreeSet<String> alreadyIncludedViralIsolates = new TreeSet<String>();
	

	public TCEQueryOutput(Table out, File file, TableOutputType type, String organism) {
		super(out, file, type);
//		this.organism = organism;		
	}

	public void process(TCE tce) {
		if(first)
			addHeader();

		ViralIsolate vi = ViralIsolateUtils.closestToDate(tce.getPatient().getViralIsolates(), tce.getStartDate());
		if(vi==null || !DateUtils.betweenInterval(vi.getSampleDate(), DateUtils.addDaysToDate(tce.getStartDate(),-90), DateUtils.addDaysToDate(tce.getStartDate(),7))) {
			return;
		}
		if(alreadyIncludedViralIsolates.contains(vi.getSampleId())){
			return;
		}
		if(vi.getSampleId().equals("35606")){
			return;
		}
		alreadyIncludedViralIsolates.add(vi.getSampleId());

//		Map<String, String> resistanceResults = new HashMap<String, String>();

		addColumn(dateOutputFormat.format(tce.getStartDate()));
		addColumn(PatientUtils.getDatasource(tce.getPatient()).getDescription());
		addColumn(tce.getPatient().getPatientId());

		addColumn(vi.getSampleId());
		addColumn(dateOutputFormat.format(vi.getSampleDate()));

		addColumn(ViralIsolateUtils.getConcatenatedNucleotideSequence(vi));
		addColumn(ViralIsolateUtils.extractSubtype(vi));
//		for(TestResult tr : vi.getTestResults()) {
//			TestType tt = tr.getTest().getTestType();
//			if(Equals.isSameTestType(tt, StandardObjects.getGssTestType(genome))) {
//				resistanceResults.put(tr.getTest().getDescription()+"_"+tr.getDrugGeneric().getGenericId(), tr.getValue());
//			}
//		}
//
//		for(Test rt : resistanceTests) {
//			if(rt.getTestType().getGenome().getOrganismName().equals(organism) && rt.getDescription().startsWith("REGA"))					
//				for(DrugGeneric dg : resistanceGenericDrugs) {
//					addColumn(resistanceResults.get(rt.getDescription() + "_" + dg.getGenericId()));
//				}
//		}
		
		addColumn(TherapyUtils.daysExperienceWithDrugClass(tce.getTherapiesBefore(), "NRTI")+"");
		addColumn(TherapyUtils.daysExperienceWithDrugClass(tce.getTherapiesBefore(), "NNRTI")+"");
		addColumn(TherapyUtils.daysExperienceWithDrugClass(tce.getTherapiesBefore(), "PI")+"");
		//TODO is this correct?
		addColumn((tce.getTherapiesBefore().size()+1)+"");
		for(DrugGeneric dg : genericDrugs) {
			boolean found = false;
			for(DrugGeneric dg_tce : tce.getDrugs()) {
				if(dg_tce.getGenericId().equals(dg.getGenericId())) {
					found = true;
				}
			}
			addColumn(found?"yes":"no");
		}

		//baseline
		addTestResultBetweenInterval(tce.getStartDate(), -90, 7, tce, cd4tt);
		addTestResultBetweenInterval(tce.getStartDate(), -90, 7, tce, vltt);
		//8 weeks
		addTestResultBetweenInterval(DateUtils.addDaysToDate(tce.getStartDate(),8*7), -30, 30, tce, cd4tt);
		addTestResultBetweenInterval(DateUtils.addDaysToDate(tce.getStartDate(),8*7), -30, 30, tce, vltt);
		//12 weeks
		addTestResultBetweenInterval(DateUtils.addDaysToDate(tce.getStartDate(),12*7), -30, 30, tce, cd4tt);
		addTestResultBetweenInterval(DateUtils.addDaysToDate(tce.getStartDate(),12*7), -30, 30, tce, vltt);
		//24 weeks
		addTestResultBetweenInterval(DateUtils.addDaysToDate(tce.getStartDate(),24*7), -30, 30, tce, cd4tt);
		addTestResultBetweenInterval(DateUtils.addDaysToDate(tce.getStartDate(),24*7), -30, 30, tce, vltt);

		if(tce.getPatient().getBirthDate()!=null) {
			addColumn(this.dateOutputFormat.format(tce.getPatient().getBirthDate()));
		} else {
			addColumn("");
		}
		addColumn(PatientUtils.getPatientAttributeValue(tce.getPatient(), "Gender"));
		addColumn(PatientUtils.getPatientAttributeValue(tce.getPatient(), "Transmission group"));
		addColumn(PatientUtils.getPatientAttributeValue(tce.getPatient(), "Ethnicity"));
		addColumn(PatientUtils.getPatientAttributeValue(tce.getPatient(), "Country of origin"), true);
	}

	private void addHeader() {
		addColumn("start date");
		addColumn("data source");
		addColumn("patient id");
		addColumn("vi id");
		addColumn("vi date");
		addColumn("nt sequences");
		addColumn("subtype");
//		for(Test rt : resistanceTests) {
//			if(rt.getTestType().getGenome().getOrganismName().equals(organism) && rt.getDescription().startsWith("REGA")){					
//				for(DrugGeneric dg : resistanceGenericDrugs) {
//					addColumn(rt.getDescription().replace('.', '_') + "_" + dg.getGenericId());
//				}
//			}
//		}

		addColumn("# days of NRTI experience");
		addColumn("# days of NNRTI experience");
		addColumn("# days of PI experience");
		addColumn("# previous therapy switches");
		for(DrugGeneric dg : genericDrugs) {
			addColumn(dg.getGenericId());
		}

		addCD4VLHeader("baseline");
		addCD4VLHeader("8 weeks");
		addCD4VLHeader("12 weeks");
		addCD4VLHeader("24 weeks");

		addColumn("birthdate");
		addColumn("sex");
		addColumn("transmission");
		addColumn("ethnicity");
		addColumn("country of origin", true);
		first = false;
	}

	private void addCD4VLHeader(String timePoint) {
		addColumn(timePoint + " CD4 date");
		addColumn(timePoint + " CD4 value");
		addColumn(timePoint + " Viral Load date");
		addColumn(timePoint + " Viral Load value");
	}

	private void addTestResultBetweenInterval(Date d, int daysBefore, int daysAfter, TCE tce, TestType testType) {
		List<TestResult> trs = TestUtils.filterTestResults(tce.getPatient().getTestResults(), testType);
		List<TestResult> trs_interval = new ArrayList<TestResult>();
		for(TestResult tr_i : trs) {
			if(DateUtils.betweenInterval(tr_i.getTestDate(), DateUtils.addDaysToDate(d, daysBefore), DateUtils.addDaysToDate(d, daysAfter))) {
				trs_interval.add(tr_i);
			}
		}

		TestResult tr = TestUtils.closestToDate(tce.getStartDate(), trs_interval);

		//TODO
		//betweenOrEquals ???
		if(tr!=null) {
			addColumn(this.dateOutputFormat.format(tr.getTestDate()));
			addColumn(tr.getValue());
		} else {
			addColumn("");
			addColumn("");
		}
	}

	public static void main(String [] args) {
		if(args.length != 3 && args.length != 4){
			System.err.println("Usage: TCEQueryOutput [snapshot | uid passwd] output.table organism");
			System.exit(1);
		}
		RegaDBSettings.createInstance();
		QueryInput input;
		if(args.length == 3){
			input = new FromSnapshot(new File(args[0]),new TCEQuery(new TCEQueryOutput(new Table(), new File(args[1]), TableOutputType.CSV, args[2])));
		} else {
			input = new FromDatabase(args[0],args[1],new TCEQuery(new TCEQueryOutput(new Table(), new File(args[2]), TableOutputType.CSV, args[3])));
		}
		input.run();
	}


}
