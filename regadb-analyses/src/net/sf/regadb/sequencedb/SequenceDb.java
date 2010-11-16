package net.sf.regadb.sequencedb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Genome;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Privileges;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.Transaction;
import net.sf.regadb.tools.exportFasta.ExportAaSequence;
import net.sf.regadb.tools.exportFasta.FastaExporter.Symbol;

import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.hibernate.ScrollableResults;

public class SequenceDb {
	private ReentrantLock formattingLock = new ReentrantLock();
	private ReentrantLock queryLock = new ReentrantLock();
	private Condition queriesFinishedCondition  = queryLock.newCondition();

	private int queries = 0;
	
	private static ExportAaSequence exporter = new ExportAaSequence(Symbol.Nucleotides, true, false);
	
	private File path;
	
	public SequenceDb(String path) {
		this.path = new File(path);
	}
	
	public void init(Transaction t) {
		if (path != null && path.listFiles().length == 0) {		
			Query q = t.createQuery("from NtSequence");
			q.setCacheMode(CacheMode.IGNORE);
			ScrollableResults r = q.scroll();
			int i = 0;
			while (r.next()) {
				exportAlignment((NtSequence)r.get(0));
				i++;
				if (i > 1000) { 
					t.clearCache();
					i = 0;
				}
			}
		}
	}
	
	private static String proteinString(Protein p) {
		return p.getOpenReadingFrame().getName() + "_" + p.getAbbreviation();
	}
	
	public static String alignmentToString(OpenReadingFrame orf, NtSequence sequence) {
		return alignmentToString(orf, getAlignmentMap(sequence));
	}
	
	private static String alignmentToString(OpenReadingFrame orf, Map<String, AaSequence> aaSeqs) {
		StringBuffer alignment = new StringBuffer();

		List<Protein> proteins = new ArrayList<Protein>(orf.getProteins());
		Collections.sort(proteins, new Comparator<Protein>() {
			public int compare(Protein p0, Protein p1) {
				return p0.getStartPosition() - p1.getStartPosition();
			}
		});
		
		boolean hasSequence = false;
		for (Protein p : proteins) {
			AaSequence aaSeq = aaSeqs.get(proteinString(p));
			if (aaSeq == null) {
				for (int i = 0; i < p.getStopPosition() - p.getStartPosition(); i++)
					alignment.append("-");
			} else {
				alignment.append(exporter.getAlignmentView(aaSeq));
				hasSequence = true;
			}
		}
		
		if (hasSequence)
			return alignment.toString();
		else
			return null;
	}

	private static Map<String, AaSequence> getAlignmentMap(NtSequence sequence) {
		Map<String, AaSequence> aaSeqs = new HashMap<String, AaSequence>();
		for (AaSequence aaSeq : sequence.getAaSequences())
			aaSeqs.put(proteinString(aaSeq.getProtein()), aaSeq);
		return aaSeqs;
	}
	
	private String getNtSequenceId(NtSequence sequence) {
		return (new Patient(sequence.getViralIsolate().getPatient(), Privileges.READONLY.getValue())).getPatientIi() + "_" +
			sequence.getViralIsolate().getViralIsolateIi() + "_" + 
			sequence.getNtSequenceIi();
	}
	
	private void exportAlignment(NtSequence sequence) {
		Genome genome = sequence.getViralIsolate().getGenome();
		for (OpenReadingFrame orf : genome.getOpenReadingFrames()) {
			String id = getNtSequenceId(sequence);
			String alignment = alignmentToString(orf, getAlignmentMap(sequence));
			
			if (alignment != null) {
				File dir = getOrfDir(orf);
				dir.mkdirs();
				File f = new File(dir.getAbsolutePath() + File.separatorChar + id + ".fasta");
				FileWriter fw = null;
				try {
					try {
						fw = new FileWriter(f);
						fw.append(">");
						fw.append(id);
						fw.append('\n');
						fw.append(alignment);
					} finally {
						fw.close();						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void sequenceAligned(NtSequence sequence, boolean newSequence) {
		if (path == null)
			return;
		
		waitForQueriesFinished();

		try {
			formattingLock.lock();

			exportAlignment(sequence);
		} finally {
			formattingLock.unlock();
		}
	}
	
	private File getOrfDir(OpenReadingFrame orf) {
		return new File(path.getAbsolutePath() 
					+ File.separatorChar + orf.getGenome().getOrganismName() 
					+ File.separatorChar + orf.getName());
	}

	private void queryImpl(Genome genome, SequenceQuery query) {
		for (OpenReadingFrame orf : genome.getOpenReadingFrames()) {
			File dir = getOrfDir(orf);
			if (dir != null && dir.exists())
				for (File f : dir.listFiles()) {
					try {
						BufferedReader input = new BufferedReader(
								new FileReader(f));
						try {
							String[] ids = input.readLine().substring(1).split(
									"_");
							String alignment = input.readLine();

							query.process(orf, 
									Integer.parseInt(ids[0]),
									Integer.parseInt(ids[1]), 
									Integer.parseInt(ids[2]), 
									alignment);
						} finally {
							input.close();
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
		}
	}
	
	public void query(Genome genome, SequenceQuery query) {
		try {
			queryLock.lock();
			queries++;
			queryLock.unlock();

			queryImpl(genome, query);
		} finally {
			queryLock.lock();
			queries--;
			queriesFinishedCondition.signal();
			queryLock.unlock();
		}
	}

	protected void waitForQueriesFinished() {
		queryLock.lock();
		try {
			while (queries > 0)
				try {
					queriesFinishedCondition.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		} finally {
			queryLock.unlock();
		}
	}
}
