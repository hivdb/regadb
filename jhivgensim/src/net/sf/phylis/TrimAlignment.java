package net.sf.phylis;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Created on Jun 17, 2005
 */

/**
 * @author kdforc0
 */
public class TrimAlignment {
	static int MARGIN = 100;

	public static void main(String[] args)
		throws FileNotFoundException, ParameterProblemException, IOException, FileFormatException {
		String example = args[0];
		String profile = args[1];
		String result = args[2];
		
		SequenceAlignment exampleAlignment
			= new SequenceAlignment(new BufferedInputStream(new FileInputStream(example)),
									SequenceAlignment.FILETYPE_FASTA);
		SequenceAlignment profileAlignment
			= new SequenceAlignment(new BufferedInputStream(new FileInputStream(profile)),
								    SequenceAlignment.FILETYPE_FASTA);

		if (exampleAlignment.getSequences().size() != 2)
			throw new RuntimeException("example must contain two sequences");

		int diff = exampleAlignment.getLength() - profileAlignment.getLength();

		Sequence query = (Sequence) exampleAlignment.getSequences().get(1);
		
		int start = query.firstNonGapPosition();
		int end = query.lastNonGapPosition();
		
		start = Math.max(0, start - MARGIN - diff);
		end = Math.min(profileAlignment.getLength(), end + MARGIN + diff);
		
		SequenceAlignment resultAlignment
			= profileAlignment.getSubSequence(start, end);
		resultAlignment.writeFastaOutput(new BufferedOutputStream(new FileOutputStream(result)));
	}
}
