package net.sf.regadb.io.export.hicdep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.settings.HicdepConfig;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;

public abstract class HicdepExporter {

	private Login login;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public HicdepExporter(Login login){
		this.login = login;
	}
	
	private HashMap<String, SimpleCsvMapper> mappers = new HashMap<String, SimpleCsvMapper>();
	
	private SimpleCsvMapper getMapper(String name){
		SimpleCsvMapper mapper = mappers.get(name);
		if(mapper == null){
			mapper = createMapper(name);
			mappers.put(name, mapper);
		}
		
		return mapper;
	}
	
	private SimpleCsvMapper createMapper(String name){
		InputStream is = HicdepExporter.class.getResourceAsStream("mappings/"+ name);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		SimpleCsvMapper m = new SimpleCsvMapper(br);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return m;
	}
	
	private String patientsInDatasetSubquery(String datasetParam) {
		return 
				"select " +
				"	patient " +
                "from " +
                "	PatientImpl as patient " +
                "	join patient.patientDatasets as patient_dataset " +
                "	join patient_dataset.id.dataset as dataset " +
                "where " +
                "	dataset.description = :" + datasetParam;
	}
	
	private HicdepConfig config() {
		return RegaDBSettings.getInstance().getInstituteConfig().getHicdepConfig();
	}
	
