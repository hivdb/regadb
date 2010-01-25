package net.sf.regadb.io.db.tipraDarunavir;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.AttributeGroup;
import net.sf.regadb.db.AttributeNominalValue;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.TestNominalValue;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.TherapyMotivation;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Mappings;
import net.sf.regadb.io.db.util.NominalAttribute;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.util.frequency.Frequency;
import net.sf.regadb.util.settings.RegaDBSettings;
//copy pasted from ImportUcsc
public class ImportSpain 
{
	//DB table names


	private String patientTableName = "patients";
	private String analysisCd4TableName = "cd4_transposed";
	private String analysisVlTableName = "vl_transposed";
	private String hivTherapyTableName = "therapy";
	private String sequencesTableName = "genotypes_with_dates";

	//DB tables
	private Table patientTable;
	private Table analysisCd4Table;
	private Table analysisVlTable;
	private Table hivTherapyTable;
	private Table sequencesTable;


	//Translation mapping tables
	private Table riskGroupTable;
	private Table institutesTable;


	private HashMap<String, Patient> patientMap = new HashMap<String, Patient>();
	private HashMap<String, ViralIsolate> viralIsolateHM = new HashMap<String, ViralIsolate>();

	private List<DrugGeneric> regaDrugGenerics;

	private List<Attribute> regadbAttributes;


	private Mappings mappings;

	private AttributeGroup regadb = new AttributeGroup("RegaDB");
	private AttributeGroup tipranavirStudy = new AttributeGroup("TPVDRVStudy");

	private TestNominalValue posSeroStatus;


	public static void main(String [] args) 
	{
		try
		{
			if(args.length != 2) 
			{
				System.err.println("Usage: ImportSpainTipranavir workingDirectory collaboratingInstitute");
				System.exit(0);
			}
			 RegaDBSettings.createInstance();
		     RegaDBSettings.getInstance().getProxyConfig().initProxySettings();
			String mappingPath = "/home/rsanged0/git/regadb/regadb-io-db/src/net/sf/regadb/io/db/spain/mappings/";
			ImportSpain imp = new  ImportSpain();

			imp.getData(new File(args[0]),  mappingPath, args[1]);
		}
		catch(Exception e)
		{
			ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
		}
	}

