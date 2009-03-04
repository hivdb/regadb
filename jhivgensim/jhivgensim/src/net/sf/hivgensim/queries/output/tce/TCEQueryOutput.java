package net.sf.hivgensim.queries.output.tce;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.hivgensim.queries.framework.TableQueryOutput;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Test;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.service.wts.util.Utils;

public class TCEQueryOutput extends TableQueryOutput<TCE> {
	private SimpleDateFormat dateOutputFormat = new SimpleDateFormat(
			"dd/MM/yyyy");

	public TCEQueryOutput(Table out, File file, TableOutputType type) {
		super(out, file, type);
	}

	protected void generateOutput(List<TCE> tces) {
		List<DrugGeneric> genericDrugs = prepareRegaDrugGenerics();
		List<Test> resistanceTests = Utils.getResistanceTests();
		List<DrugGeneric> resistanceGenericDrugs = getDrugsSortedOnResistanceRanking(genericDrugs);
		
		//header
		addColumn("start date");
		addColumn("data source");
		addColumn("patient id");
		
		addColumn("vi id");
		addColumn("vi date");
		addColumn("nt sequences");
		addColumn("subtype");
		for(Test rt : resistanceTests) {
			for(DrugGeneric dg : resistanceGenericDrugs) {
				addColumn(rt.getDescription() + "_" + dg.getGenericId());
			}
		}
		
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
		
		//data
		ViralIsolate vi;
		Map<String, String> resistanceResults = new HashMap<String, String>();
		for(TCE tce : tces) {
			addColumn(dateOutputFormat.format(tce.getStartDate()));
			addColumn(getDatasource(tce.getPatient()).getDescription());
			addColumn(tce.getPatient().getPatientId());
			
			vi = this.closestToDate(tce.getPatient().getViralIsolates(), tce.getStartDate());
			if(vi!=null && betweenInterval(vi.getSampleDate(), addDaysToDate(tce.getStartDate(),-90), addDaysToDate(tce.getStartDate(),7))) {
				addColumn(vi.getSampleId());
				addColumn(dateOutputFormat.format(vi.getSampleDate()));
				
				String seqs = "";
				for(NtSequence ntSeq : vi.getNtSequences()) {
					seqs += ntSeq.getNucleotides() + "+";
				}
				addColumn(seqs.substring(0,seqs.length()-1));
				//TODO subtype
				addColumn("subtype");
				for(TestResult tr : vi.getTestResults()) {
					if(tr.getTest().getTestType().getDescription().equals("Genotypic Susceptibility Score (GSS)")) {
						resistanceResults.put(tr.getTest().getDescription()+"_"+tr.getDrugGeneric().getGenericId(), tr.getValue());
					}
				}
				
				for(Test rt : resistanceTests) {
					for(DrugGeneric dg : resistanceGenericDrugs) {
						addColumn(resistanceResults.get(rt.getDescription() + "_" + dg.getGenericId()));
					}
				}
			} else {
				addColumn("");
				addColumn("");
				addColumn("");
				addColumn("");
				for(Test rt : resistanceTests) {
					for(DrugGeneric dg : resistanceGenericDrugs) {
						addColumn("");
					}
				}
			}
			
			addColumn(daysExperienceWithDrugClass(tce.getTherapiesBefore(), "NRTI")+"");
			addColumn(daysExperienceWithDrugClass(tce.getTherapiesBefore(), "NNRTI")+"");
			addColumn(daysExperienceWithDrugClass(tce.getTherapiesBefore(), "PI")+"");
		}
	}

	private void addCD4VLHeader(String timePoint) {
		addColumn(timePoint + " CD4 date");
		addColumn(timePoint + " CD4 value");
		addColumn(timePoint + " Viral Load date");
		addColumn(timePoint + " Viral Load value");
	}
	
	// TODO
	// standard fun
	public long daysExperienceWithDrugClass(List<Therapy> therapies, String drugClass) {
		int days = 0;
		
		for(Therapy t : therapies) {
			if(QueryUtils.hasClassExperience(drugClass, t)) {
				days+=millisecondsToDays(t.getStopDate().getTime()-t.getStartDate().getTime());
			}
		}
		
		return days;
	}
	
	
	private long millisecondsToDays(long millis) {
		   final long MILLISECS_PER_MINUTE = 60*1000;
		   final long MILLISECS_PER_HOUR   = 60*MILLISECS_PER_MINUTE;
		   final long MILLISECS_PER_DAY = 24*MILLISECS_PER_HOUR;

		   return (long)(millis/MILLISECS_PER_DAY);
	}
	// TODO
	// standard fun
	public boolean betweenInterval(Date d, Date begin, Date end) {
		return d.after(begin) && d.before(end);
	}
	
	// TODO
	// standard fun
	public Date addDaysToDate(Date d, int daysToAdd) {
		Calendar c = Calendar.getInstance();
		
		c.setTime(d);
		c.add(Calendar.DAY_OF_MONTH, daysToAdd);
		
		return c.getTime();
	}
	
	// TODO
	// standard fun
	public ViralIsolate closestToDate(Set<ViralIsolate> viralIsolates, Date d) {
		long min = Long.MIN_VALUE;
		ViralIsolate closest = null;
		
		if(viralIsolates.size()==0)
			return null;
		
		long diff;
		for(ViralIsolate vi : viralIsolates) {
			diff = Math.abs(vi.getSampleDate().getTime()-d.getTime());
			if(diff<min) {
				min = diff;
				closest = vi;
			}
		}
		
		return closest;
	}
	
	// TODO
	// standard func
	public Dataset getDatasource(Patient p) {
		for (Dataset ds : p.getDatasets()) {
			if (ds.getClosedDate() == null) {
				return ds;
			}
		}
		
		return null;
	}

	// TODO
	// standard func
	public List<DrugGeneric> getDrugsSortedOnResistanceRanking(
			List<DrugGeneric> genericDrugs) {
		List<DrugGeneric> resistanceGenericDrugs = new ArrayList<DrugGeneric>();

		for (DrugGeneric dg : genericDrugs) {
			if (dg.getResistanceTableOrder() != null
					&& dg.getDrugClass().getResistanceTableOrder() != null) {
				resistanceGenericDrugs.add(dg);
			}
		}

		Collections.sort(resistanceGenericDrugs, new Comparator<DrugGeneric>() {
			public int compare(DrugGeneric dg1, DrugGeneric dg2) {
				if (dg1.getDrugClass().getClassId().equals(
						dg2.getDrugClass().getClassId())) {
					return dg1.getResistanceTableOrder().compareTo(
							dg2.getResistanceTableOrder());
				} else {
					return dg1.getDrugClass().getResistanceTableOrder()
							.compareTo(
									dg2.getDrugClass()
											.getResistanceTableOrder());
				}
			}
		});

		return resistanceGenericDrugs;
	}

	// TODO
	// standard func
	public List<DrugGeneric> prepareRegaDrugGenerics() {
		ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
		return imDrug.getGenericDrugs();
	}
}
