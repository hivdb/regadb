package net.sf.hivgensim.queries.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.hivgensim.queries.framework.SequencePair;
import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.hivgensim.preprocessing.Utils;

public class SequencePairsToFasta extends DefaultQueryOutput<SequencePair>{

	private SelectionWindow sw;

	public SequencePairsToFasta(File file,SelectionWindow sw) throws FileNotFoundException {
		super(new PrintStream(file));
		this.sw = sw;
	}

	public void process(SequencePair pair) {
		if(sw != null){
			getOut().println(">"+pair.getSeq1().getNtSequenceIi());
			getOut().println(Utils.getAlignedNtSequenceString(pair.getSeq1(),sw));
			getOut().println(">"+pair.getSeq2().getNtSequenceIi());
			getOut().println(Utils.getAlignedNtSequenceString(pair.getSeq2(),sw));
		}else{
			getOut().println(">"+pair.getSeq1().getNtSequenceIi());
			getOut().println(pair.getSeq1());
			getOut().println(">"+pair.getSeq2().getNtSequenceIi());
			getOut().println(pair.getSeq2());
		}
	}

}
