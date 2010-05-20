package net.sf.regadb.tools.exportFasta;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Dataset;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.PatientImplHelper;
import net.sf.regadb.db.Protein;
import net.sf.regadb.db.ViralIsolate;

public class FastaExporter {
	public enum Mode {
		Submitted("form.query.querytool.fastaExport.mode.submitted"),
		BaseOnProteins("form.query.querytool.fastaExport.mode.proteins");
		
		private String localizedMessage;
		
		private Mode(String localizedMessage) {
			this.localizedMessage = localizedMessage;
		}
		
		public String getLocalizedMessage() {
			return localizedMessage;
		}
	}
	
	public enum Symbol {
		AminoAcids("form.query.querytool.fastaExport.symbol.aminoacids"),
		Nucleotides("form.query.querytool.fastaExport.symbol.nucleotides");
		
		private String localizedMessage;
		
		private Symbol(String localizedMessage) {
			this.localizedMessage = localizedMessage;
		}
		
		public String getLocalizedMessage() {
			return localizedMessage;
		}
	}
	
	public enum FastaId {
		Dataset("form.query.querytool.fastaExport.id.dataset"),
		PatientId("form.query.querytool.fastaExport.id.patientId"),
		SampleId("form.query.querytool.fastaExport.id.sampleId");
		
		
		private String localizedMessage;
		
		private FastaId(String localizedMessage) {
			this.localizedMessage = localizedMessage;
		}
		
		public String getLocalizedMessage() {
			return localizedMessage;
		}
	}
	
	private Mode mode;
	private Set<String> proteins;
	private String orf;
	private Symbol symbol;
	private boolean aligned;
	private EnumSet<FastaId> fastaId;
	
	public FastaExporter() {
		
	}
	
	/**
	 * Note: proteins from one ORF are expected
	 */
	public FastaExporter(Mode mode, Set<Protein> proteins, Symbol symbol, boolean aligned, EnumSet<FastaId> fastaId) {
		this.mode = mode;
		this.proteins = new HashSet<String>();
		for (Protein p : proteins) {
			this.proteins.add(p.getAbbreviation());
			if (this.orf == null)
				this.orf = p.getOpenReadingFrame().getName();
			else 
				if (!this.orf.equals(p.getOpenReadingFrame().getName()))
					throw new RuntimeException("All provided proteins must belong to the same ORF!");
		}
		this.symbol = symbol;
		this.aligned = aligned;
		this.fastaId = fastaId;
	}

	public Mode getMode() {
		return mode;
	}
	
	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public String getOrf() {
		return orf;
	}

	public void setOrf(String orf) {
		this.orf = orf;
	}
	
	public Set<String> getProteins() {
		return proteins;
	}
	
	public void setProteins(Set<String> protein) {
		this.proteins = protein;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public boolean isAligned() {
		return aligned;
	}
	
	public void setAligned(boolean aligned) {
		this.aligned = aligned;
	}
	
	public EnumSet<FastaId> getFastaId() {
		return fastaId;
	}

	public void setFastaId(EnumSet<FastaId> fastaId) {
		this.fastaId = fastaId;
	}
	
	public int export(ViralIsolate viralIsolate, OutputStreamWriter os, Set<Dataset> datasets, Map<String, Protein> proteinMap) throws IOException {
		int entries = 0;
		if (mode == null) {
			return entries;
		} else if (mode == Mode.Submitted) {
			for (NtSequence ntseq : viralIsolate.getNtSequences()) {
				os.write(">" + getFastaId(viralIsolate, datasets));
				if (viralIsolate.getNtSequences().size() > 1)
					os.write("_" + ntseq.getLabel());
				os.write("\n");
				os.write(ntseq.getNucleotides()  + "\n");
				entries++;
			}
		} else if (mode == Mode.BaseOnProteins) {
			ExportAaSequence exporter = new ExportAaSequence(symbol, aligned);
			os.write(">" + getFastaId(viralIsolate, datasets) + "\n");
			for (String protein : proteins) {
				boolean found = false;
				
				for (NtSequence ntseq : viralIsolate.getNtSequences())
					for (AaSequence aaseq : ntseq.getAaSequences())
						if (aaseq.getProtein().getAbbreviation().equals(protein)) {
							os.write(exporter.getAlignmentView(aaseq));
							found = true;
						}
				
				if (!found && aligned) {
					int size = (proteinMap.get(protein).getStopPosition() - proteinMap.get(protein).getStartPosition());
					for (int i = 0; i < size; i++)
						if (this.symbol == Symbol.Nucleotides)
							os.write("---");
						else /*AminoAcids*/
							os.write("-");
				}
			}
			entries++;
			os.write("\n");
		}
		return entries;
	}
	
	private String getFastaId(ViralIsolate viralIsolate, Set<Dataset> datasets) {
		String toReturn = "";
		for (FastaId id : fastaId) {
			if (toReturn.equals(""))
				toReturn += getId(id, viralIsolate, datasets);
			else
				toReturn += "_" + getId(id, viralIsolate, datasets);
		}
		return toReturn;
	}
	
	private String getId(FastaId id, ViralIsolate viralIsolate, Set<Dataset> datasets) {
		switch (id) {
		case Dataset:
			return PatientImplHelper.getPatient(viralIsolate, datasets).getSourceDataset().getDescription();
		case PatientId:
			return PatientImplHelper.getPatient(viralIsolate, datasets).getPatientId();
		case SampleId:
			return viralIsolate.getSampleId();
		}
		
		return null;
	}
}
