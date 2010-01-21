package net.sf.hivgensim.consensus;

import java.io.File;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.GetDrugClassNaiveSequences;
import net.sf.hivgensim.queries.SampleDateFilter;
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
	private int windowSize;
	private GetDrugClassNaiveSequences preQuery;
	private String reference;
	private PrintStream out;
	
	public MutationTimeLine(Date begin, Date end, int field, int delta, int windowSize, String drugClass) {
		String[] classes;
		if(drugClass.equals("RTI")){
			classes = new String[]{"NRTI","NNRTI"};
		}else{
			classes = new String[]{drugClass};
		}
		this.begin = begin;
		this.end = end;
		this.deltaField = field;
		this.delta = delta;
		this.windowSize = windowSize;
		Protein protein = DrugGenericUtils.getProteinForDrugClass(classes[0]);
		this.preQuery = new GetDrugClassNaiveSequences(classes,
				new SequenceProteinFilter(protein, 
						new SampleDateFilter(begin, end, 
								new MutationTimeLineProcessor())));
		this.reference = new SelectionWindow(
				protein.getOpenReadingFrame().getGenome().getOrganismName()
				, protein.getOpenReadingFrame().getName(), protein.getAbbreviation())
				.getReferenceAaSequence();
		this.out = System.out;
	}

	public MutationTimeLine(String drugClass) {
		this(defaultBegin(), new Date(), Calendar.MONTH, 1, 12, drugClass);
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
		private static final String subB = "HIV-1 Subtype B";

		public MutationTimeLineProcessor(){
			initializeWindows();
		}
		
		
		public void close() {
			Date begin = windows.firstKey();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(begin);
			calendar.add(deltaField, windowSize);
			Date end = calendar.getTime();
			
			ConsensusCalculator calculator = new ConsensusCalculator(MutationTimeLine.this.reference);
			
			Date current = begin;
			calendar.setTime(begin);
			while(current.before(end)){
				windows.get(current).beginConsensusFor(subB, calculator);
				calendar.add(deltaField, delta);
				current = calendar.getTime();
			}
			
			begin = windows.firstKey();
			Calendar beginCalendar = calendar;
			Calendar endCalendar = Calendar.getInstance();
			beginCalendar.setTime(begin);
			endCalendar.setTime(end);
			
			String previousConsensus = reference;
			while(end.before(windows.lastKey())){
				out.print("                         ");
				String consensus = calculator.getCurrentConsensusSequence();
				out.println();
				out.print(printDate(beginCalendar)+"-"+printDate(endCalendar));
//				out.format(" (%4d) : ", calculator.getAmountOfSequences()); //FIXME
				out.println(consensus);
				
				if(!consensus.equals(previousConsensus)){
						out.print("| ");
						for (int i = 0; i < consensus.length(); i++) {
							if(previousConsensus.charAt(i) != consensus.charAt(i)){
								out.print((i+1)+""+consensus.charAt(i)+" ");
							}
						}
						out.println();
				}
				previousConsensus = consensus;
				
				beginCalendar.add(deltaField, delta);
				endCalendar.add(deltaField, delta);
				begin = beginCalendar.getTime();
				end = endCalendar.getTime();
				windows.get(windows.tailMap(begin).firstKey()).endConsensusFor(subB, calculator);
				windows.get(windows.tailMap(end).firstKey()).beginConsensusFor(subB, calculator);
			}
		}

		private String printDate(Calendar calendar) {
			int month = (calendar.get(deltaField)+1);
			String leadingZero = month < 10 ? "0" : "";
			return leadingZero+month+"/"+calendar.get(Calendar.YEAR);
		}

		public void process(AaSequence input) {
			Date sampleDate = input.getNtSequence().getViralIsolate().getSampleDate();
			ConsensusWindow window = windows.get(windows.tailMap(sampleDate).firstKey());
			String subtype = ConsensusCalculator.getSubtypeForConsensus(input);
			SimpleSequence sequence = new SimpleSequence(input.getFirstAaPos(), 
					input.getLastAaPos(), AaSequenceUtils.toCharSequence(input, reference));
			window.addSequence(sequence, subtype);
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
		if(args.length != 2){
			System.err.println("Usage: consensus snapshot drugclass");
			System.exit(1);
		}
		RegaDBSettings.createInstance();
		new FromSnapshot(new File(args[0]),new MutationTimeLine(args[1])).run();
	}
	
}
