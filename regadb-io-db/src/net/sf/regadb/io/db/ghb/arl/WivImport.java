package net.sf.regadb.io.db.ghb.arl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.Patient;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.export.hicdep.SimpleCsvMapper;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.io.util.WivObjects;
import net.sf.regadb.util.args.Arguments;
import net.sf.regadb.util.args.PositionalArgument;

public class WivImport {
	
	private SimpleCsvMapper eadPatcode;
	private Table wivTable;
	private File output;
	private Map<String, Patient> patients = new TreeMap<String,Patient>();

	public WivImport(File wiv, File dbEadPatcode, File output) throws FileNotFoundException, UnsupportedEncodingException{
		eadPatcode = new SimpleCsvMapper(dbEadPatcode);
		wivTable = Table.readTable(wiv.getAbsolutePath());
		this.output = output;
	}
	
	public void run(){
		Attribute aPatcode = new Attribute("PatCode");
		aPatcode.setValueType(StandardObjects.getStringValueType());
		aPatcode.setAttributeGroup(StandardObjects.getClinicalAttributeGroup());
		
		Attribute aBirthDate = StandardObjects.getBirthDateAttribute();
		Attribute aGender = StandardObjects.getGenderAttribute();
		
		int cPatcode = wivTable.findColumn("CODE_PAT");
		int cEad = wivTable.findColumn("pt EADnr");
		
		int cRefLabo = wivTable.findColumn("REF_LABO");
		int cDateTest = wivTable.findColumn("DATE_TEST");
		int cBirthDate = wivTable.findColumn("BIRTH_DATE");
		int cSex = wivTable.findColumn("SEX");
		int cNation = wivTable.findColumn("NATION");
		int cCountry = wivTable.findColumn("COUNTRY");
		int cResidB = wivTable.findColumn("RESID_B");
		int cAfkomst = wivTable.findColumn("AFKOMST");
		int cArrivalB = wivTable.findColumn("ARRIVAL_B");
		int cSexcontact = wivTable.findColumn("SEXCONTACT");
		int cSexpartner = wivTable.findColumn("SEXPARTNER");
		int cNatpartner = wivTable.findColumn("NATPARTNER");
		int cBloodborne = wivTable.findColumn("BLOODBORNE");
		int cYeartransfer = wivTable.findColumn("YEARTRANSFER");
		int cTranscountr = wivTable.findColumn("TRANSCOUNTR");
		int cChild = wivTable.findColumn("CHILD");
		int cProfrisk = wivTable.findColumn("PROFRISK");
		int cProbyear = wivTable.findColumn("PROBYEAR");
		int cProbcountr = wivTable.findColumn("PROBCOUNTR");
		int cStadClin = wivTable.findColumn("STAD_CLIN");
		int cReasontest = wivTable.findColumn("REASONTEST");
		int cFormOut = wivTable.findColumn("Formulier_OUT");
		int cFormIn = wivTable.findColumn("Formulier_IN");
		
		int cLympho = wivTable.findColumn("LYMPHO");
		int cVirload = wivTable.findColumn("VIRLOAD");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		
		for(int i=0; i<wivTable.numRows(); ++i){
			String patcode = wivTable.valueAt(cPatcode, i);
			String ead = wivTable.valueAt(cEad, i);
			
			if(!checkEadAndPatcode(ead, patcode))
				continue;
			
			Patient p = patients.get(ead);
			if(p == null){
				p = new Patient();
				p.setPatientId(ead);
				patients.put(ead, p);
				
				p.createPatientAttributeValue(aPatcode).setValue(patcode);
				p.createPatientAttributeValue(aBirthDate).setValue(getDate(dateFormat, wivTable.valueAt(cBirthDate, i)));
				
				AttributeNominalValue gender = getGender(wivTable.valueAt(cSex, i));
				if(gender != null)
					p.createPatientAttributeValue(aGender).setAttributeNominalValue(gender);
				
				WivObjects.createPatientAttributeValue(p, "REF_LABO", wivTable.valueAt(cRefLabo, i));
				WivObjects.createPatientAttributeValue(p, "DATE_TEST", getDate(dateFormat, wivTable.valueAt(cDateTest, i)));
				WivObjects.createPatientAttributeValue(p, "NATION", wivTable.valueAt(cNation, i));
				WivObjects.createPatientAttributeValue(p, "COUNTRY", wivTable.valueAt(cCountry, i));
				WivObjects.createPatientAttributeValue(p, "RESID_B", wivTable.valueAt(cResidB, i));
				WivObjects.createPatientAttributeValue(p, "ARRIVAL_B", wivTable.valueAt(cArrivalB, i));
				WivObjects.createPatientAttributeValue(p, "SEXCONTACT", wivTable.valueAt(cSexcontact, i));
				WivObjects.createPatientAttributeValue(p, "SEXPARTNER", wivTable.valueAt(cSexpartner, i));
				WivObjects.createPatientAttributeValue(p, "NATPARTNER", wivTable.valueAt(cNatpartner, i));
				WivObjects.createPatientAttributeValue(p, "BLOODBORNE", wivTable.valueAt(cBloodborne, i));
				WivObjects.createPatientAttributeValue(p, "YEARTRANSFER", wivTable.valueAt(cYeartransfer, i));
				WivObjects.createPatientAttributeValue(p, "TRANSCOUNTR", wivTable.valueAt(cTranscountr, i));
				WivObjects.createPatientAttributeValue(p, "CHILD", wivTable.valueAt(cChild, i));
				WivObjects.createPatientAttributeValue(p, "PROFRISK", wivTable.valueAt(cProfrisk, i));
				WivObjects.createPatientAttributeValue(p, "PROBYEAR", wivTable.valueAt(cProbyear, i));
				WivObjects.createPatientAttributeValue(p, "PROBCOUNTR", wivTable.valueAt(cProbcountr, i));
				WivObjects.createPatientAttributeValue(p, "STAD_CLIN", wivTable.valueAt(cStadClin, i));
				WivObjects.createPatientAttributeValue(p, "REASONTEST", wivTable.valueAt(cReasontest, i));
				WivObjects.createPatientAttributeValue(p, "FORM_OUT", getDate(dateFormat, wivTable.valueAt(cFormOut, i)));
				WivObjects.createPatientAttributeValue(p, "FORM_IN", getDate(dateFormat, wivTable.valueAt(cFormIn, i)));
				
				WivObjects.createPatientAttributeValue(p, "LYMPHO", wivTable.valueAt(cLympho, i));
				WivObjects.createPatientAttributeValue(p, "VIRLOAD", wivTable.valueAt(cVirload, i));
			}
		}
		
		IOUtils.exportPatientsXML(patients.values(), output.getAbsolutePath() + File.separatorChar +"patients.xml", ConsoleLogger.getInstance());
		
		System.err.println("done");
	}
	
