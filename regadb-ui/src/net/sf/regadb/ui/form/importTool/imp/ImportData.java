package net.sf.regadb.ui.form.importTool.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.regadb.db.Attribute;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugCommercial;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.Event;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.PatientAttributeValue;
import net.sf.regadb.db.PatientEventValue;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyCommercial;
import net.sf.regadb.db.TherapyCommercialId;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.db.ValueType;
import net.sf.regadb.db.ValueTypes;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.db.session.Login;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.service.IAnalysis;
import net.sf.regadb.service.wts.BlastAnalysis;
import net.sf.regadb.service.wts.FullAnalysis;
import net.sf.regadb.service.wts.ServiceException;
import net.sf.regadb.service.wts.BlastAnalysis.UnsupportedGenomeException;
import net.sf.regadb.service.wts.ServiceException.ServiceUnavailableException;
import net.sf.regadb.ui.form.importTool.data.DataProvider;
import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.form.importTool.data.SequenceDetails;
import net.sf.regadb.ui.form.singlePatient.ViralIsolateFormUtils;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.util.xls.ExcelTable;

import org.biojava.bio.seq.Sequence;
import org.biojavax.bio.seq.RichSequenceIterator;

import eu.webtoolkit.jwt.WString;

public class ImportData {
	private ImportDefinition definition;
	
	private DataProvider dataProvider;
	private Map<String, Sequence> sequences = new HashMap<String, Sequence>();
	private Dataset dataset;
	
