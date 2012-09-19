package net.sf.regadb.io.export.hicdep;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.db.login.DisabledUserException;
import net.sf.regadb.db.login.WrongPasswordException;
import net.sf.regadb.db.login.WrongUidException;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;
import net.sf.regadb.util.args.ValueArgument;
import net.sf.regadb.util.settings.RegaDBSettings;

import org.hibernate.Query;

public class HicdepExporter {

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
		InputStream is = null;
		is = this.getClass().getResourceAsStream("mappings/"+ name);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		SimpleCsvMapper m = new SimpleCsvMapper(br);
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return m;
	}
	
	private static final String ID = "id";
	private static final String BIRTH_DATE = "birth_date";
	private static final String GENDER = "gender";
	private static final String TRANSMISSION_GROUP = "transmission_group";
	private static final String GEOGRAPHIC_ORIGIN = "geographic_origin";
	private static final String ETHNICITY = "ethnicity";
	private static final String DEATH_DATE = "death_date";
	
	@SuppressWarnings("unchecked")
	public void export(String dataset){		
		Transaction t = login.createTransaction();
		
		Map<String, Attribute> attributes = new HashMap<String, Attribute>();
		attributes.put(BIRTH_DATE, StandardObjects.getBirthDateAttribute());
		attributes.put(GENDER, StandardObjects.getGenderAttribute());
		attributes.put(TRANSMISSION_GROUP, StandardObjects.getTransmissionGroupAttribute());
		attributes.put(GEOGRAPHIC_ORIGIN, StandardObjects.getGeoGraphicOriginAttribute());
		attributes.put(ETHNICITY, StandardObjects.getEthnicityAttribute());
		attributes.put(DEATH_DATE, StandardObjects.getDeathDateAttribute());
		
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
		queryString.append(")" +
				"from PatientImpl p " +
				"order by p.patientId");
		
		Query q = t.createQuery(queryString.toString());
		for (Map.Entry<String, Attribute> e : attributes.entrySet())
			q.setParameter(e.getKey() + "_name", e.getValue().getName());

		SimpleCsvMapper genderMap = getMapper("gender.csv");
		SimpleCsvMapper transmissionMap = getMapper("transmission_group.csv");
		
		LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
		for(Map<String, String> m : (List<Map<String,String>>)q.list()){
			row.clear();
			
			row.put("PATIENT", m.get(ID));
			row.put("CENTER", null);
			
			String birthDate = m.get(BIRTH_DATE);
			row.put("BIRTH_D", birthDate == null ? null : format(new Date(Long.parseLong(birthDate))));
			
			row.put("FRSVIS_D", null);
			row.put("ENROL_D", null);
		
			String gender = genderMap.b2a(m.get(GENDER));
			row.put("GENDER", gender);

			//TODO ask Stijn //""+ (height.nextInt(50) + 150);	//Random height
			row.put("HEIGH", "999");
			
			String transmission = transmissionMap.b2a(m.get(TRANSMISSION_GROUP));
			row.put("MODE", transmission);
			
			row.put("ORIGIN", null);
			row.put("ETHNIC", null);
			row.put("SEROCO_D", null);
			row.put("RECART_Y", null);
			row.put("AIDS_Y", null);
			row.put("AIDS_D", null);
			
			printInsert("tblBAS", row);

			row.clear();
			
			row.put("PATIENT", m.get(ID));
			
			row.put("DROP_Y", null); //TODO should be N ???
			row.put("DROP_D", null);
			row.put("DROP_RS", null);
			
			String deathDate = m.get(DEATH_DATE);
			row.put("DEATH_Y", deathDate == null ? "0" : "1");
			row.put("DEATH_D", deathDate == null ? null : format(new Date(Long.parseLong(deathDate))));
			
			row.put("AUTOP_Y", null); //TODO should be 9 ???
			row.put("DEATH_R1", null);
			row.put("DEATH_RC1", null); //TODO should be N ???
			
 			printInsert("tblLTFU", row);
		}
		
		q = t.createQuery("select" +
				" new map (" +
				"	p.patientId as patient_id," +
				" 	v.sampleId as isolate_id," +
				" 	v.sampleDate as isolate_date, " +
				" 	(select max(n.sequenceDate) from NtSequence n where n.viralIsolate = v) as last_sequence_date, "  +
				"	(select max(r.value) from NtSequence n join n.testResults r where n.viralIsolate = v and r.test.description = :subtype_description) as subtype_result" +
				" )" +
				"from PatientImpl p join p.viralIsolates v " +
				"order by p.patientId, v.sampleDate, v.id");
		q.setParameter("subtype_description", StandardObjects.getSubtypeTestDescription());
		
		for(Map<String, Object> m : (List<Map<String, Object>>)q.list()) {
			Date isolateDate = (Date)m.get("isolate_date");
			Date sequenceDate = (Date)m.get("last_sequence_date");
			
			row.clear();
			row.put("PATIENT", (String)m.get("patient_id"));
			row.put("SAMP_ID", (String)m.get("isolate_id"));
			row.put("SAMPLE_D", isolateDate == null ? null : format(isolateDate));
			row.put("SEQ_DT", sequenceDate == null ? null : format(sequenceDate));
			row.put("LAB", null);
			row.put("LIBRARY", null);
			row.put("REFSEQ", null); //TODO we can provide this???
			row.put("KIT", null); //TODO we can provide this???
			row.put("SOFTWARE", null); //TODO we can provide this???
			row.put("TESTTYPE", null); //TODO we can provide this???
			
			row.put("SUBTYPE", (String)m.get("subtype_result"));
			
			printInsert("tblLAB_RES", row);
			
			row.clear();
			
			row.put("PATIENT", (String)m.get("patient_id"));
			row.put("SAMP_LAB_D", isolateDate == null ? null : format(isolateDate));
			//TODO this should become BP, 
			//since this is the default
			//we have this info in RegaDB, and should use it
			row.put("SAMP_TYPE", "BS");
			row.put("SAMP_ID", (String)m.get("isolate_id"));
			row.put("SAMP_LAB", null);
			row.put("SAMP_FREEZE_D", null);
			row.put("SAMP_FREEZE_T", null);
			row.put("SAMP_ALIQ_NO", null);
			row.put("SAMP_ALIQ_SIZE", null);
			row.put("SAMP_ALIQ_U", null);
			
			printInsert("tblSAMPLES", row);
		}
		
		String[] columns, columns2, values, values2;
		
		columns = new String[]{
				"PATIENT",
				"VIS_D",
				"WEIGH",
				"GAIN_Y",
				"LOSS_Y"
		};
		values = new String[columns.length];
		
		columns2 = new String[]{
				"PATIENT",
				"RNA_D",
				"RNA_V"
//				"RNA_L",
//				"RNA_T"
		};
		values2 = new String[columns2.length];
		
		q = t.createQuery("select r.patient.patientId, r.testDate, tt.description, r.value from TestResult r join r.test t join t.testType tt" +
				" where tt.description = '"+ StandardObjects.getContactTestType().getDescription() +"'"+
				" order by r.patient.id, r.id");
		for(Object[] o : (List<Object[]>)q.list()){
			values[0] = values2[0] = (String)o[0];
			values[1] = values2[1] = format((Date)o[1]);
			
			if(o[2].equals(StandardObjects.getContactTestType().getDescription())){
				values[2] = "999";
				values[3] = "9";
				values[4] = "9";
			
				printInsert("tblVIS", columns, values);
			} else if(o[2].equals(StandardObjects.getViralLoadDescription())){
				values2[2] = ((String)o[3]).replace('>', '-').replace('>', '-').replaceAll("=", "");
				
				printInsert("tblLAB_RNA", columns2, values2);
			}
		}
		
		q = t.createQuery("select new map(tg.id.therapy.patient.patientId as patient_id, tg.id.therapy as therapy, tg as therapy_generic)" +
				" from TherapyGeneric tg" +
				" order by tg.id");
		for (Map<String, Object> db_row : (List<Map<String, Object>>)q.list()) {
			printGenericART(
					(String)db_row.get("patient_id"), 
					(Therapy)db_row.get("therapy"),
					((TherapyGeneric)db_row.get("therapy_generic")).getId().getDrugGeneric());
		}
		
		q = t.createQuery("select new map(tc.id.therapy.patient.patientId as patient_id, tc.id.therapy as therapy, tc as therapy_commercial)" +
				" from TherapyCommercial tc" +
				" order by tc.id");
		for (Map<String, Object> db_row : (List<Map<String, Object>>)q.list()) {
			printCommercialART(
						(String)db_row.get("patient_id"), 
						(Therapy)db_row.get("therapy"),
						((TherapyCommercial)db_row.get("therapy_commercial")));
		}
		
		columns = new String[]{
				"SAMP_ID",
				"SEQTYPE",
				"SEQ_START",
				"SEQ_STOP",
				"SEQ_NUQ"
		};
		values = new String[columns.length];
		q = t.createQuery("select n.viralIsolate.sampleId, p.abbreviation, a.firstAaPos, a.lastAaPos, n.nucleotides" +
				" from NtSequence n join n.aaSequences a join a.protein p" +
				" order by a.id");
		for(Object[] o : (List<Object[]>)q.list()){
			values[0] = (String)o[0];
			values[1] = (String)o[1];
			values[2] = ""+ o[2];
			values[3] = ""+ o[3];
			values[4] = (String)o[4];
			
			printInsert("tblLAB_RES_LVL_1", columns, values);
		}
		
		columns = new String[]{
				"SAMP_ID",
				"GENE",
				"AA_POS",
				"AA_POS_SUB",
				"AA_FOUND_1"
		};
		values = new String[columns.length];
		q = t.createQuery("select a.ntSequence.viralIsolate.sampleId, p.abbreviation, m.id.mutationPosition, m.aaMutation" +
				" from AaSequence a join a.aaMutations m join a.protein p " +
				" order by m.id ");
		for(Object[] o : (List<Object[]>)q.list()){
			values[0] = (String)o[0];
			values[1] = (String)o[1];
			values[2] = ""+ o[2];
			values[3] = null;
			values[4] = (String)o[3];
			
			printInsert("tblLAB_RES_LVL_2", columns, values);
		}
		
		q = t.createQuery("select a.ntSequence.viralIsolate.sampleId, p.abbreviation, i.id.insertionPosition, i.id.insertionOrder, i.aaInsertion" +
		" from AaSequence a join a.aaInsertions i join a.protein p" +
		" order by i.id");
		for(Object[] o : (List<Object[]>)q.list()){
			values[0] = (String)o[0];
			values[1] = (String)o[1];
			values[2] = ""+ o[2];
			values[3] = ""+ (char)(Character.getNumericValue('a') + (Short)o[3]);
			values[4] = (String)o[4];
			
			printInsert("tblLAB_RES_LVL_2", columns, values);
		}
		
		t.commit();
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
	
	private void printGenericART(String patientId, Therapy t, DrugGeneric dg) {
		SimpleCsvMapper genericDrugs = getMapper("generic-drugs.csv");
	
		LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();

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
		
		printInsert("tblART", row);
	}
	
	private void printCommercialART(String patientId, Therapy t, TherapyCommercial tc){
		String code = tc.getId().getDrugCommercial().getAtcCode();
		if ("".equals(code))
			code = null;

		if (code != null) {
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
	
			row.put("PATIENT", patientId);
			row.put("ART_ID", code);
			row.put("ART_SD", format(t.getStartDate()));
			row.put("ART_ED", format(t.getStopDate()));
			row.put("ART_RS", therapyMotivation(t));
			
			printInsert("tblART", row);
		} else {
			for (DrugGeneric dg : tc.getId().getDrugCommercial().getDrugGenerics())
				printGenericART(patientId, t, dg);
		}
	}

	public void printInsert(String table, LinkedHashMap<String, String> row) {
		printInsert(table, row.keySet().toArray(new String[row.keySet().size()]), row.values().toArray(new String[row.values().size()]));
	}
	
	protected void printInsert(String table, String[] columns, String[] values){
		if(columns.length != values.length)
			System.err.println("columns.length != values.length");
		
		PrintStream out = System.out;
		
		out.print("INSERT INTO ");
		out.print(table);
		out.print(" (");
		
		boolean first = true;
		for(String s : columns){
			if(first)
				first = false;
			else
				out.print(',');
			out.print(s);
		}
		
		out.print(") values (");
		
		first = true;
		for(String s : values){
			if(first)
				first = false;
			else
				out.print(',');
			
			if(s == null){
				out.print("NULL");
			}else{
				out.print('\'');
				out.print(s);
				out.print('\'');
			}
		}
		
		out.println(");");
	}
	
	private String format(Date date){
		return date == null ? null : sdf.format(date);
	}

	
	public static void main(String[] args) throws WrongUidException, WrongPasswordException, DisabledUserException{
		Arguments as = new Arguments();
		PositionalArgument user = as.addPositionalArgument("user", true);
		PositionalArgument pass = as.addPositionalArgument("pass", true);
		PositionalArgument data = as.addPositionalArgument("dataset", false);
		ValueArgument confDir = as.addValueArgument("c", "conf-dir", false);
		
		if(!as.handle(args))
			return;
		
		if(confDir.isSet())
			RegaDBSettings.createInstance(confDir.getValue());
		else
			RegaDBSettings.createInstance();
		
		Login login = Login.authenticate(user.getValue(), pass.getValue());
		
		HicdepExporter he = new HicdepCsvExporter(login, new File("/home/simbre1/Desktop"));
		he.export(data.getValue());
		
		login.closeSession();
	}
}
