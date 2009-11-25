package net.sf.hivgensim.consensus;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.utils.AaSequenceUtils;
import net.sf.hivgensim.queries.framework.utils.DrugGenericUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
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
	private PrintStream out;

	public MutationTimeLine(Date begin, Date end, int field, int delta, String drugClass) {
		this.begin = begin;
		this.end = end;
		this.deltaField = field;
		this.delta = delta;
		Protein protein = DrugGenericUtils.getProteinForDrugClass(drugClass);
//		this.preQuery = new GetDrugClassNaiveSequences(new String[] {drugClass},
//				new SequenceProteinFilter(protein, 
//						new SampleDateFilter(begin, end, 
//								new MutationTimeLineProcessor())));
		this.reference = SelectionWindow.getWindow(
				protein.getOpenReadingFrame().getGenome().getOrganismName()
				, protein.getOpenReadingFrame().getName(), protein.getAbbreviation())
				.getReferenceAaSequence();
		this.out = System.out;
	}

	public MutationTimeLine(int field, int delta, String drugClass) {
		this(defaultBegin(), new Date(), field, delta, drugClass);
	}
	
	public void setOutput(PrintStream out){
		this.out = out;
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

	public void close() {
		preQuery.close();
	}

	public void process(Patient input) {
		preQuery.process(input);
	}
	
	private class MutationTimeLineProcessor implements IQuery<AaSequence> {

		private SortedMap<Date, ConsensusWindow> windows;

		public MutationTimeLineProcessor(){
			initializeWindows();
		}
		
		public void close() {
			for (Entry<Date, ConsensusWindow> consensus : windows.entrySet()) {
				out.println(consensus.getKey()+" : "+consensus.getValue().getConsensusFor("HIV-1 Subtype B"));
			}
		}

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
				System.err.println(currentThreshold);
			}
			//calendar.add after windows.put -> need one more
			this.windows.put(currentThreshold, new ConsensusWindow());
			
		}
	}
	
	public static void main(String[] args) {
		RegaDBSettings.createInstance();
		new FromDatabase("admin", "admin", 
				new MutationTimeLine(Calendar.MONTH, 1, "PI")).run();
	}
	
}
