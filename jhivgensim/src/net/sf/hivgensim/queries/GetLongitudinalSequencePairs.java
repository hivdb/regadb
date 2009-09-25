package net.sf.hivgensim.queries;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.TableQueryOutput.TableOutputType;
import net.sf.hivgensim.queries.framework.datatypes.SequencePair;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.hivgensim.queries.framework.utils.NtSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.PatientUtils;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.hivgensim.queries.output.SequencePairsTableOutput;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

public class GetLongitudinalSequencePairs extends Query<Patient,SequencePair> {

	private String[] drugs;
	private String[] drugclasses;
	private String region;
	private String organism;

	public GetLongitudinalSequencePairs(String drug, String drugclasses, String organism, String region, IQuery<SequencePair> nextQuery){
		this(new String[]{drug},new String[]{drugclasses},organism,region,nextQuery);
	}
	
	public GetLongitudinalSequencePairs(String[] drugs, String[] drugclasses, String organism, String region,IQuery<SequencePair> nextQuery){
		super(nextQuery);
		this.drugs = drugs;
		this.drugclasses = drugclasses;
		this.region = region;
		this.organism = organism;
	}

	@Override
	public void process(Patient input) {
		Set<NtSequence> sequences = PatientUtils.getSequences(input);
		ArrayList<NtSequence> naiveSequences = new ArrayList<NtSequence>();
		ArrayList<Set<DrugGeneric>> naiveHistories = new ArrayList<Set<DrugGeneric>>();
		ArrayList<NtSequence> treatedSequences = new ArrayList<NtSequence>();
		ArrayList<Set<DrugGeneric>> treatedHistories = new ArrayList<Set<DrugGeneric>>();
		
		for(NtSequence seq : NtSequenceUtils.sort(sequences)){
			if(!NtSequenceUtils.coversRegion(seq, organism, region)){
				continue;
			}
			
			Set<DrugGeneric> history = new HashSet<DrugGeneric>();
			for(Therapy t : TherapyUtils.sortTherapies(input.getTherapies())){
				if(t.getStartDate().before(seq.getViralIsolate().getSampleDate())){
					history.addAll(TherapyUtils.allDrugGenerics(t));
				}else{
					break;
				}
			}
			
			if(DrugGenericUtils.containsDrugsFromDrugClasses(history, drugclasses)){
				if(DrugGenericUtils.containsOnlyFromDrugClass(history, drugclasses, drugs)){
					treatedSequences.add(seq);
					treatedHistories.add(history);
				}else{
					
				}
			}else{
				naiveSequences.add(seq);
				naiveHistories.add(history);
			}
		}		

		if(naiveSequences.size() > 0 && treatedSequences.size() > 0){
			NtSequence naive = naiveSequences.get(naiveSequences.size()-1);
			NtSequence treated = treatedSequences.get(treatedSequences.size()-1);
			Set<DrugGeneric> naiveHistory = naiveHistories.get(naiveSequences.size()-1);
			Set<DrugGeneric> treatedHistory = treatedHistories.get(treatedSequences.size()-1);
			Set<DrugGeneric> difference = new HashSet<DrugGeneric>();
			difference.addAll(treatedHistory);
			difference.removeAll(naiveHistory);
			String regimen = DrugGenericUtils.toString(difference);
			SequencePair pair = new SequencePair(input,naive,treated,regimen);
			for(String drug : drugs){
				pair.addResistance(drug,NtSequenceUtils.resistance(naive, drug),NtSequenceUtils.resistance(treated, drug));
			}
			getNextQuery().process(pair);
		}
	}
	
	public static void main(String[] args){
		Table t = new Table();
		QueryInput qi = new FromSnapshot(new File("/home/gbehey0/snapshot"),
				new GetLongitudinalSequencePairs("NFV","PI","HIV-1","PR",
				new SequencePairsTableOutput(t,new File("/home/gbehey0/long.nfv.out"),TableOutputType.CSV)));
		qi.run();		
	}
}
