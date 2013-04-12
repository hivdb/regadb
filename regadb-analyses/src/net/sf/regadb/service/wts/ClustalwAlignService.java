package net.sf.regadb.service.wts;

import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.align.local.ScoredAlignment;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.Alignment;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleAlignment;

public class ClustalwAlignService extends AbstractService {
	@SuppressWarnings("serial")
	public static class AlignServiceException extends ServiceException{
		private IllegalSymbolException symbolException;

		public AlignServiceException(String service, String url, IllegalSymbolException symbolException) {
			super(service, url);
			this.symbolException = symbolException;
		}
		
		public IllegalSymbolException getSymbolException(){
			return symbolException;
		}
	}
	
	public static enum SubstitutionMatrix{BLOSUM, IUB, NUC4_4};
	
	private Sequence sequence1, sequence2;
	private Sequence alignedSequence1, alignedSequence2;
	private double score;
	
	private SubstitutionMatrix matrix;
	private double gapOpenScore, gapExtScore;
	private boolean aa;
	
	public ClustalwAlignService(boolean aa, double gapOpenScore, double gapExtScore, SubstitutionMatrix matrix){
		setAa(aa);
		setSequence1(sequence1);
		setSequence2(sequence2);
	}
	@Override
	protected void init() {
		setService("regadb-align-clustalw");
		
		String fasta = toFasta(getSequence1(),getSequence2());
		getInputs().put("sequences",fasta);
		getInputs().put("matrix", getMatrix().name());
		getInputs().put("gapopen", getGapOpenScore()+"");
		getInputs().put("gapext", getGapExtScore()+"");
	}
	
	public static String toFasta(Sequence... sequences){
		StringBuilder sb = new StringBuilder();
		
		for(Sequence sequence : sequences)
			sb.append('>'+ sequence.getName() +'\n'+ sequence.seqString() +'\n');
			
		return sb.toString();
	}

	@Override
	protected void processResults() throws ServiceException {
		String seqs[] = getOutputs().get("sequences").split(">");
		String seq1 = seqs[0].substring(seqs[0].indexOf('\n')).replace("\n", "").trim();
		String seq2 = seqs[1].substring(seqs[1].indexOf('\n')).replace("\n", "").trim();
		
		try{
			if(isAa()){
				setAlignedSequence1(ProteinTools.createGappedProteinSequence(seq1, getSequence1().getName()));
				setAlignedSequence2(ProteinTools.createGappedProteinSequence(seq2, getSequence2().getName()));
			}
			else{
				setAlignedSequence1(DNATools.createGappedDNASequence(seq1, getSequence1().getName()));
				setAlignedSequence2(DNATools.createGappedDNASequence(seq2, getSequence2().getName()));
			}
		}
		catch (IllegalSymbolException e) {
			e.printStackTrace();
			throw new AlignServiceException(getService(),getUrl(),e);
		}
		
		setScore(Double.parseDouble(getOutputs().get("score")));
	}
	
	public ScoredAlignment pairwiseAlignment(Sequence seq1, Sequence seq2) throws ServiceException{
		launch();
		
		Map<String, Sequence> m = new HashMap<String, Sequence>();
        m.put(getAlignedSequence1().getName(), getAlignedSequence1());
        m.put(getAlignedSequence2().getName(), getAlignedSequence2());
        Alignment result = new SimpleAlignment(m);

        return new ScoredAlignment(result, getScore());
    }
	
	
	public void setSequence1(Sequence sequence1) {
		this.sequence1 = sequence1;
	}
	public Sequence getSequence1() {
		return sequence1;
	}
	public void setSequence2(Sequence sequence2) {
		this.sequence2 = sequence2;
	}
	public Sequence getSequence2() {
		return sequence2;
	}
	public void setMatrix(SubstitutionMatrix matrix) {
		this.matrix = matrix;
	}
	public SubstitutionMatrix getMatrix() {
		return matrix;
	}
	public void setGapOpenScore(double gapOpenScore) {
		this.gapOpenScore = gapOpenScore;
	}
	public double getGapOpenScore() {
		return gapOpenScore;
	}
	public void setGapExtScore(double gapExtScore) {
		this.gapExtScore = gapExtScore;
	}
	public double getGapExtScore() {
		return gapExtScore;
	}
	public void setAa(boolean aa) {
		this.aa = aa;
	}
	public boolean isAa() {
		return aa;
	}
	protected void setAlignedSequence2(Sequence alignedSequence2) {
		this.alignedSequence2 = alignedSequence2;
	}
	public Sequence getAlignedSequence2() {
		return alignedSequence2;
	}
	protected void setAlignedSequence1(Sequence alignedSequence1) {
		this.alignedSequence1 = alignedSequence1;
	}
	public Sequence getAlignedSequence1() {
		return alignedSequence1;
	}
	protected void setScore(double score) {
		this.score = score;
	}
	public double getScore() {
		return score;
	}

}