	public void getData(File workingDirectory,  String mappingBasePath, String institute)
	{
		//Just for testing purposes...otherwise remove
		ConsoleLogger.getInstance().setInfoEnabled(true);

		try
		{
			mappings = Mappings.getInstance(mappingBasePath);



			ConsoleLogger.getInstance().logInfo("Reading CSV files...");
			//Filling DB tables
			patientTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + patientTableName + ".csv");
			analysisCd4Table = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + analysisCd4TableName + ".csv");
			analysisVlTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + analysisVlTableName + ".csv");
			hivTherapyTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + hivTherapyTableName + ".csv");
			sequencesTable = Utils.readTable(workingDirectory.getAbsolutePath() + File.separatorChar + sequencesTableName + ".csv");


			ConsoleLogger.getInstance().logInfo("Initializing mapping tables...");
			//Filling translation mapping tables
			
			riskGroupTable = Utils.readTable(mappingBasePath + File.separatorChar + "riskgroup.mapping");
			institutesTable= Utils.readTable(mappingBasePath + File.separatorChar + "institute.mapping");
			ConsoleLogger.getInstance().logInfo("Retrieving attributes, drugs, and events...");
			regadbAttributes = Utils.prepareRegaDBAttributes();
			regaDrugGenerics = Utils.prepareRegaDrugGenerics();
	

			posSeroStatus = Utils.getNominalValue(StandardObjects.getHiv1SeroStatusTestType(), "Positive");

			ConsoleLogger.getInstance().logInfo("Migrating patient information...");
			handlePatientData(institute);
			ConsoleLogger.getInstance().logInfo("Successful");
			ConsoleLogger.getInstance().logInfo("Migrating Cd4 data...");
			handleCd4Data();
			ConsoleLogger.getInstance().logInfo("Successful");
			ConsoleLogger.getInstance().logInfo("Migrating VL data...");
			handleVlData();
			ConsoleLogger.getInstance().logInfo("Successful");
			ConsoleLogger.getInstance().logInfo("Migrating treatments...");
			handleTherapies();
			ConsoleLogger.getInstance().logInfo("Successful");
			ConsoleLogger.getInstance().logInfo("Processing sequences...");
			handleSequences();
			ConsoleLogger.getInstance().logInfo("Processed "+patientMap.size()+" patient(s).");
			ConsoleLogger.getInstance().logInfo("Successful");

			ConsoleLogger.getInstance().logInfo("Generating output xml file...");
			IOUtils.exportPatientsXML(patientMap.values(), workingDirectory.getAbsolutePath() + File.separatorChar + institute +"_patients.xml", ConsoleLogger.getInstance());
			IOUtils.exportNTXMLFromPatients(patientMap.values(), workingDirectory.getAbsolutePath() + File.separatorChar + institute +"_viralIsolates.xml", ConsoleLogger.getInstance());
			ConsoleLogger.getInstance().logInfo("Export finished.");
		}
		catch(Exception e)
		{
			ConsoleLogger.getInstance().logError("Unknown error: "+e.getMessage());
		}
	}

	private void handlePatientData(String collaboratorInstitute)
	{
		int CpatientID = Utils.findColumn(this.patientTable, "patient_id");
		int Csex = Utils.findColumn(this.patientTable, "SEX");
		int CbirthDate = Utils.findColumn(this.patientTable, "DBIRTH");
		int CriskGroup = Utils.findColumn(this.patientTable, "risk group");
		int CfirstTest = Utils.findColumn(this.patientTable, "Yera Dx HIV");
		int Ccdc = Utils.findColumn(this.patientTable, "CDC");

		NominalAttribute gender = new NominalAttribute("Gender", Csex, new String[] { "M", "F" },
				new String[] { "male", "female" } );
		gender.attribute.setAttributeGroup(regadb);

		NominalAttribute cdcA = new NominalAttribute("CDC", Ccdc, new String[] { "A1","A2",  "A3", "B2","B3", "C2","C3" },
				new String[] { "A1","A2",  "A3", "B2","B3", "C2","C3" } );
		cdcA.attribute.setAttributeGroup(tipranavirStudy);
		//NominalAttribute instituteAtt = new NominalAttribute("Institute of origin", 6, new String[]{collaboratorInstitute}, new String[]{collaboratorInstitute});
		NominalAttribute instituteAtt = new NominalAttribute("Institute of origin", institutesTable, tipranavirStudy, null);
		instituteAtt.attribute.setAttributeGroup(tipranavirStudy);
		NominalAttribute transmissionGroupA = new NominalAttribute("Transmission group", riskGroupTable, regadb, Utils.selectAttribute("Transmission group", regadbAttributes));    	

		for(int i = 1; i < this.patientTable.numRows(); i++)
		{
			String patientId = this.patientTable.valueAt(CpatientID, i);
			String sex = this.patientTable.valueAt(Csex, i);
			String birthDate = this.patientTable.valueAt(CbirthDate, i);
			String institute = collaboratorInstitute;
			String riskGroup = this.patientTable.valueAt(CriskGroup, i);
			String firstTest = this.patientTable.valueAt(CfirstTest, i);
			String cdc = this.patientTable.valueAt(Ccdc, i);

			if(!"".equals(patientId))
			{
				patientId = patientId.toUpperCase();

				Patient p = new Patient();
				p.setPatientId(patientId);
				//Utils.handlePatientAttributeValue(instituteAtt, institute, p);

				if(Utils.checkColumnValueForEmptiness("gender", sex, i, patientId))
				{
					AttributeNominalValue vv = gender.nominalValueMap.get(sex.toUpperCase().trim());

					if (vv != null) 
					{
						PatientAttributeValue v = p.createPatientAttributeValue(gender.attribute);
						v.setAttributeNominalValue(vv);
					}
					else 
					{
						ConsoleLogger.getInstance().logWarning("Unsupported attribute value (gender): "+sex);
					}
				}

				if(Utils.checkColumnValueForEmptiness("date of Birth", birthDate, i, patientId))
				{
					//p.setBirthDate(Utils.parseEnglishAccessDate(birthDate));
										Utils.setBirthDate(p,Utils.parseMMDDYY(birthDate));
				}





				if(Utils.checkColumnValueForEmptiness("risk group", riskGroup, i, patientId))
				{
					Utils.handlePatientAttributeValue(transmissionGroupA, riskGroup, p);
				}


				if(Utils.checkColumnValueForEmptiness("date of first positive HIV test", firstTest, i, patientId))
				{
					TestResult t = p.createTestResult(StandardObjects.getGenericHiv1SeroStatusTest());
					t.setTestNominalValue(posSeroStatus);
					t.setTestDate(Utils.parseYY(firstTest));
				}


				if(Utils.checkColumnValueForExistance("CDC value", cdc, i, patientId))
				{
					AttributeNominalValue vv = cdcA.nominalValueMap.get(cdc.toUpperCase().trim());

					if(vv != null) 
					{
						PatientAttributeValue v = p.createPatientAttributeValue(cdcA.attribute);
						v.setAttributeNominalValue(vv);
					} 
					else 
					{
						if(!"".equals(cdc))
							ConsoleLogger.getInstance().logWarning("Unsupported attribute value (CDC): "+cdc);
					}
				}
				if(Utils.checkColumnValueForExistance("Institute of origin", institute, i, patientId))
				{
					Utils.handlePatientAttributeValue(instituteAtt, institute, p);
					
				}
				patientMap.put(patientId, p);
			}
			else
			{
				ConsoleLogger.getInstance().logWarning("No patientID in row "+i+" present...Skipping data set.");
			}
		}
	}

	private void handleCd4Data()
	{
		int CCC4PatientID = Utils.findColumn(this.analysisCd4Table, "patient_id");
		int CAnalysisDate = Utils.findColumn(this.analysisCd4Table, "cd4Date");
		int CResult= Utils.findColumn(this.analysisCd4Table, "cd4");

		for(int i = 1; i < this.analysisCd4Table.numRows(); i++)
		{
			String patientID = this.analysisCd4Table.valueAt(CCC4PatientID, i);
			String analysisDate = this.analysisCd4Table.valueAt(CAnalysisDate, i);
			String result = this.analysisCd4Table.valueAt(CResult, i);

			patientID = patientID.toUpperCase();

			Patient p = patientMap.get(patientID);

			if(p == null)
				ConsoleLogger.getInstance().logWarning("No patient with id "+patientID+" found.");


			//CD4
			if (Utils.checkColumnValueForEmptiness("CD4 test result (�L)", result, i, patientID) && Utils.checkCDValue(result, i, patientID)) 
			{
				TestResult t = p.createTestResult(StandardObjects.getGenericCD4Test());
				t.setValue(result.replace(',', '.'));
				t.setTestDate(Utils.parseMMDDYY(analysisDate));
			}
		}
	}

	private void handleVlData()
	{
		int CCC4PatientID = Utils.findColumn(this.analysisVlTable, "patient_id");
		int CAnalysisDate = Utils.findColumn(this.analysisVlTable, "vlDate");
		int CResult= Utils.findColumn(this.analysisVlTable, "vl");

		for(int i = 1; i < this.analysisVlTable.numRows(); i++)
		{
			String patientID = this.analysisVlTable.valueAt(CCC4PatientID, i);
			String analysisDate = this.analysisVlTable.valueAt(CAnalysisDate, i);
			String result = this.analysisVlTable.valueAt(CResult, i);

			patientID = patientID.toUpperCase();

			Patient p = patientMap.get(patientID);

			if(p == null)
				ConsoleLogger.getInstance().logWarning("No patient with id "+patientID+" found.");



			//Viral Load
			if(Utils.checkColumnValueForEmptiness("HIV RNA test result", result, i, patientID) && Utils.checkColumnValueForEmptiness("date ofHIV RNA test result", analysisDate, i, patientID))
			{
				try
				{
					TestResult vl = p.createTestResult(StandardObjects.getGenericHiv1ViralLoadTest());

					String value = null;

					double limit = 50.0;

					if(Double.parseDouble(result) <= limit)
						value = "<"+ limit;
					else
						value = "=";

					value += result;

					vl.setValue(value.replace(',', '.'));
					vl.setTestDate(Utils.parseDDMMYY(analysisDate));
				}
				catch(Exception e)
				{

				}
			}

		}
	}




	private void handleTherapies()
	{
		int ChivPatientID = Utils.findColumn(this.hivTherapyTable, "patient_id");
		int ChivStartTherapy = Utils.findColumn(this.hivTherapyTable, "startdate");
		int ChivStopTherapy = Utils.findColumn(this.hivTherapyTable, "stopdate");
		int ChivTherapyDrug = Utils.findColumn(this.hivTherapyTable, "therapy");
		
		
			
	
		
		

		for(int i = 1; i < this.hivTherapyTable.numRows(); i++)
		{
			String hivPatientID = this.hivTherapyTable.valueAt(ChivPatientID, i);
			String hivStartTherapy = this.hivTherapyTable.valueAt(ChivStartTherapy, i);
			String hivStopTherapy = this.hivTherapyTable.valueAt(ChivStopTherapy, i);
			

			if(!"".equals(hivPatientID))
			{
				hivPatientID = hivPatientID.toUpperCase();
				Date startDate = null;
				Date stopDate = null;

				if(Utils.checkColumnValueForEmptiness("start date of therapy", hivStartTherapy, i, hivPatientID))
				{
					startDate = Utils.parseMMDDYY(hivStartTherapy);
				}
				if(Utils.checkColumnValueForExistance("stop date of therapy", hivStopTherapy, i, hivPatientID))
				{
					stopDate = Utils.parseMMDDYY(hivStopTherapy);
				}

				String therapy = hivTherapyTable.valueAt(ChivTherapyDrug,i);
				String drugsarray[] = therapy.split(",");
				HashMap<String,String> drugs = new HashMap<String,String>();
				
				for(int j=0;j<drugsarray.length;j++)
				{
					//System.err.println(drugsarray[j]);
					drugs.put(drugsarray[j].toUpperCase().trim(), String.valueOf(j));
					Utils.checkDrugsWithRepos(drugsarray[j].trim(), regaDrugGenerics, mappings);
					
				}
				ArrayList<DrugGeneric> genDrugs = evaluateDrugs(drugs);

				if(hivPatientID != null)
				{
					storeTherapy(hivPatientID, startDate, stopDate, genDrugs, "");
				}
			}
			else
			{
				ConsoleLogger.getInstance().logWarning("No patient with id "+hivPatientID+" found.");
			}
		}
	}

	private ArrayList<DrugGeneric> evaluateDrugs(HashMap<String,String> drugs)
	{
		ArrayList<DrugGeneric> gDrugs = new ArrayList<DrugGeneric>();

		for(String drug : drugs.keySet())
		{
			if(!"".equals(drug))
			{
				getDrugMapping(gDrugs, drug, drugs.get(drug));
			}
		}

		return gDrugs;
	}

	private void getDrugMapping(ArrayList<DrugGeneric> gDrugs, String drug, String value)
	{
		//ConsoleLogger.getInstance().logInfo("Found drug "+drug+" with value "+value);

		boolean foundDrug = false;
		DrugGeneric genDrug = null;

		for(int j = 0; j < regaDrugGenerics.size(); j++)
		{
			genDrug = regaDrugGenerics.get(j);

			if(genDrug.getGenericId().toUpperCase().equals(drug.toUpperCase()))
			{
				foundDrug = true;

				gDrugs.add(genDrug);

				break;
			}
		}

		if(!foundDrug)
		{
			String mapping = mappings.getMapping("generic_drugs.mapping", drug);

			if(mapping != null) 
			{
				for(int i = 0; i < regaDrugGenerics.size(); i++)
				{
					genDrug = regaDrugGenerics.get(i);

					if(genDrug.getGenericId().toUpperCase().equals(mapping.toUpperCase()))
					{
						gDrugs.add(genDrug);
					}
				}
			}
		}
	}

	private ArrayList<DrugGeneric> validateDrugs(ArrayList<DrugGeneric> foundDrugs, String patientID)
	{
		ArrayList<DrugGeneric> vDrugs = new ArrayList<DrugGeneric>();

		for(int i = 0; i < foundDrugs.size(); i++)
		{
			DrugGeneric gDrug = (DrugGeneric)foundDrugs.get(i);

			List<DrugGeneric> subList = foundDrugs.subList(i+1, foundDrugs.size());

			if(subList.contains(gDrug))
			{
				ConsoleLogger.getInstance().logWarning("Found double drug entry "+(String)foundDrugs.get(i).getGenericId()+" for patient "+patientID+"");
			}
			else
			{
				vDrugs.add(gDrug);
			}
		}

		return vDrugs;
	}

	@SuppressWarnings("deprecation")
	private void storeTherapy(String patientId, Date startDate, Date endDate, ArrayList<DrugGeneric> foundDrugs, String motivation) 
	{
		Patient p = patientMap.get(patientId);

		if (p == null)
		{
			ConsoleLogger.getInstance().logWarning("No patient with id "+patientId+" found.");

			return;
		}

		if(foundDrugs == null)
		{
			ConsoleLogger.getInstance().logWarning("Something wrong with therapy mapping for patient '" + patientId + "': No valid drugs found...Storing anyway!");
		}

		if(startDate != null && endDate != null)
		{
			if(startDate.equals(endDate))
			{
				ConsoleLogger.getInstance().logWarning("Something wrong with treatment dates for patient '" + patientId + "': Therapy start " + startDate.toLocaleString() + " - Therapy end " + endDate.toLocaleString() + ": Dates are equal.");

				//Do not store here...
				return;
			}

			if(startDate.after(endDate))
			{
				ConsoleLogger.getInstance().logWarning("Something wrong with treatment dates for patient '" + patientId + "': Therapy start " + startDate.toLocaleString() + " - Therapy end " + endDate.toLocaleString() + ": End date is in the past.");

				//Do not store here...
				return;
			}
		}
		else if(startDate == null)
		{
			ConsoleLogger.getInstance().logError(patientId, "No corresponding start date available.");
		}

		Therapy t = p.createTherapy(startDate);
		t.setStopDate(endDate);

		String drugs = ""; 

		if(foundDrugs != null)
		{
			ArrayList<DrugGeneric> medicinsList = validateDrugs(foundDrugs, p.getPatientId());

			for (int i = 0; i < medicinsList.size(); i++) 
			{
				TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, (DrugGeneric)medicinsList.get(i)), 
						1.0, 
						false,
						false, 
						(long)Frequency.DAYS.getSeconds());

				t.getTherapyGenerics().add(tg);

				drugs += (String)medicinsList.get(i).getGenericId() + " ";
			}

			//ConsoleLogger.getInstance().logInfo(""+p.getPatientId()+" "+startDate.toLocaleString()+" "+drugs);
		}

		if(motivation != null && !motivation.equals(""))
		{
			//Still needs improvement, requires the mapping of motivation
			TherapyMotivation therapyMotivation = null;

			if(motivation.equals("toxicity"))
				therapyMotivation = new TherapyMotivation("Toxicity");
			else if(motivation.equals("unknown"))
				therapyMotivation = new TherapyMotivation("Unknown");
			else if(motivation.equals("patient's choice"))
				therapyMotivation = new TherapyMotivation("Patient's choice");
			else
				therapyMotivation = new TherapyMotivation("Other");

			if(therapyMotivation != null)
				t.setTherapyMotivation(therapyMotivation);
		}
	}

	private void addViralIsolateToPatients(int counter, String patientID, Date date, String seq)
	{
		Patient p = patientMap.get(patientID);

		if(p == null)
		{
			ConsoleLogger.getInstance().logError("No patient with id "+patientID+" found.");
		}

		ViralIsolate vi = p.createViralIsolate();
		vi.setSampleId(counter+"");
		vi.setSampleDate(date);

		NtSequence ntseq = new NtSequence();
		ntseq.setLabel("Sequence1");
		ntseq.setSequenceDate(date);
		ntseq.setNucleotides(seq);

		vi.getNtSequences().add(ntseq);

		viralIsolateHM.put(vi.getSampleId(), vi);
	}


	private void handleSequences() throws IOException
	{
		int counter = 0;
		int emptyCounter = 0;

		int CpatientID = Utils.findColumn(this.sequencesTable, "patient_id");
		int CsequenceDate = Utils.findColumn(this.sequencesTable, "Date res");
		int Csequence = Utils.findColumn(this.sequencesTable, "genotipo");

		for(int i = 1; i < this.sequencesTable.numRows(); i++)
		{
			String patientID = this.sequencesTable.valueAt(CpatientID, i);
			String sequenceDate = this.sequencesTable.valueAt(CsequenceDate, i);
			String sequence = this.sequencesTable.valueAt(Csequence, i);

			if(!"".equals(patientID))
			{
				patientID = patientID.toUpperCase();

				if(Utils.checkColumnValueForEmptiness("date of sequence analysis", sequenceDate, i, patientID))
				{
					Date date = Utils.parseMMDDYY(sequenceDate);

					if(date != null)
					{
						if(Utils.checkSequence(sequence, i, patientID))
						{
							String clearedSequ = Utils.clearNucleotides(sequence);

							if(!"".equals(clearedSequ))
							{
								addViralIsolateToPatients(counter, patientID, date, clearedSequ);

								counter++;
							}
						}
						else
						{
							ConsoleLogger.getInstance().logWarning("Empty seq for patient "+patientID+"");

							emptyCounter++;
						}
					}
					else
					{
						ConsoleLogger.getInstance().logWarning("No sequence date found for patient "+patientID+".");
					}
				}
				else
				{
					ConsoleLogger.getInstance().logWarning("No sequence date found for patient "+patientID+".");
				}
			}
			else
			{
				ConsoleLogger.getInstance().logWarning("No patientID in row "+i+" present...Skipping data set.");
			}
		}

		ConsoleLogger.getInstance().logInfo(""+counter+" sequence(s) added");
		ConsoleLogger.getInstance().logInfo(""+emptyCounter+" blank sequence(s) found");
	}

}