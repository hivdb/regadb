package net.sf.phylis;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Created on Feb 24, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */

/**
 * @author kdforc0
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class FastaToNexus {

	public static void main(String[] args) throws FileNotFoundException, ParameterProblemException, IOException, FileFormatException {
		if (args.length != 2) {
			System.err.println("usage: fasta2nex input.fasta output.nex");
		}

		SequenceAlignment sa
			= new SequenceAlignment(new BufferedInputStream(new FileInputStream(args[0])),
									SequenceAlignment.FILETYPE_FASTA);
        
        //sa.removeAllEmpty();
        
		sa.writeNexusOutput(new BufferedOutputStream(new FileOutputStream(args[1])));
	}
}