	private AttributeNominalValue getGender(String value){
		if(value == null)
			return null;

		Attribute gender = StandardObjects.getGenderAttribute();

		value = value.trim().toUpperCase();
		if(value.equals("M"))
			return Utils.getNominalValue(gender, "male");
		if(value.equals("F"))
			return Utils.getNominalValue(gender, "female");
		
		return null;
	}
	
	private String getDate(DateFormat format, String value){
		if(value == null)
			return null;
		
		try {
			return format.parse(value).getTime() +"";
		} catch (ParseException e) {
			return null;
		}
	}

	public boolean checkEadAndPatcode(String ead, String patcode){
		String dbPatcode = eadPatcode.a2b(ead);
		String dbEad = eadPatcode.b2a(patcode);
		
		if(dbEad == null){
			System.err.print(patcode);
			if(dbPatcode != null)
				System.err.print(" != "+ dbPatcode);
			System.err.println();
			return false;
		} else {
			if(!dbEad.equals(ead)){
				System.err.println(ead +" != "+ dbEad);
				return false;
			}
			
			return true;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		Arguments as = new Arguments();
		
		PositionalArgument wivFile = as.addPositionalArgument("wiv-file", true);
		PositionalArgument eadFile = as.addPositionalArgument("ead-patcode-file", true);
		PositionalArgument output = as.addPositionalArgument("output-dir", false);
		
		if(!as.handle(args))
			return;
		
		File fWiv = new File(wivFile.getValue());
		File fEad = new File(eadFile.getValue());
		File fOut;
		
		if(output.isSet())
			fOut = new File(output.getValue());
		else
			fOut = fWiv.getParentFile();
		
		WivImport wi = new WivImport(fWiv, fEad, fOut);
		
		wi.run();
	}
}
