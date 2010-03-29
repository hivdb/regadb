package net.sf.phylis;
/*
 * Created on May 5, 2003
 */

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class PhylisAlgo {
	static public String mrBayesCommand = "mrbayes";
	static public String paupCommand = "paup";
	static public final int MRBAYES_ANALYSIS = 0;
	static public final int PAUP_ANALYSIS = 1;
	static public final int PHYLIP_ANALYSIS = 2;

	static public List retrieveTaxa(File inputFile) throws ApplicationException {
		List taxa = new ArrayList();
        
		SequenceAlignment alignment = null;
		try {
			InputStream input = new FileInputStream(inputFile);

			alignment =
				new SequenceAlignment(input, SequenceAlignment.FILETYPE_FASTA);
		} catch (FileNotFoundException e) {
			throw new ApplicationException(
				"Error: file not found " + inputFile.getAbsolutePath());
		} catch (ParameterProblemException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new ApplicationException(
				"I/O Error while reading " + inputFile.getAbsolutePath());
		} catch (FileFormatException e) {
			throw new ApplicationException(
				"File format exception while reading "
					+ inputFile.getAbsolutePath()
					+ ":\n"
					+ e.getMessage());
		}

		for (Iterator i = alignment.getSequences().iterator(); i.hasNext();) {
			SequenceInterface sequence = (SequenceInterface) i.next();
			taxa.add(sequence.getName());
		}

		return taxa;
	}
	
	public static void runScanAnalysis(PhylisConfig config,
					 				   File outputDir, PhylisUi ui,
					 				   int analysisMethod)
			throws ApplicationException {
		writeRunConfigFile(config, outputDir);

		/*
		 * Run SlidingGene
		 */
		List windows = runSlidingGene(config, analysisMethod == PAUP_ANALYSIS);

		if (windows == null)
			return;

		/*
		 * Now, for each window, we run MrBayes.
		 */
		if (ui != null)
			ui.updateProgress(0, windows.size());
		int k = 0;
		for (Iterator i = windows.iterator(); i.hasNext(); ++k) {
			SequenceAlignment a = (SequenceAlignment) i.next();

			switch (analysisMethod) {
			case MRBAYES_ANALYSIS:
				if (!runMrBayes(a, outputDir, k, config.getMrBayesBlock()))
					return;
				break;
			case PAUP_ANALYSIS:
				if (!runPaup(a, outputDir, k, config.getPaupBlock()))
					return;
			}

			if (ui != null)
				ui.updateProgress(k + 1, windows.size());
		}
	}

	private static void writeRunConfigFile(PhylisConfig config,
										   File outputDir)
			throws ApplicationException {
		if (outputDir == null)
			throw new ApplicationException("Error: no output directory defined");
		
		File configFileName =
			new File(outputDir + File.separator + "config.xml");
		try {
			config.save(configFileName);
		} catch (IOException e) {
			throw new ApplicationException(
				"Error: cannot write config file to "
					+ configFileName.getAbsolutePath());
		}
	}

	private static boolean runMrBayes(SequenceAlignment a, File outputDir,
									  int windowNr, String mrBayesText) throws ApplicationException {
		String nexName = writeWindowFile(a, outputDir, windowNr, mrBayesText, true);

		Runtime runtime = Runtime.getRuntime();
		Process bayes = null;
		try {
			/*
			 * Run MrBayes
			 */
			String cmds[] = { mrBayesCommand, nexName};
			System.err.println("Starting: " + cmds[0] + " " + cmds[1]);

			if (outputDir.isDirectory())
				bayes = runtime.exec(cmds, null, outputDir);
			else
				bayes = runtime.exec(cmds, null);

			OutputStream output = bayes.getOutputStream();
			InputStream input = bayes.getInputStream();
			output.write('\n');
			output.flush();
			doLogWindow(input, nexName);
			int result = bayes.waitFor();

			if (result != 0) {
				/*
				 * Apparently MrBayes always exits with error code 1
				 * Duh!
				 */
				//throw new ApplicationException("MrBayes exited with error: " + result);
			}
		} catch (InterruptedIOException e) {
			bayes.destroy();
			return false;
		} catch (IOException e) {
			if (bayes != null)
				bayes.destroy();
			throw new ApplicationException("Error: I/O Error while invoking MrBayes: "
				+ e.getMessage());
		} catch (InterruptedException e) {
			bayes.destroy();
			return false;
		}

		return true;
	}

	private static boolean runPaup(SequenceAlignment a, File outputDir,
								   int windowNr, String paupText) throws ApplicationException {
		String nexName = writeWindowFile(a, outputDir, windowNr, paupText, true);
		Runtime runtime = Runtime.getRuntime();
		Process paup = null;

		try {
			/*
			 * Run paup
			 */
			String cmd = paupCommand + " -n " + nexName;

			if (outputDir.isDirectory())
				paup = runtime.exec(cmd, null, outputDir);
			else
				paup = runtime.exec(cmd);

            InputStream stderr = paup.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null)
            	System.err.println(line);
            br.close();
            
			//InputStream input = paup.getInputStream();
			//input.close();
			//doLogWindow(input, nexName);
            int result = paup.waitFor();

			if (result != 0) {
				throw new ApplicationException("Paup exited with error: " + result);
			}
			System.gc();
		} catch (InterruptedIOException e) {
			paup.destroy();
			return false;
		} catch (IOException e) {
			if (paup != null)
				paup.destroy();
			throw new ApplicationException("Error: I/O Error while invoking Paup: "
				+ e.getMessage());
		} catch (InterruptedException e) {
			paup.destroy();
			return false;
		}

		return true;
	}

	private static String writeWindowFile(SequenceAlignment a, File outputDirOrFile,
									      int windowNr,
										  String appText,
										  boolean needQuit)
			throws ApplicationException {
		String outFileName;
		String nexName;
		if (outputDirOrFile.isDirectory()) {
			nexName = "window" + windowNr + ".nex";
			String outputDirName = outputDirOrFile.getAbsolutePath();
			outFileName = outputDirName + File.separator + nexName;
		} else {
			nexName = outputDirOrFile.getName();
			outFileName = outputDirOrFile.getAbsolutePath();
		}
		
		try {
			OutputStream outFile = new FileOutputStream(outFileName);
			a.writeOutput(outFile, SequenceAlignment.FILETYPE_NEXUS);
		    
			/*
			 * Append App block.
			 */
			appendAppBlock(appText, outFile, nexName, needQuit);
			outFile.flush();
			outFile.close();
		
		} catch (FileNotFoundException e) {
			throw new ApplicationException("Error: Cannot open " + outFileName + " for writing");
		} catch (IOException e) {
			throw new ApplicationException("Error: I/O Error while writing " + outFileName);
		} catch (ParameterProblemException e) {
			throw new RuntimeException(e);
		}
		return nexName;
	}

	private static void appendAppBlock(String appText,
									   OutputStream outputFile,
							  		   String fileName,
							  		   boolean needQuit) throws IOException {
		Writer writer = new OutputStreamWriter(outputFile);

		writer.write('\n');
		/*
		 * replace ${FILE} with the filename
		 */
		if (fileName != null)
			appText = appText.replaceAll("\\$\\{FILE\\}", fileName);
		boolean haveQuit = (appText.indexOf("quit;") != -1);
		if (needQuit) {
			if (!haveQuit)
				appText = appText + "\nquit;";
		} else
			if (haveQuit)
				appText = appText.replaceAll("quit;", "");
		writer.write(appText);
		writer.write('\n');
		writer.flush();
	}

	private static List runSlidingGene(PhylisConfig config, boolean degap)
			throws ApplicationException {
		List windows = null;
		try {
			windows =
				SlidingGene.slidingGene(
					config.getInputFileName(),
					config.getWindowSize(),
					config.getStepSize(),
					config.isAminoAcid(),
					degap);
		} catch (FileNotFoundException e) {
			throw new ApplicationException("Could not open file " + config.getInputFileName());
		} catch (ParameterProblemException e) {
			throw new ApplicationException("Error: " + e.getMessage());
		} catch (IOException e) {
			throw new ApplicationException("I/O error while reading " + config.getInputFileName());
		} catch (FileFormatException e) {
			throw new ApplicationException("File format exception while reading "
					+ config.getInputFileName()
					+ ":\n"
					+ e.getMessage());
		} catch (AlignmentException e) {
			throw new ApplicationException("Input data error: " + e.getMessage());
		}

		return windows;
	}

	private static void doLogWindow(InputStream input, String baseName) throws IOException {
		BufferedReader in
			= new BufferedReader(new InputStreamReader(input));
		String s = null;
		do {
			s = in.readLine();
			if (s != null)
				System.err.println("[" + baseName + "] " + s);
		} while (s != null);
	}
	
	protected static List plotSelected(int[] taxaIndexes, int numTaxa,
									   File inputDir, int analysisMethod)
			throws ApplicationException {
		List results = retrieveResultValues(taxaIndexes, numTaxa, inputDir, analysisMethod);

		List allResults = new ArrayList();
		allResults.add(results);

        return allResults;
	}

	/*
	 * returns the bootscanSupport as well.
	 */
	public static float gnuPlotResults(PhylisConfig config, File outputDir, int queryIndex,
									  int analysisMethod, File gnuPlotFile)
			throws ApplicationException, FileNotFoundException {
		List results = retrieveClustersValues(new int[]{queryIndex}, config.getTaxa().size(), config.getClusters(),
										      outputDir, analysisMethod);

		int[] numWindows = new int[config.getClusters().size()];
		
		PrintStream out = new PrintStream(new FileOutputStream(gnuPlotFile));
		for (int j = -1; j < ((List) results.get(0)).size(); ++j) {
			if (j != -1)
				out.print(config.getWindowSize() / 2 + j * config.getStepSize());

			int maxSupportCluster = -1;
			float maxSupport = -1;
			for (int i = 0; i < config.getClusters().size(); ++i) {
				PhylisConfig.Cluster cluster = (PhylisConfig.Cluster) config.getClusters().get(i);
				if (j == -1)
					out.print("\t" + cluster.getName());
				else {
					List clusterValues = (List) results.get(i);
					float f = ((Float) (clusterValues.get(j))).floatValue();
					out.print("\t" + f);
					
					if (f > maxSupport) {
						maxSupportCluster = i;
						maxSupport = f;
					}
				}
			}

			if (j != -1)
				++numWindows[maxSupportCluster];
			
			out.println();
		}
		
		int maxWindowsCluster = -1;
		int maxWindows = 0;
		
		for (int i = 0; i < config.getClusters().size(); ++i) {
			if (numWindows[i] > maxWindows) {
				maxWindows = numWindows[i];
				maxWindowsCluster = i;
			}
		}
		
		return (float)maxWindows / ((List) results.get(0)).size();
	}

	public static void plotResults(List resultLists, List valueNames, List XValues, File outputFileName)
			throws ApplicationException {
		/*
		 * CSV output
		 */
		try {
			FileOutputStream outputFile = new FileOutputStream(outputFileName);
			Writer writer = new OutputStreamWriter(outputFile);
			final char endl = '\n';

			/*
			 * Column heads
			 */
			for (Iterator i = valueNames.iterator(); i.hasNext();) {
				String columnName = (String) i.next();
                
				writer.write(",\"" + columnName + "\"");
			}
			writer.write(endl);
            
			/*
			 * Value rows
			 */
			for (int i = 0; i < ((List) resultLists.get(0)).size(); ++i) {
				writer.write(String.valueOf(XValues.get(i)));
                
				for (Iterator j = resultLists.iterator(); j.hasNext();) {
					List resultJ = (List) j.next();
					writer.write("," + (Float)resultJ.get(i));
				}
				writer.write(endl);
			}

			writer.flush();
		} catch (FileNotFoundException e) {
			throw new ApplicationException(e.getMessage());
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		}
	}

	private static List retrieveResultValues(int[] taxaIndexes, int numTaxa, File inputDirOrFile, int analysisMethod)
			throws ApplicationException {
		StringBuffer posMaskBuf = new StringBuffer();
		StringBuffer negMaskBuf = new StringBuffer();

		if (taxaIndexes.length < 2) {
			System.err.println("length = " + taxaIndexes.length);
			for (int i = 0; i < taxaIndexes.length; ++i)
				System.err.println(taxaIndexes[i]);
			throw new ApplicationException("Select 2 or more taxa");
		}

		Arrays.sort(taxaIndexes);
		int tax = 0;
		for (int i = 0; i < numTaxa; ++i) {
			if (i != taxaIndexes[tax]) {
				posMaskBuf.append('.');
				negMaskBuf.append('*');
			} else {
				posMaskBuf.append('*');
				negMaskBuf.append('.');

				// skip this, and all other duplicates in the taxaIndexes
				// list                
				while (i == taxaIndexes[tax] && tax < taxaIndexes.length - 1)
					++tax;
			}
		}

		String posMask = posMaskBuf.toString();
		String negMask = negMaskBuf.toString();
		
		System.err.println(posMask);

		String numberPattern = null;
		switch (analysisMethod) {
			case MRBAYES_ANALYSIS:
				numberPattern = "\\s*\\d+\\s+((\\d|\\.)+)";
				break;
			case PAUP_ANALYSIS:
				numberPattern = "(?:\\s+)(\\d+)";
		}
        
		Pattern posPattern = Pattern.compile("\\Q" + posMask + "\\E" + numberPattern);
		Pattern negPattern = Pattern.compile("\\Q" + negMask + "\\E" + numberPattern);
        
		/*
		 * if inputDir is a directory, iterate over all .parts files.
		 * otherwise, analyze the one file
		 */
		List results = new ArrayList();
        
        if (inputDirOrFile.isDirectory()) {
			for (int i = 0;; ++i) {
				String inputFileName;
				if (analysisMethod == MRBAYES_ANALYSIS)
					inputFileName
						= inputDirOrFile.getAbsolutePath() + File.separator + "window" + i + ".nex.parts";
				else
					inputFileName
						= inputDirOrFile.getAbsolutePath() + File.separator + "window" + i + ".nex.log";
				File inputFile = new File(inputFileName);
				if (!inputFile.exists())
					break;
        
				Matcher m = patternMatch(posPattern, negPattern, inputFile);
				if (m != null)
					results.add(Float.valueOf(m.group(1)));
				else
					results.add(new Float(0.));
			}
		} else {
			Matcher m = patternMatch(posPattern, negPattern, inputDirOrFile);
			if (m != null)
				results.add(Float.valueOf(m.group(1)));
			else
				results.add(new Float(0.));
		}

		return results;
	}

	private static Matcher patternMatch(Pattern posPattern, Pattern negPattern,
									    File inputFile)
		throws ApplicationException {
		try {
			LineNumberReader reader
			   = new LineNumberReader(new InputStreamReader(new FileInputStream(inputFile)));
		
			for (;;) {
				String s = reader.readLine();
				if (s == null)
					break;
				Matcher m = posPattern.matcher(s);
		        
				if (m.find()) {
					return m;
				}
		        
				m = negPattern.matcher(s);
		        
				if (m.find()) {
					return m;
				}                    
			}
			
			return null;
		} catch (FileNotFoundException e) {
			throw new ApplicationException(e.getMessage());
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		}
	}

	protected static List retrieveClustersValues(int[] taxaIndexes, int numTaxa, List clusters, File inputDir,
												 int analysisMethod)
			throws ApplicationException {
		List results = new ArrayList();
        
		for (Iterator i = clusters.iterator(); i.hasNext();) {
			PhylisConfig.Cluster c = (PhylisConfig.Cluster) i.next();
            
			int[] allTaxaIndexes = new int[taxaIndexes.length + c.getTaxaIndexes().length];
			for (int j = 0; j < taxaIndexes.length; ++j)
				allTaxaIndexes[j] = taxaIndexes[j];
			for (int j = 0; j < c.getTaxaIndexes().length; ++j)
				allTaxaIndexes[taxaIndexes.length + j] = c.getTaxaIndexes()[j];
        
			results.add(retrieveResultValues(allTaxaIndexes, numTaxa, inputDir, analysisMethod));
		}

		return results;
		//plotResults(results, valueNames, outputFileName, windowsize, stepsize);
	}

	static public class ClusterResult {
		PhylisConfig.Cluster cluster;
		float result;
		float clusterWithoutQuery; // cluster without query sequence
		float clusterComponentsWithQuery; // parts of cluster with query sequence

		/**
		 * @param cluster
		 * @param f
		 */
		public ClusterResult(PhylisConfig.Cluster cluster, float f) {
			this.cluster = cluster;
			result = f;
		}
	}

	protected static ClusterResult getMostProbableCluster(PhylisConfig config, int queryTaxus, int analysisMethod,
														  boolean reverseTaxa)
			throws FileNotFoundException, ParameterProblemException, IOException,
				   FileFormatException, ApplicationException {
		ClusterResult best = null;
		
		SequenceAlignment alignment =
			new SequenceAlignment(new FileInputStream(config.getInputFileName()), SequenceAlignment.FILETYPE_FASTA);
		if (reverseTaxa)
			alignment.reverseTaxa();
		//System.err.println("Degapping alignment");
		//alignment.degap();

		File outputFile = new File("analysis.nex");
		File bootstrapFile = null;
		List results = null;

		switch (analysisMethod) {
		case MRBAYES_ANALYSIS:
			if (!runMrBayes(alignment, outputFile, 0, config.getMrBayesBlock()))
				throw new ApplicationException("internal error: weirdness running mrbayes");
			bootstrapFile = new File("analysis.parts");
			break;
		case PAUP_ANALYSIS:
			if (!runPaup(alignment, outputFile, 0, config.getPaupBlock()))
				throw new ApplicationException("internal error: weirdness running mrbayes");
			bootstrapFile = new File("paup.log");
		}

		results = retrieveClustersValues(new int[] {queryTaxus}, config.getTaxa().size(),
										 config.getClusters(), bootstrapFile, analysisMethod);

		for (int i = 0; i < config.getClusters().size(); ++i) {
			PhylisConfig.Cluster cluster = (PhylisConfig.Cluster) config.getClusters().get(i);
			float f = ((Float) ((List) results.get(i)).get(0)).floatValue();
			
			if (best == null || f > best.result) {
				best = new ClusterResult(cluster, f);
			}
		}

		getClusterDetails(new int[] {queryTaxus}, best, config.getTaxa().size(), bootstrapFile, analysisMethod);
		
		return best;
	}

	/*
	 * Get also the bootstrap for the cluster without the query, and the sum of bootstrap supports
	 * of subclusters with the query.
	 */
	static void getClusterDetails(int taxaIndexes[], ClusterResult r, int numTaxa, File inputDirOrFile,
								  int analysisMethod) throws ApplicationException {
		System.err.println("determining outer");
		if (r.cluster.getTaxaIndexes().length >= 2) {
			List results = retrieveResultValues(r.cluster.getTaxaIndexes(), numTaxa, inputDirOrFile, analysisMethod);

			r.clusterWithoutQuery = ((Float) results.get(0)).floatValue();
		}
		
		int numSubClusters = 1;
		for (int i = 0; i < r.cluster.getTaxaIndexes().length; ++i)
			numSubClusters *= 2;
		
		System.err.println(numSubClusters);
		
		r.clusterComponentsWithQuery = 0;
		for (int i = 1; i < numSubClusters - 1; ++i) { // excluding the empty and full cluster
			List idxesList = new ArrayList();
			
			for (int j = 0; j < r.cluster.getTaxaIndexes().length; ++j)
				if ((i & (1 << j)) != 0)
					idxesList.add(new Integer(j));
	
			int[] indexes = new int[taxaIndexes.length + idxesList.size()];

			for (int j = 0; j < taxaIndexes.length; ++j)
				indexes[j] = taxaIndexes[j];
			for (int j = 0; j < idxesList.size(); ++j) {
				indexes[taxaIndexes.length + j] = r.cluster.getTaxaIndexes()[((Integer) idxesList.get(j)).intValue()];
			}

			System.err.println("determining inner " + i);
			List results = retrieveResultValues(indexes, numTaxa, inputDirOrFile, analysisMethod);
			
			r.clusterComponentsWithQuery += ((Float) results.get(0)).floatValue();
		}		
	}
	
	protected static void makeSlidingBayesJob(PhylisConfig config, File outputDir)
			throws ApplicationException {
		writeRunConfigFile(config, outputDir);

		List windows = runSlidingGene(config, false);

		if (windows == null)
			return;

		/*
		 * Now, for each window, we write the outputfile
		 */
		int k = 0;
		for (Iterator i = windows.iterator(); i.hasNext(); ++k) {
			SequenceAlignment a = (SequenceAlignment) i.next();

			writeWindowFile(a, outputDir, k, config.getMrBayesBlock(), false);
		}
		
		/*
		 * We add a MrBayes control file.
		 */
		File batchFileName =
			new File(outputDir + File.separator + "batch.mb");

		try {
			FileOutputStream batchFile = new FileOutputStream(batchFileName);
			Writer writer = new OutputStreamWriter(batchFile);
			final char endl = '\n';
			
			for (int i = 0; i < windows.size(); ++i) {
				writer.write("Execute window" + i + ".nex" + endl);
			}
			writer.write("Quit" + endl);
			writer.flush();
		} catch (FileNotFoundException e) {
			throw new ApplicationException(e.getMessage());
		} catch (IOException e) {
			throw new ApplicationException(e.getMessage());
		}
	}
}
