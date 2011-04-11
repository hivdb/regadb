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
import java.util.List;
import java.util.Random;

import net.sf.regadb.db.Transaction;
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
	
	@SuppressWarnings("unchecked")
	public void export(String dataset){
		
		String[] columns, columns2, values, values2;
		
		Transaction t = login.createTransaction();
		
		Query q = t.createQuery("select " +
				" p.patientId"+
				", (select min(pav.value) from PatientAttributeValue pav join pav.attribute a where pav.patient = p and a.name = '"+ StandardObjects.getBirthDateAttribute().getName() +"')"+
				", (select min(pav.attributeNominalValue.value) from PatientAttributeValue pav join pav.attribute a where pav.patient = p and a.name = '"+ StandardObjects.getGenderAttribute().getName() +"')"+
				", (select min(pav.attributeNominalValue.value) from PatientAttributeValue pav join pav.attribute a where pav.patient = p and a.name = '"+ StandardObjects.getTransmissionGroupAttribute().getName() +"')"+
				", (select min(pav.attributeNominalValue.value) from PatientAttributeValue pav join pav.attribute a where pav.patient = p and a.name = '"+ StandardObjects.getGeoGraphicOriginAttribute().getName() +"')"+
				", (select min(pav.attributeNominalValue.value) from PatientAttributeValue pav join pav.attribute a where pav.patient = p and a.name = '"+ StandardObjects.getEthnicityAttribute().getName() +"')"+
				", (select min(pav.value) from PatientAttributeValue pav join pav.attribute a where pav.patient = p and a.name = '"+ StandardObjects.getDeathDateAttribute().getName() +"')"+
				" from PatientImpl p order by p.patientId");
		
		columns = new String[]{
			"PATIENT",
			"CENTER",
			"BIRTH_D",
			"FRSVIS_D",
			"ENROL_D",
			"GENDER",
			"HEIGH",
			"MODE",
			"ORIGIN",
			"ETHNIC",
			"SEROCO_D",
			"RECART_Y",
			"AIDS_Y",
			"AIDS_D"
			};
		values = new String[columns.length];
		values[1] = values[3] = values[4] = values[8] = values[9] = values[10] = values[11] = values[12] = values[13] = null;
		
		columns2 = new String[]{
			"PATIENT",
			"DROP_Y",
			"DROP_D",
			"DROP_RS",
			"DEATH_Y",
			"DEATH_D",
			"AUTOP_Y",
			"DEATH_R1",
			"DEATH_RC1"
		};
		values2 = new String[columns2.length];
		values2[1] = values2[2] = values2[3] = values2[6] = values2[7] = values2[8] = null;
		
		SimpleCsvMapper genderMap = getMapper("gender.csv");
		SimpleCsvMapper transmissionMap = getMapper("transmission_group.csv");		

		Random height = new Random();
		
		for(Object[] o : (List<Object[]>)q.list()){
			values[0] = (String)o[0];
			values[2] = o[1] == null ? null : format(new Date(Long.parseLong((String)o[1])));
			values[5] = genderMap.b2a((String)o[2]);
			values[6] = ""+ (height.nextInt(50) + 150);	//Random height
			values[7] = transmissionMap.b2a((String)o[3]);
			
			printInsert("tblBAS", columns, values);

			values2[0] = values[0];
			values2[4] = o[6] == null ? "0" : "1";
			values2[5] = o[6] == null ? null : format(new Date(Long.parseLong((String)o[6])));
 			printInsert("tblLTFU", columns2, values2);
		}
		
		columns = new String[]{
			"PATIENT",
			"SAMP_ID",
			"SAMPLE_D",
			"SEQ_DT",
			"LAB",
			"LIBRARY",
			"REFSEQ",
			"KIT",
			"SOFTWARE",
			"TESTTYPE",
			"SUBTYPE"
		};
		values = new String[columns.length];
		values[4] = values[5] = values[6] = values[7] = values[8] = values[9] = null;
		
		columns2 = new String[]{
			"PATIENT",
			"SAMP_LAB_D",
			"SAMP_TYPE",
			"SAMP_ID",
			"SAMP_LAB",
			"SAMP_FREEZE_D",
			"SAMP_FREEZE_T",
			"SAMP_ALIQ_NO",
			"SAMP_ALIQ_SIZE",
			"SAMP_ALIQ_U"
		};
		values2 = new String[columns2.length];
		values2[4] = values2[5] = values2[6] = values2[7] = values2[8] = values2[9] = null;
		
		q = t.createQuery("select" +
				" p.patientId"+
				", v.sampleId"+
				", v.sampleDate"+
				", (select max(n.sequenceDate) from NtSequence n where n.viralIsolate = v)"+
				", (select max(r.value) from NtSequence n join n.testResults r where n.viralIsolate = v and r.test.description = '"+ StandardObjects.getSubtypeTestDescription() +"')"+
				" from PatientImpl p join p.viralIsolates v order by p.patientId, v.sampleDate");
		
		for(Object[] o : (List<Object[]>)q.list()){
			values[0] = (String)o[0];
			values[1] = (String)o[1];
			values[2] = format((Date)o[2]);
			values[3] = o[3] == null ? null : format((Date)o[3]);
			values[10] = (String)o[4];
			
			printInsert("tblLAB_RES", columns, values);
			
			values2[0] = values[0];
			values2[1] = values[2];
			values2[2] = "BS";
			values2[3] = values[1];
			
			printInsert("tblSAMPLES", columns2, values2);
		}
		
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
				" ");
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
		
		q = t.createQuery("select t.patient.patientId, t.startDate, t.stopDate, tg.id.drugGeneric.genericId, t.therapyMotivation.value" +
				" from Therapy t join t.therapyGenerics tg" +
				" order by t.patient.patientId, t.startDate");
		printART((List<Object[]>)q.list());
		
		q = t.createQuery("select t.patient.patientId, t.startDate, t.stopDate, dg.genericId, t.therapyMotivation.value" +
				" from Therapy t join t.therapyCommercials tc join tc.id.drugCommercial.drugGenerics dg" +
				" order by t.patient.patientId, t.startDate");
		printART((List<Object[]>)q.list());
		
		
		columns = new String[]{
				"SAMP_ID",
				"SEQTYPE",
				"SEQ_START",
				"SEQ_STOP",
				"SEQ_NUQ"
		};
		values = new String[columns.length];
		q = t.createQuery("select n.viralIsolate.sampleId, p.abbreviation, a.firstAaPos, a.lastAaPos, n.nucleotides" +
				" from NtSequence n join n.aaSequences a join a.protein p");
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
				" from AaSequence a join a.aaMutations m join a.protein p");
		for(Object[] o : (List<Object[]>)q.list()){
			values[0] = (String)o[0];
			values[1] = (String)o[1];
			values[2] = ""+ o[2];
			values[3] = null;
			values[4] = (String)o[3];
			
			printInsert("tblLAB_RES_LVL_2", columns, values);
		}
		
		q = t.createQuery("select a.ntSequence.viralIsolate.sampleId, p.abbreviation, i.id.insertionPosition, i.id.insertionOrder, i.aaInsertion" +
		" from AaSequence a join a.aaInsertions i join a.protein p");
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
	
	private void printART(List<Object[]> queryResult){
		String[] columns = new String[]{
				"PATIENT",
				"ART_ID",
				"ART_SD",
				"ART_ED",
				"ART_RS"
		};
		String[] values = new String[columns.length];
		
		SimpleCsvMapper reason = getMapper("therapy_stopreason.csv");
		SimpleCsvMapper drugs = getMapper("drugs.csv");
		
		for(Object[] o : queryResult){
			values[0] = (String)o[0];
			values[1] = drugs.a2b((String)o[1]);
			values[2] = format((Date)o[2]);
			values[3] = format((Date)o[3]);
			values[4] = reason.a2b((String)o[4]);
			
			printInsert("tblART", columns, values);
		}
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
		
		HicdepCsvExporter he = new HicdepCsvExporter(login, new File("/home/simbre1/Desktop"));
		he.export(data.getValue());
		he.close();
		
		login.closeSession();
	}
}
