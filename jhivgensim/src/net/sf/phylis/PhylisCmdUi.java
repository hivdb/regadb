package net.sf.phylis;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import jargs.gnu.CmdLineParser;

/*
 * Created on Jul 6, 2004
 */

/**
 * @author kdforc0
 */
public class PhylisCmdUi {

	public static void main(String[] args) {
		try {
			CmdLineParser parser = new CmdLineParser();
			CmdLineParser.Option mrBayesPathOption = parser.addStringOption('b', "bayes");
			CmdLineParser.Option paupPathOption = parser.addStringOption('p', "paup");
			//CmdLineParser.Option phylipPathOption = parser.addStringOption('y', "phylip");
			CmdLineParser.Option configOption = parser.addStringOption('c', "config");
			CmdLineParser.Option scanOption = parser.addStringOption('s', "scan");
			CmdLineParser.Option helpOption = parser.addBooleanOption('h', "help");
			CmdLineParser.Option taxusNameOption = parser.addStringOption('t', "taxus");
			CmdLineParser.Option reverseOption = parser.addBooleanOption('r', "reverse");
			CmdLineParser.Option outputDirOption = parser.addStringOption('d', "directory");
			CmdLineParser.Option alignmentOption = parser.addStringOption('a', "alignment");
			
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
			
			String mrBayesPath = (String) parser.getOptionValue(mrBayesPathOption);
			String paupPath = (String) parser.getOptionValue(paupPathOption);
			//String phylipPath = (String) parser.getOptionValue(phylipPathOption);
			String configFile = (String) parser.getOptionValue(configOption);
			String scanFile = (String) parser.getOptionValue(scanOption);
			String taxusName = (String) parser.getOptionValue(taxusNameOption);
			Boolean reverseTaxa = (Boolean) parser.getOptionValue(reverseOption);
			String outputDir = (String) parser.getOptionValue(outputDirOption);
			String alignment = (String) parser.getOptionValue(alignmentOption);
			
			if (configFile == null) {
				Fail("need configfile");
			}
			if (taxusName == null) {
				Fail("need taxusname");
			}

			int analysisMethod = -1;
			
			if (mrBayesPath != null)
				if (analysisMethod == -1) {
					analysisMethod = PhylisAlgo.MRBAYES_ANALYSIS;
					PhylisAlgo.mrBayesCommand = mrBayesPath;
				} else {
					Fail("only analysis may be selected");
				}
			
			if (paupPath != null)
				if (analysisMethod == -1) {
					analysisMethod = PhylisAlgo.PAUP_ANALYSIS;
					PhylisAlgo.paupCommand = paupPath;
				} else {
					Fail("only analysis may be selected");
				}
			
			if (analysisMethod == -1) {
				Fail("an analysis must be selected");
			}
			
			PhylisConfig config = new PhylisConfig(new File(configFile));
			
			if (alignment != null)
				config.setInputFileName(alignment);
				
			if (reverseTaxa == Boolean.TRUE) {
				config.reverseTaxa();
			}

			int queryTaxus = config.findTaxus(taxusName);
				
			if (queryTaxus == -1)
				Fail("could not find taxus: " + taxusName);

			if (scanFile == null) {
				PhylisAlgo.ClusterResult c
					= PhylisAlgo.getMostProbableCluster(config, queryTaxus, analysisMethod, reverseTaxa == Boolean.TRUE);
				
				if (c == null) {
					Fail("no clusters defined");
				}

				System.out.println(c.cluster.getName() + "\t" + c.result
								   + "\t" + c.clusterComponentsWithQuery + "\t" + c.clusterWithoutQuery);
			} else {
				if (outputDir == null) {
					Fail("need an output directory");
				}

				PhylisAlgo.runScanAnalysis(config, new File(outputDir), (PhylisUi) null, analysisMethod);
				float bootscanSupport
					= PhylisAlgo.gnuPlotResults(config, new File(outputDir), queryTaxus,
										        analysisMethod, new File(scanFile));
				
				System.out.println(bootscanSupport);
			}
		} catch (FileNotFoundException e) {
			Fail(e.getMessage());
		} catch (ParameterProblemException e) {
			Fail("internal error: " + e.getStackTrace().toString());
		} catch (IOException e) {
			Fail(e.getMessage());
		} catch (FileFormatException e) {
			Fail(e.getMessage());
		} catch (ApplicationException e) {
			Fail(e.getMessage());
		}
	}

	private static void Fail(String msg) {
		System.err.println("phylis error: " + msg);
		System.exit(1);
	}

	private static void printUsage() {
		System.err.println("usage: phylis [-r] [-s result.gnuplot] [-p]|[-m] executablepath -c config.xml -t taxus");
		System.err.println();
		System.err.println("       analyze the clustering of the given taxus together");
		System.err.println("       with clusters and alignment specified in config.xml,");
		System.err.println("       using the whole sequence or a sliding window analysis.");
		System.err.println();
		System.err.println("options:");
		System.err.println("\t-s,--scan      perform a sliding window analysis");		
		System.err.println("\t               and write results in gnuplot format");
		System.err.println("\t-p,--paup      use paup with bootstrap analysis");
		System.err.println("\t-b,--mrbayes   use mrbayes with bayesian analysis");
		System.err.println("\t-r,--reverse   reverse taxa order before analysis");
	}
}
