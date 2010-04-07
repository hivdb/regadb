package net.sf.phylis;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jargs.gnu.CmdLineParser;

/*
 * Created on Jul 6, 2004
 */

/**
 * @author kdforc0
 */
public class FastaInfo {

	public static void main(String[] args) {
		CmdLineParser parser = new CmdLineParser();
		CmdLineParser.Option inputFileOption = parser.addStringOption('i', "input");
		CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
		CmdLineParser.Option nameOption = parser.addBooleanOption('n', "name");
		CmdLineParser.Option descriptionOption = parser.addBooleanOption('d', "description");
		CmdLineParser.Option lengthOption = parser.addBooleanOption('l', "length");
		CmdLineParser.Option oneOption = parser.addBooleanOption('1', "one");
		CmdLineParser.Option numberOption = parser.addBooleanOption('N', "number");	
		CmdLineParser.Option getOption = parser.addIntegerOption('g', "get");
		CmdLineParser.Option outputFileOption = parser.addStringOption('o', "output");

		try {
			parser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			printUsage();
			System.exit(2);
		}
			
		if (parser.getOptionValue(helpOption) == Boolean.TRUE) {
			printUsage();
			System.exit(0);
		}
		
		String inputFile = (String) parser.getOptionValue(inputFileOption);
		String outputFile = (String) parser.getOptionValue(outputFileOption);
		boolean length = parser.getOptionValue(lengthOption) != null;
		boolean name = parser.getOptionValue(nameOption) != null;
		boolean description = parser.getOptionValue(descriptionOption) != null;
		boolean help = parser.getOptionValue(helpOption) != null;
		boolean one = parser.getOptionValue(oneOption) != null;
		Integer get = (Integer) parser.getOptionValue(getOption);

		try {
			InputStream input;
			if (inputFile == null)
				input = System.in;
			else
				input = new BufferedInputStream(new FileInputStream(inputFile));
			
			SequenceAlignment alignment = new SequenceAlignment(input, SequenceAlignment.FILETYPE_FASTA);

			if (parser.getOptionValue(numberOption) == Boolean.TRUE) {
				System.out.println(alignment.getSequences().size());
				System.exit(0);
			}

			if (alignment.getSequences().size() == 0) {
				Fail("no sequences in input");
			}

			if (one && alignment.getSequences().size() != 1) {
				Fail("input contains more than one sequence");
			}
			
			if (!name && !length && !description && (get == null))
				Fail("select at least one of -l, -n, -d or -g");

			if (get != null) {
				Sequence s = (Sequence) alignment.getSequences().get(get.intValue());
				ArrayList l = new ArrayList();
				l.add(s);
				SequenceAlignment result = new SequenceAlignment(l, SequenceAlignment.SEQUENCE_DNA);
				result.writeFastaOutput(new FileOutputStream(outputFile));
				System.exit(0);
			}
			
			for (int i = 0; i < alignment.getSequences().size(); ++i) {
				Sequence s = (Sequence) alignment.getSequences().get(i);
				
				if (name)
					System.out.print(s.getName());

				if (description) {
					if (name)
						System.out.print("\t");
					System.out.print(s.getDescription());
				}
				
				if (length) {
					if (name || description)
						System.out.print("\t");
					System.out.print(s.getLength());
				}
				
				System.out.println();
			}
		} catch (FileNotFoundException e1) {
			Fail("could not open " + inputFile);
		} catch (ParameterProblemException e1) {
			Fail("internal error: " + e1.getMessage());
		} catch (IOException e1) {
			Fail(e1.getMessage());
		} catch (FileFormatException e1) {
			Fail(e1.getMessage());
		}
	}

	private static void Fail(String msg) {
		System.err.println("fastainfo error: " + msg);
		System.exit(1);
	}

	private static void printUsage() {
		System.err.println("usage: fastainfo [-n] [-l] [-i input.fasta]");
		System.err.println();
		System.err.println("       retrieve elementary information about a fasta file");
		System.err.println();
		System.err.println("options:");
		System.err.println("\t-n,--name      sequence names");
		System.err.println("\t-l,--length    sequence length");
		System.err.println("\t-i,--input     input file, or stdin if not provided");		
	}
}
