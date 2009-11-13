package net.sf.hivgensim.consensus;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.SequenceProteinFilter;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.snapshot.FromSnapshot;
import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Protein;
import net.sf.regadb.util.settings.RegaDBSettings;

public class MutationTimeLine implements IQuery<Patient> {

	private Date begin;
	private Date end;
	private int deltaField;
	private int delta;
	private GetDrugClassNaiveSequences preQuery;
	private String reference;

	public MutationTimeLine(Date begin, Date end, int field, int delta, String drugClass) {
		this.begin = begin;
		this.end = end;
		this.deltaField = field;
		this.delta = delta;
		Protein protein = DrugGenericUtils.getProteinForDrugClass(drugClass);
		this.preQuery = new GetDrugClassNaiveSequences(new String[] {drugClass},
				new SequenceProteinFilter(protein, new MutationTimeLineProcessor()));
		this.reference = SelectionWindow.getWindow(
				protein.getOpenReadingFrame().getGenome().getOrganismName()
				, protein.getOpenReadingFrame().getName(), protein.getAbbreviation())
				.getReferenceAaSequence();
	}

	public MutationTimeLine(int field, int delta, String drugClass) {
		this(defaultBegin(), new Date(), field, delta, drugClass);
	}

	private static Date defaultBegin() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -15);
		return cal.getTime();
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public int getDeltaDays() {
		return delta;
	}

	@Override
	public void close() {
		preQuery.close();
	}

	@Override
	public void process(Patient input) {
		preQuery.process(input);
	}
	
	private class MutationTimeLineProcessor implements IQuery<AaSequence> {

		private SortedMap<Date, ConsensusWindow> windows;

		public MutationTimeLineProcessor(){
			initializeWindows();
		}
		
		@Override
		public void close() {
			for (Entry<Date, ConsensusWindow> consensus : windows.entrySet()) {
				System.out.println(consensus.getKey()+" : "+consensus.getValue().getConsensus());
			}
		}

		@Override
		public void process(AaSequence input) {
			Date sampleDate = input.getNtSequence().getViralIsolate().getSampleDate();
			ConsensusWindow window = windows.get(windows.tailMap(sampleDate).firstKey());
			String subtype = ConsensusCalculator.getSubtypeForConsensus(input);
			window.addSequence(AaSequenceUtils.toCharSequence(input, reference), subtype );
		}
		
		public void initializeWindows() {
			this.windows = new TreeMap<Date, ConsensusWindow>();
			Date currentThreshold = begin;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(currentThreshold);
			
			while(currentThreshold.before(end)){
				this.windows.put(currentThreshold, new ConsensusWindow());
				calendar.add(deltaField, delta);
				currentThreshold = calendar.getTime();
			}
		}
	}
	
	public static void main(String[] args) {
		RegaDBSettings.createInstance();
		new FromSnapshot(new File("/home/tm/labo/small_snapshot"), 
				new MutationTimeLine(Calendar.MONTH, 1, "PI")).run();
	}
	
}