	private void exportBASandLTFU(String dataset) {
		SimpleCsvMapper genderMap = getMapper("gender.csv");
		SimpleCsvMapper transmissionMap = getMapper("transmission_group.csv");

		final String ID = "id";
		final String BIRTH_DATE = "birth_date";
		final String GENDER = "gender";
		final String TRANSMISSION_GROUP = "transmission_group";
		final String GEOGRAPHIC_ORIGIN = "geographic_origin";
		final String ETHNICITY = "ethnicity";
		final String DEATH_DATE = "death_date";
		final String CENTER = "center";
		final String ENROL_D = "enrol_d";
		
		Map<String, Attribute> attributes = new HashMap<String, Attribute>();
		
		attributes.put(BIRTH_DATE, StandardObjects.getBirthDateAttribute());
		attributes.put(GENDER, StandardObjects.getGenderAttribute());
		attributes.put(TRANSMISSION_GROUP, StandardObjects.getTransmissionGroupAttribute());
		attributes.put(GEOGRAPHIC_ORIGIN, StandardObjects.getGeoGraphicOriginAttribute());
		attributes.put(ETHNICITY, StandardObjects.getEthnicityAttribute());
		attributes.put(DEATH_DATE, StandardObjects.getDeathDateAttribute());
		
		{
			Transaction t = login.createTransaction();
			
			HicdepConfig.Attribute center = config().getCenter();
			if (center != null)
				attributes.put(CENTER, t.getAttribute(center.name, center.group));
		}
		
		HicdepConfig.Event enrolEvent = config().getEnrol_d();

		StringBuilder queryString = new StringBuilder("select new map (p.patientId as " + ID);
		for (Map.Entry<String, Attribute> e : attributes.entrySet()) {
			String select;
			if (ValueTypes.getValueType(e.getValue().getValueType()) == ValueTypes.NOMINAL_VALUE) {
				select = 
					"select min(pav.attributeNominalValue.value) " +
					"from PatientAttributeValue pav join pav.attribute a " +
					"where pav.patient = p and a.name = :" + e.getKey() + "_name";
			} else {
				select = 
					"select min(pav.value) " +
					"from PatientAttributeValue pav join pav.attribute a " +
					"where pav.patient = p and a.name = :" + e.getKey() + "_name";
			}
			queryString.append(", \n (" + select + ") as " + e.getKey() + " ");	
		}
		
		if (enrolEvent != null) {
			String select  = 
					"select min(pev.startDate) " +
					"from PatientEventValue pev join pev.event e " +
					"where pev.patient = p and e.name = :" + ENROL_D;
			queryString.append(", \n (" + select + ") as " + ENROL_D + " ");	
		}
		
		queryString.append(")" +
				"from PatientImpl p " +
				"where p in (" + patientsInDatasetSubquery("dataset") + ")" + 
				"order by p.patientId");
		
		Transaction t = login.createTransaction();
		Query q = t.createQuery(queryString.toString());
		for (Map.Entry<String, Attribute> e : attributes.entrySet())
			q.setParameter(e.getKey() + "_name", e.getValue().getName());
		if (enrolEvent != null)
			q.setParameter(ENROL_D, enrolEvent.name);
		q.setParameter("dataset", dataset);
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			
			{
				LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
				
				row.put("PATIENT", (String)m.get(ID));
				
				String birthDate = (String)m.get(BIRTH_DATE);
				row.put("BIRTH_D", birthDate == null ? null : format(new Date(Long.parseLong(birthDate))));
				
				row.put("FRSVIS_D", null);
			
				String gender = genderMap.b2a((String)m.get(GENDER));
				row.put("GENDER", gender);
	
				row.put("HEIGH", "999");
				
				String transmission = transmissionMap.b2a((String)m.get(TRANSMISSION_GROUP));
				row.put("MODE", transmission);
				
				row.put("CENTER", (String)m.get(CENTER));
				Date enrol_d = (Date)m.get(ENROL_D);
				if (enrol_d != null)
					row.put("ENROL_D", format(enrol_d));
				
				row.put("ORIGIN", null);
				row.put("ETHNIC", null);
				row.put("SEROCO_D", null);
				row.put("RECART_Y", null);
				row.put("AIDS_Y", null);
				row.put("AIDS_D", null);
				
				printRow("tblBAS", row);
			}
			
			{
				LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
				
				row.put("PATIENT", (String)m.get(ID));
				
				row.put("DROP_Y", null);
				row.put("DROP_D", null);
				row.put("DROP_RS", null);
				
				String deathDate = (String)m.get(DEATH_DATE);
				row.put("DEATH_Y", deathDate == null ? "0" : "1");
				row.put("DEATH_D", deathDate == null ? null : format(new Date(Long.parseLong(deathDate))));
				
				row.put("AUTOP_Y", "9");
				row.put("DEATH_R1", "92");
				row.put("DEATH_RC1", "N");
				
	 			printRow("tblLTFU", row);
			}
			
			if (counter == 100) {
				counter = 0;
				t.clearCache();
			} else {
				++counter;
			}
		}		
		t.clearCache();
	}
	
	enum CD4_Type {
		Value,
		Percentage
	}
	
	private void exportLAB_CD4(CD4_Type type, String dataset) {
		final String patient_id = "patient_id";
		final String test_date = "test_date";
		final String value = "value";
		
		Transaction t = login.createTransaction();
		String qs = 
				"select " +
				"	new map (" +
				"		r.patient.patientId as " + patient_id + "," +
				" 		r.testDate as " + test_date + ", " +
				"		r.value as " + value + 
				"	)" +
				"from" +
				"	TestResult r " + 
				"where " +
				"	r.test.testType.description = :description " +
				"	and r.patient in (" + patientsInDatasetSubquery("dataset") + ")" + 
				"order " +
				"	by r.patient.id, r.id";
		
		Query q = t.createQuery(qs);
		q.setParameter("dataset", dataset);
		
		if (type == CD4_Type.Value)
			q.setParameter("description", StandardObjects.getCd4TestType().getDescription());
		else if (type == CD4_Type.Percentage)
			q.setParameter("description", StandardObjects.getCd4PercentageTestType().getDescription());
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
			
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			
			row.put("PATIENT", (String)m.get(patient_id));
			row.put("CD4_D", format((Date)m.get(test_date)));
			row.put("CD4_V", (String)m.get(value));
			
			if (type == CD4_Type.Value)
				row.put("CD4_U", "1");
			else if (type == CD4_Type.Percentage)
				row.put("CD4_U", "2");
				
			printRow("tblLAB_CD4", row);
			
			if (counter == 100) {
				counter = 0;
				t.clearCache();
			} else {
				++counter;
			}
		}
		t.clearCache();
	}
	
	private void exportLAB_RNA(String dataset) {
		final String patient_id = "patient_id";
		final String test_date = "test_date";
		final String value = "value";
		
		Transaction t = login.createTransaction();
		String qs = 
				"select " +
				"	new map (" +
				"		r.patient.patientId as " + patient_id + "," +
				" 		r.testDate as " + test_date + ", " +
				"		r.value as " + value + 
				"	)" +
				"from" +
				"	TestResult r " + 
				"where " +
				"	r.test.testType.description = :description and " +
				"	r.test.testType.genome.organismName = :organism " +
				"	and r.patient in (" + patientsInDatasetSubquery("dataset") + ")" + 
				"order " +
				"	by r.patient.id, r.id";
		
		Query q = t.createQuery(qs);
		q.setParameter("dataset", dataset);
		
		q.setParameter("description", StandardObjects.getViralLoadDescription());
		q.setParameter("organism", StandardObjects.getHiv1Genome().getOrganismName());
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
			
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			
			row.put("PATIENT", (String)m.get(patient_id));
			row.put("RNA_D", format((Date)m.get(test_date)));
			row.put("RNA_T", "99"); //unknown
	
			String v = (String)m.get(value);
			v = v.replace('<', '-');
			row.put("RNA_V", v);
				
			printRow("tblLAB_RNA", row);
			
			if (counter == 100) {
				counter = 0;
				t.clearCache();
			} else {
				++counter;
			}
		}
		t.clearCache();
	}
	
	private String virusType(String genomeName) {
		if (genomeName.startsWith("HIV"))
			return "1";
		else if (genomeName.startsWith("HCV"))
			return "2";
		else
			return null;
	}
	
	private void exportLAB_RES(String dataset) {
		final String patient_id = "patient_id";
		final String isolate_id = "isolate_id";
		final String isolate_date = "isolate_date";
		final String last_sequence_date = "last_sequence_date";
		final String virus_type = "virus_type";
		final String reference_sequence = "reference_sequence";
		final String subtype_result = "subtype_result";
		
		String query = 
				"select" +
				" new map (" +
				"	p.patientId as " + patient_id + "," +
				" 	v.sampleId as " + isolate_id + "," +
				" 	v.sampleDate as " + isolate_date + ", " +
				"	v.genome.organismName as " + virus_type + ", " + 
				"	v.genome.organismDescription as " + reference_sequence + ", " + 
				" 	( " +
				"		select max(n.sequenceDate) " +
				"		from NtSequence n " +
				"		where n.viralIsolate = v" +
				"	) as " + last_sequence_date + ", "  +
				"	( " +
				"		select max(r.value) " +
				"		from NtSequence n join n.testResults r " +
				"		where n.viralIsolate = v " +
				"			and r.test.description = :subtype_description" +
				"	) as " + subtype_result +
				" )" +
				"from " +
				"	PatientImpl p join p.viralIsolates v " +
				"where " +
				"	p in (" + patientsInDatasetSubquery("dataset") + ")" + 
				"order " +
				"	by p.patientId, v.sampleDate, v.id";
		
		Transaction t = login.createTransaction();
		
		Query q = t.createQuery(query);
		q.setParameter("subtype_description", StandardObjects.getSubtypeTestDescription());
		q.setParameter("dataset", dataset);
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			Date isolateDate = (Date)m.get(isolate_date);
			
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();

			Date sequenceDate = (Date) m.get(last_sequence_date);
			String patientId = (String) m.get(patient_id);
			String isolateId = (String) m.get(isolate_id);

			row.clear();
			row.put("PATIENT", patientId);
			row.put("TEST_ID", dataset + "_" + patientId + "_" + isolateId);
			row.put("SAMPLE_D", isolateDate == null ? null : format(isolateDate));
			row.put("SEQ_DT", sequenceDate == null ? null : format(sequenceDate));

			row.put("LIBRARY", null);
			row.put("REFSEQ", (String) m.get(reference_sequence));
			row.put("SOFTWARE", "RegaDB");

			row.put("VIRUSTYPE", virusType((String) m.get(virus_type)));
			row.put("SUBTYPE", (String) m.get("subtype_result"));

			row.put("KIT", null);
			row.put("LAB", null);
			row.put("TESTTYPE", null);

			row.put("SAMP_LAB", isolateId);

			printRow("tblLAB_RES", row);
			
			if (counter == 100) {
				counter = 0;
				t.clearCache();
			} else {
				++counter;
			}
			t.clearCache();
		}
	}
	
	private void exportSAMPLES(String dataset) {
		final String patient_id = "patient_id";
		final String isolate_id = "isolate_id";
		final String isolate_date = "isolate_date";
		
		String query = 
				"select" +
				" new map (" +
				"	p.patientId as " + patient_id + "," +
				" 	v.sampleId as " + isolate_id + "," +
				" 	v.sampleDate as " + isolate_date + 
				" )" +
				"from " +
				"	PatientImpl p join p.viralIsolates v " +
				"where " +
				"	p in (" + patientsInDatasetSubquery("dataset") + ")" + 
				"order " +
				"	by p.patientId, v.sampleDate, v.id";
		
		Transaction t = login.createTransaction();
		
		Query q = t.createQuery(query);
		q.setParameter("dataset", dataset);
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			Date isolateDate = (Date)m.get(isolate_date);
			
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();

			row.put("PATIENT", (String) m.get(patient_id));
			row.put("SAMP_LAB_D", isolateDate == null ? null
					: format(isolateDate));
			row.put("SAMP_TYPE", null);
			row.put("SAMP_ID", (String) m.get(isolate_id));
			row.put("SAMP_LAB", null);
			row.put("SAMP_FREEZE_D", null);
			row.put("SAMP_FREEZE_T", null);
			row.put("SAMP_ALIQ_NO", null);
			row.put("SAMP_ALIQ_SIZE", null);
			row.put("SAMP_ALIQ_U", null);

			printRow("tblSAMPLES", row);
			
			if (counter == 100) {
				counter = 0;
				t.clearCache();
			} else {
				++counter;
			}
			t.clearCache();
		}
	}
	
	private void exportVIS(String dataset) {
		final String patient_id = "patient_id";
		final String test_date = "test_date";
		
		Transaction t = login.createTransaction();
		String qs = 
				"select " +
				"	new map (" +
				"		r.patient.patientId as " + patient_id + "," +
				" 		r.testDate as " + test_date +
				"	)" +
				"from" +
				"	TestResult r " + 
				"where " +
				"	r.test.testType.description = :description " +
				"	and r.patient in (" + patientsInDatasetSubquery("dataset") + ")" + 
				"order " +
				"	by r.patient.id, r.id";
		Query q = t.createQuery(qs);
		
		q.setParameter("description", StandardObjects.getContactTestType().getDescription());
		q.setParameter("dataset", dataset);
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();

			row.put("PATIENT", (String)m.get(patient_id));
			row.put("VIS_D", format((Date)m.get(test_date)));
			row.put("WEIGH", "999");
			row.put("GAIN_Y", "9");
			row.put("LOSS_Y", "9");
			
			printRow("tblVIS", row);
			
			if (counter == 100) {
				counter = 0;
				t.clearCache();
			} else {
				++counter;
			}
		}
		t.clearCache();
	}
	
	private void exportGenericART(String dataset) {
		final String patient_id = "patient_id";
		final String therapy = "therapy";
		final String therapy_generic = "therapy_generic";
		
		String qs = 
			"select " +
			"	new map(" +
			"		tg.id.therapy.patient.patientId as " + patient_id + ", " +
			"		tg.id.therapy as " + therapy + ", " +
			"		tg as " + therapy_generic + 
			"	)" +
			"from " +
			"	TherapyGeneric tg " +
			"where " +
			"	tg.id.therapy.patient in (" + patientsInDatasetSubquery("dataset") + ")" +
			"order " +
			"	by tg.id";
		
		Transaction tr = login.createTransaction();
		Query q = tr.createQuery(qs);
		q.setParameter("dataset", dataset);
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			
			Therapy t = (Therapy)m.get(therapy);
			TherapyGeneric tg = (TherapyGeneric)m.get(therapy_generic);
			DrugGeneric dg = tg.getId().getDrugGeneric();
			
			printGenericART((String)m.get(patient_id), t, dg);
			
			if (counter == 100) {
				counter = 0;
				tr.clearCache();
			} else {
				++counter;
			}
		}
		tr.clearCache();
	}
	
	private void printGenericART(String patientId, Therapy t, DrugGeneric dg) {
		LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
	
		SimpleCsvMapper genericDrugs = getMapper("generic-drugs.csv");

		String genericId = dg.getGenericId();
		String atc = genericDrugs.b2a(genericId);
		if (atc == null)
			atc = dg.getAtcCode();
		if ("".equals(atc))
			atc = null;
		
		if (atc == null)
			throw new RuntimeException("No atc code could be found for generic drug '" + genericId + "'");
		
		row.put("PATIENT", patientId);
		row.put("ART_ID", atc);
		row.put("ART_SD", format(t.getStartDate()));
		row.put("ART_ED", format(t.getStopDate()));
		row.put("ART_RS", therapyMotivation(t));
		
		printRow("tblART", row);
	}
	
	private void exportCommercialART(String dataset) {
		final String patient_id = "patient_id";
		final String therapy = "therapy";
		final String therapy_commercial = "therapy_commercial";
		
		String qs =
				"select " +
				"	new map(" +
				"		tc.id.therapy.patient.patientId as " + patient_id + "," +
				"		tc.id.therapy as " + therapy + ", " +
				"		tc as " + therapy_commercial +
				"	) " + 
				"from " +
				"	TherapyCommercial tc " +
				"where " +
				"	tc.id.therapy.patient in (" + patientsInDatasetSubquery("dataset") + ")" +
				"order " +
				"	by tc.id";
		
		Transaction tr = login.createTransaction();
		Query q = tr.createQuery(qs);
		q.setParameter("dataset", dataset);
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
			
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			
			Therapy t = (Therapy)m.get(therapy);
			TherapyCommercial tc = (TherapyCommercial)m.get(therapy_commercial);
			
			String code = tc.getId().getDrugCommercial().getAtcCode();
			if ("".equals(code))
				code = null;

			if (code != null) {
				row.put("PATIENT", (String)m.get(patient_id));
				row.put("ART_ID", code);
				row.put("ART_SD", format(t.getStartDate()));
				row.put("ART_ED", format(t.getStopDate()));
				row.put("ART_RS", therapyMotivation(t));
				
				printRow("tblART", row);
			} else {
				for (DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics())
					printGenericART((String)m.get(patient_id), t, dg);
			}
			
			if (counter == 100) {
				counter = 0;
				tr.clearCache();
			} else {
				++counter;
			}
		}
		tr.clearCache();
	}
	
	private void exportLAB_RES_LVL_1(String dataset) {
		final String sample_id = "sample_id";
		final String protein = "protein";
		final String start = "start";
		final String stop = "stop";
		final String nucleotides = "nucleotides";
		
		String qs = 
				"select " +
				"	new map(" +
				"		n.viralIsolate.sampleId as " + sample_id + ", " +
				"		p.abbreviation as " + protein + ", " +
				"		a.firstAaPos as " + start + ", " +
				"		a.lastAaPos as " + stop + ", " +
				"		n.nucleotides as " + nucleotides +
				"	)" +
				"from " +
				"	NtSequence n join n.aaSequences a join a.protein p " +
				"where " +
				"	n.viralIsolate.patient in (" + patientsInDatasetSubquery("dataset") + ")" +
				"order " +
				"	by a.id";
		
		Transaction tr = login.createTransaction();
		Query q = tr.createQuery(qs);
		q.setParameter("dataset", dataset);
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
			
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			
			row.put("SAMP_ID", (String)m.get(sample_id));
			row.put("SEQTYPE", (String)m.get(protein));
			row.put("SEQ_START", m.get(start).toString());
			row.put("SEQ_STOP", m.get(stop).toString());
			row.put("SEQ_NUQ", (String)m.get(nucleotides));
			
			printRow("tblLAB_RES_LVL_1", row);
			
			if (counter == 100) {
				counter = 0;
				tr.clearCache();
			} else {
				++counter;
			}
		}
		tr.clearCache();
	}
	
	private void printMutationRow(String sampleId, String protein, int position, String acids, int insertion) {
		LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
		
		row.put("SAMP_ID", sampleId);
		row.put("GENE", protein);
		row.put("AA_POS", String.valueOf(position));
		String insertionString = insertion != -1 ? String.valueOf((char)((Character.getNumericValue('a') + insertion))) : null;
		row.put("AA_POS_SUB", insertionString);
		
		final byte acidColumns = 22;
		for (int i = 0; i < acidColumns; ++i) {
			char aa = ' ';
			if (i < acids.length())
				aa = acids.charAt(i);
			row.put("AA_FOUND_" + (i+1), String.valueOf(aa));
		}
		
		printRow("tblLAB_RES_LVL_2", row);
	}
	
	private void exportLAB_RES_LVL_2_mutations(String dataset) {
		final String sample_id = "sample_id";
		final String protein = "protein";
		final String aa_position = "aa_position";
		final String aa_mutation = "aa_mutation";
			
		String qs = 
				"select " +
				"	new map(" +
				"		a.ntSequence.viralIsolate.sampleId as " + sample_id + ", " +
				"		p.abbreviation as " + protein + ", " +
				"		m.id.mutationPosition as " + aa_position + ", " +
				"		m.aaMutation as " + aa_mutation + 
				"	)" + 
				" from " +
				"	AaSequence a join a.aaMutations m join a.protein p " +
				" where " +
				"	a.ntSequence.viralIsolate.patient in (" + patientsInDatasetSubquery("dataset") + ")" +
				" order " +
				"	by m.id ";
		
		Transaction tr = login.createTransaction();
		Query q = tr.createQuery(qs);
		q.setParameter("dataset", dataset);
		
		ScrollableResults sr = q.scroll();
		
		byte counter = 0;
		while(sr.next()){
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			
			if (m.get(aa_mutation) != null) {
				printMutationRow(
						(String)m.get(sample_id),
						(String)m.get(protein),
						((Short)m.get(aa_position)).intValue(),
						(String)m.get(aa_mutation),
						-1);
			}
			
			if (counter == 100) {
				counter = 0;
				tr.clearCache();
			} else {
				++counter;
			}
		}
		tr.clearCache();
	}
	
	private void exportLAB_RES_LVL_2_insertions(String dataset) {
		final String sample_id = "sample_id";
		final String protein = "protein";
		final String aa_insertion = "aa_insertion";
			
		String qs = 
				"select " +
				"	new map(" +
				"		a.ntSequence.viralIsolate.sampleId as " + sample_id + ", " +
				"		p.abbreviation as " + protein + ", " +
				"		i as " + aa_insertion +
				"	)" +
				"from AaSequence a join a.aaInsertions i join a.protein p " +
				"where " +
				"	a.ntSequence.viralIsolate.patient in (" + patientsInDatasetSubquery("dataset") + ")" +
				"order " +
				"	by i.id";
		
		Transaction tr = login.createTransaction();
		Query q = tr.createQuery(qs);
		q.setParameter("dataset", dataset);
		
		ScrollableResults sr = q.scroll(ScrollMode.FORWARD_ONLY);
		
		byte counter = 0;
		while(sr.next()){
			Map<String, Object> m = (Map<String,Object>)sr.get(0);
			AaInsertion insertion = (AaInsertion)m.get(aa_insertion);
			
			printMutationRow(
					(String)m.get(sample_id),
					(String)m.get(protein),
					insertion.getId().getInsertionPosition(),
					insertion.getAaInsertion(),
					insertion.getId().getInsertionOrder());
			
			if (counter == 100) {
				counter = 0;
				tr.clearCache();
			} else {
				++counter;
			}
		}
		tr.clearCache();
	}
	
	@SuppressWarnings("unchecked")
	public void export(String dataset){		
		System.err.println("Exporting BASandLTFU");
		exportBASandLTFU(dataset);
		System.err.println("Exporting LAB_RES");
		exportLAB_RES(dataset);
		System.err.println("Exporting SAMPLES");
		exportSAMPLES(dataset);
		System.err.println("Exporting VIS");
		exportVIS(dataset);
		System.err.println("Exporting ART");
		exportGenericART(dataset);
		exportCommercialART(dataset);
		System.err.println("Exporting LAB_RES_LVL");
		exportLAB_RES_LVL_1(dataset);
		exportLAB_RES_LVL_2_mutations(dataset);
		exportLAB_RES_LVL_2_insertions(dataset);
		System.err.println("Exporting LAB_CD4");
		exportLAB_CD4(CD4_Type.Value, dataset);
		exportLAB_CD4(CD4_Type.Percentage, dataset);
		System.err.println("Exporting LAB_RNA");
		exportLAB_RNA(dataset);
	}
	
	private String therapyMotivation(Therapy t) {
		String motivation = null;
		if (t.getTherapyMotivation() != null) {
			SimpleCsvMapper reason = getMapper("therapy_stopreason.csv");
			motivation = reason.b2a(t.getTherapyMotivation().getValue());
			if (motivation == null)
				throw new RuntimeException("Could not map therapy stop reason '" + t.getTherapyMotivation().getValue() + "'");
		}
		
		if (motivation != null)
			return motivation;
		else 
			return "";
	}

	public abstract void printRow(String table, LinkedHashMap<String, String> row);
	
	protected abstract void printRow(String table, String[] columns, String[] values);
	
	private String format(Date date) {
		return date == null ? null : sdf.format(date);
	}
}