	public ImportData(ImportDefinition definition, File xlsFile, File fastaFile, Dataset dataset) {
		ExcelTable table = new ExcelTable("dd/MM/yyyy");
		try {
			table.loadFile(xlsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.dataProvider = new DataProvider(table, definition.getScript());
		this.dataset = dataset;
		this.definition = definition;
		
		if (fastaFile.exists()) {
	        RichSequenceIterator xna = null;
	        
	        try {
	            xna = org.biojavax.bio.seq.RichSequence.IOTools.readFastaDNA(new BufferedReader(new FileReader(fastaFile)), null);
	        } catch (NoSuchElementException ex) {
	        	ex.printStackTrace();
	        } catch (FileNotFoundException ex) {
	        	ex.printStackTrace();
	        }
	        
	        if(xna!=null) { 
	            while(xna.hasNext()) {
	                try {
	                	Sequence s = xna.nextRichSequence();
	                	sequences.put(s.getName(), s);
	                } catch (Exception e) {
	                	e.printStackTrace();
	                }
	            }
	        }
		}
	}
	
	/**
	 * @param simulate
	 * @return an empty list in case there were no errors
	 */
	public List<WString> doImport(Transaction tr, boolean simulate) {
		Map<String, Test> testsMap = new HashMap<String, Test>();
		for (Test t : tr.getTests()) {
			testsMap.put(Rule.getTestName(t), t);
		}
		
		List<WString> errors = new ArrayList<WString>();
		List<Patient> patients = new ArrayList<Patient>();
		for (int i = 1; i < this.dataProvider.getNumberRows(); i++) {
			WString error = doImport(i, dataProvider.getRowValues(i), tr, testsMap, patients);
			if (error != null)
				errors.add(error);
		}
		
		if(errors.size() > 0)
			return errors;
		else {
			if (!simulate) {
				for (Patient p : patients) {
					for (ViralIsolate vi : p.getViralIsolates()) {
						Genome genome = blast(vi.getNtSequences().iterator().next());
						vi.setGenome(tr.getGenome(genome.getOrganismName()));
					}
					tr.save(p);
				}
				tr.commit();
				
				for (Patient p : patients) {
					for (ViralIsolate vi : p.getViralIsolates()) {
						Login copiedLogin = RegaDBMain.getApp().getLogin().copyLogin();
						try {
						NonThreadedFullAnalysis analysis = new NonThreadedFullAnalysis(vi, vi.getGenome());
						analysis.launch(copiedLogin);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							copiedLogin.closeSession();
						}
					}
				}
			}
			return errors;
		}
	}
	
	private Genome blast(NtSequence ntseq){
	    Genome genome = null;
	    //TODO check ALL sequences?
	    
        if(ntseq != null){
            BlastAnalysis blastAnalysis = new BlastAnalysis(ntseq, RegaDBMain.getApp().getLogin().getUid());
            try{
                blastAnalysis.launch();
                genome = blastAnalysis.getGenome();
            }
            catch(UnsupportedGenomeException e){
                return null;
            }
            catch(ServiceUnavailableException e){
                return null;
            }
            catch(ServiceException e){
                e.printStackTrace();
            }            
        }
        return genome;
	}
	
	public WString doImport(int row, Map<String, String> headerValueMap, Transaction t, Map<String, Test> testsMap, List<Patient> patients) {
		Patient p = null;
		Map<Attribute, String> attributes = new HashMap<Attribute, String>();
		Map<Integer, TestResult> testResults = new HashMap<Integer, TestResult>(); 
		Map<Integer, PatientEventValue> eventValues = new HashMap<Integer, PatientEventValue>();
		Map<Integer, Therapy> therapies = new HashMap<Integer, Therapy>();
		Map<Integer, ViralIsolate> isolates = new HashMap<Integer, ViralIsolate>(); 
		
		for (Rule r : definition.getRules()) {
			String header = r.getColumn();
			String value = headerValueMap.get(header).trim();
			Rule.Type type = r.getType();
			
			if (type == Rule.Type.PatientId) {
				if (value.equals(""))
					return WString.tr("importTool.import.emptyPatientId").arg(row).arg(header);
				
				p = t.getPatient(dataset, value);
				if (p == null) {
					p = new Patient();
					p.setPatientId(value);
					p.addDataset(this.dataset);
				}
			} else if (type == Rule.Type.AttributeValue) {
				if (!value.equals("")) {
					Attribute a = t.getAttributes(r.getTypeName()).get(0);
					value = handleValueType(r, a.getValueType(), value);
					if (value != null)
						attributes.put(a, value);
					else 
						return WString.tr("importTool.import.errorWithValueType").arg(a.getValueType().getDescription()).arg(row).arg(header);
				}
			} else if (type == Rule.Type.TestValue) {
				if (!value.equals("")) {
					Test test = testsMap.get(r.getTypeName());
					ValueType vt = test.getTestType().getValueType();
					value = handleValueType(r, vt, value);
					if (value != null) {
						TestResult tr = getTestResult(r.getNumber(), testResults);
						if (ValueTypes.getValueType(test.getTestType().getValueType()) == ValueTypes.NOMINAL_VALUE) 
							tr.setTestNominalValue(t.getTestNominalValue(test.getTestType(), value));
						else
							tr.setValue(value);
						tr.setTest(test);
					}
					else 
						return WString.tr("importTool.import.errorWithValueType").arg(vt.getDescription()).arg(row).arg(header);
				}
			} else if (type == Rule.Type.TestDate) {
				if (!value.equals("")) {
					Date d = handleDateValue(r, value);
					if (d == null)
						return WString.tr("importTool.import.errorWithDateValue").arg(row).arg(header);
					getTestResult(r.getNumber(), testResults).setTestDate(d);
				}
			} else if (type == Rule.Type.EventValue) {
				if (!value.equals("")) {
					Event event = t.getEvent(r.getTypeName());
					value = handleValueType(r, event.getValueType(), value);
					if (value != null) {
						PatientEventValue pev = getEventValue(r.getNumber(), eventValues);
						if (ValueTypes.getValueType(event.getValueType()) == ValueTypes.NOMINAL_VALUE) 
							pev.setEventNominalValue(t.getEventNominalValue(event, value));
						else
							pev.setValue(value);
						pev.setEvent(event);
					}
					else 
						return WString.tr("importTool.import.errorWithValueType").arg(event.getValueType().getDescription()).arg(row).arg(header);
				}
			} else if (type == Rule.Type.EventStartDate) {
				if (!value.equals("")) {
					Date d = handleDateValue(r, value);
					if (d == null)
						return WString.tr("importTool.import.errorWithDateValue").arg(row).arg(header);
					getEventValue(r.getNumber(), eventValues).setStartDate(d);
				}
			} else if (type == Rule.Type.EventEndDate) {
				if (!value.equals("")) {
					Date d = handleDateValue(r, value);
					if (d == null)
						return WString.tr("importTool.import.errorWithDateValue").arg(row).arg(header);
					getEventValue(r.getNumber(), eventValues).setEndDate(d);
				}
			} else if (type == Rule.Type.TherapyStartDate) {
				if (!value.equals("")) {
					Date d = handleDateValue(r, value);
					if (d == null)
						return WString.tr("importTool.import.errorWithDateValue").arg(row).arg(header);
					getTherapy(r.getNumber(), therapies).setStartDate(d);
				}
			} else if (type == Rule.Type.TherapyEndDate) {
				if (!value.equals("")) {
					Date d = handleDateValue(r, value);
					if (d == null)
						return WString.tr("importTool.import.errorWithDateValue").arg(row).arg(header);
					getTherapy(r.getNumber(), therapies).setStopDate(d);
				}
			} else if (type == Rule.Type.TherapyComment) {
				if (!value.equals("")) {
					getTherapy(r.getNumber(), therapies).setComment(value);
				}
			} else if (type == Rule.Type.TherapyStopMotivation) {
				if (!value.equals("")) {
					value = r.getMappingDetails().getMappings().get(value);
					if (value != null)
						getTherapy(r.getNumber(), therapies).setTherapyMotivation(t.getTherapyMotivation(value));
					else
						return WString.tr("importTool.import.errorWithTherapyMotivation").arg(row).arg(header);
				}
			} else if (type == Rule.Type.TherapyRegimen) {
				if (!value.equals("")) {
					String mappedDrug;
					Therapy therapy = getTherapy(r.getNumber(), therapies);
					StringTokenizer tokenizer = new StringTokenizer(value, r.getRegimenDetails().getDelimiter());
					while (tokenizer.hasMoreTokens()) {
						String drug = tokenizer.nextToken();
						if (drug.equals(""))
							continue;
						
						mappedDrug = r.getRegimenDetails().getMappings().get(drug);
						if (mappedDrug == null)
							return WString.tr("importTool.import.errorWithTherapyRegimen").arg(drug).arg(row).arg(header);
						else {
							DrugCommercial dc = t.getDrugCommercial(mappedDrug);
							DrugGeneric dg = t.getDrugGeneric(mappedDrug);
							if (dc != null) {
								TherapyCommercial tc = new TherapyCommercial(
										new TherapyCommercialId(therapy, dc),
										false, false);
								therapy.getTherapyCommercials().add(tc);
							} else if (dg != null) {
								TherapyGeneric tg = new TherapyGeneric(
										new TherapyGenericId(therapy, dg),
										false, false);
								therapy.getTherapyGenerics().add(tg);
							}
						}
					}
				}
			} else if (type == Rule.Type.ViralIsolateSampleDate) {
				if (!value.equals("")) {
					Date d = handleDateValue(r, value);
					if (d == null)
						return WString.tr("importTool.import.errorWithDateValue").arg(row).arg(header);
					getIsolate(r.getNumber(), isolates).setSampleDate(d);
				}
			} else if (type == Rule.Type.ViralIsolateSampleId) {
				if (!value.equals("")) {
					getIsolate(r.getNumber(), isolates).setSampleId(value);
				}
			} else if (type == Rule.Type.ViralIsolateSampleSequence) {
				if (!value.equals("")) {
					NtSequence ntseq = new NtSequence();
					if (r.getSequenceDetails().getRetrievalOptions() == SequenceDetails.SequenceRetrievalOptions.CSV) {
						ntseq.setNucleotides(Utils.clearNucleotides(value));
						ntseq.setLabel("Sequence " + (getIsolate(r.getNumber(), isolates).getNtSequences().size() + 1));
					} else {
						Sequence s = sequences.get(value);
						if (s == null)
							return WString.tr("importTool.import.sequenceNotFound").arg(value).arg(row).arg(header);
						ntseq.setNucleotides(s.seqString());
						ntseq.setLabel(value);
					}
					ntseq.setViralIsolate(getIsolate(r.getNumber(), isolates));
					getIsolate(r.getNumber(), isolates).getNtSequences().add(ntseq);
				}
			}
		}
		
		for (Map.Entry<Attribute, String> e : attributes.entrySet()) {
			PatientAttributeValue pav = p.createPatientAttributeValue(e.getKey());
			if (ValueTypes.getValueType(e.getKey().getValueType()) == ValueTypes.NOMINAL_VALUE) 
				pav.setAttributeNominalValue(t.getAttributeNominalValue(e.getKey(), e.getValue()));
			else 
				pav.setValue(e.getValue());
		}
		
		for (Map.Entry<Integer, TestResult> e : testResults.entrySet()) {
			if (e.getValue().getTest() != null) {
				if (e.getValue().getTestDate() == null) 
					return WString.tr("importTool.import.testResultMissingDate").arg(e.getValue().getTest().getDescription()).arg(row);
				else 
					p.addTestResult(e.getValue());
			}
		}
		
		for (Map.Entry<Integer, PatientEventValue> e : eventValues.entrySet()) {
			if (e.getValue().getEvent() != null) { 
				if (e.getValue().getStartDate() == null)
					return WString.tr("importTool.import.eventMissingStartDate").arg(e.getValue().getEvent().getName()).arg(row);
				else if (!isStartBeforeEnd(e.getValue().getStartDate(), e.getValue().getEndDate()))
					return WString.tr("importTool.import.eventEndBeforeStartDate").arg(e.getKey()).arg(row);
				else 
					p.addPatientEventValue(e.getValue());
			}
		}
		
		for (Map.Entry<Integer, Therapy> e : therapies.entrySet()) {
			if (e.getValue().getTherapyCommercials().size() > 0 || e.getValue().getTherapyGenerics().size() > 0) {
				if (e.getValue().getStartDate() == null)
					return WString.tr("importTool.import.therapyMissingStartDate").arg(row);
				else if (!isStartBeforeEnd(e.getValue().getStartDate(), e.getValue().getStopDate()))
					return WString.tr("importTool.import.therapyEndBeforeStartDate").arg(e.getKey()).arg(row);
				else
					p.addTherapy(e.getValue());
			}
		}
		
		for (Map.Entry<Integer, ViralIsolate> e : isolates.entrySet()) {
			if (e.getValue().getSampleId() != null && e.getValue().getNtSequences().size() > 0) {
				if (e.getValue().getSampleDate() == null)
					return WString.tr("importTool.import.viralIsolateDateMissing").arg(e.getValue().getSampleId()).arg(row);
				else if (!ViralIsolateFormUtils.checkSampleId(e.getValue().getSampleId(), e.getValue(), getDatasets(), t))
					return WString.tr("importTool.import.nonUniqueSampleId").arg(e.getValue().getSampleId()).arg(row);
				else 
					p.addViralIsolate(e.getValue());
			}
		}
		
		patients.add(p);
		
		return null;
	}
	
	private Set<Dataset> getDatasets() {
		Set<Dataset> datasets = new HashSet<Dataset>();
		datasets.add(this.dataset);
		return datasets;
	}
	
	private ViralIsolate getIsolate(int index, Map<Integer, ViralIsolate> isolates) {
		if (isolates.get(index) == null)
			isolates.put(index, new ViralIsolate());
		
		return isolates.get(index);
	}
	
	private Therapy getTherapy(int index, Map<Integer, Therapy> therapies) {
		if (therapies.get(index) == null)
			therapies.put(index, new Therapy());
		
		return therapies.get(index);
	}

	private PatientEventValue getEventValue(int index, Map<Integer, PatientEventValue> eventValues) {
		if (eventValues.get(index) == null)
			eventValues.put(index, new PatientEventValue());
		
		return eventValues.get(index);
	}
		
	private TestResult getTestResult(int index, Map<Integer, TestResult> testResults) {
		if (testResults.get(index) == null)
			testResults.put(index, new TestResult());
		
		return testResults.get(index);
	}
	
	private String handleValueType(Rule r, ValueType valueType, String value) {
		if (ValueTypes.getValueType(valueType) == ValueTypes.DATE) {
			Date d = handleDateValue(r, value);
			if (d != null)
				return d.getTime()+"";
			else 
				return null;
		} else if (ValueTypes.getValueType(valueType) == ValueTypes.LIMITED_NUMBER) {
			if (Character.isDigit(value.charAt(0)))
				value = "=" + value;
			
			if (ValueTypes.isValidLimitedNumber(value))
				return value;
			else 
				return null;
		} else if (ValueTypes.getValueType(valueType) == ValueTypes.NUMBER) {
			if (ValueTypes.isValidNumber(value))
				return value;
			else
				return null;
		} else if (ValueTypes.getValueType(valueType) == ValueTypes.NOMINAL_VALUE) {
			return r.getMappingDetails().getMappings().get(value);
		} else if (ValueTypes.getValueType(valueType) == ValueTypes.STRING) {
			return value;
		}
		return null;
	}
	
	private Date handleDateValue(Rule r, String value) {
		for (String df : r.getDateDetails().getDateFormats()) {
			SimpleDateFormat sdf = new SimpleDateFormat(df);
			try {
				return sdf.parse(value);
			} catch (ParseException e) {
			}
		}
		return null;
	}
	
	public boolean isStartBeforeEnd(Date start, Date end) {
		if (end == null)
			return true;
		else 
			return start.before(end);
	}
}
