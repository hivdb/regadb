package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.regadb.db.NtSequence;

public class SequencesToFasta extends DefaultQueryOutput<NtSequence> {

	private SelectionWindow sw;
	
	public SequencesToFasta(File file) throws FileNotFoundException {
		super(new PrintStream(file));
	}
	
	public SequencesToFasta(File file, SelectionWindow sw) throws FileNotFoundException {
		this(file);
		this.sw = sw;
	}	

	public void process(NtSequence seq) {
//		getOut().println(">"+seq.getViralIsolate().getSampleId()+"|"+seq.getLabel());
		getOut().println(">"+seq.getNtSequenceIi());
		if(sw == null){			
			getOut().println(seq.getNucleotides());			
		}else{
			getOut().println(net.sf.hivgensim.preprocessing.Utils.getAlignedNtSequenceString(seq, sw));
		}
	}
}
