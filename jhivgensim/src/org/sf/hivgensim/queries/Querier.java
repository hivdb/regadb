package org.sf.hivgensim.queries;

import jargs.gnu.CmdLineParser;
import jargs.gnu.CmdLineParser.Option;

import java.io.File;

import net.sf.regadb.db.Patient;

public class Querier {

	public static void printUsageAndExit(){
		System.err.println("from database: -d -l login -p password -o output");
		System.err.println("from xml: -x -l login -p password -i input -o output");
		System.err.println("from snapshot: -s -i input -o output");
	}

	public static void main(String [] args) {
		CmdLineParser cmdLineParser = new CmdLineParser();
		Option inputOption = cmdLineParser.addStringOption('i', "input");
		Option outputOption = cmdLineParser.addStringOption('o', "output");
		Option loginOption = cmdLineParser.addStringOption('l', "login");
		Option passwordOption = cmdLineParser.addStringOption('p', "password");

		Option databaseOption = cmdLineParser.addBooleanOption('d',"database");
		Option xmlOption = cmdLineParser.addBooleanOption('x',"xml");
		Option snapshotOption = cmdLineParser.addBooleanOption('s',"snapshot");

		try {
			cmdLineParser.parse(args);
		} catch (CmdLineParser.OptionException e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

		QueryInput queryInput = null;

		if((Boolean)cmdLineParser.getOptionValue(databaseOption))
		{
			String login = (String) cmdLineParser.getOptionValue(loginOption);
			String password = (String) cmdLineParser.getOptionValue(passwordOption);
			queryInput = new FromDatabase(login,password);
		}
		else if((Boolean)cmdLineParser.getOptionValue(xmlOption))
		{
			String login = (String) cmdLineParser.getOptionValue(loginOption);
			String password = (String) cmdLineParser.getOptionValue(passwordOption);
			String file = (String) cmdLineParser.getOptionValue(inputOption);
			queryInput = new FromXml(new File(file),login,password);
		}
		else if((Boolean)cmdLineParser.getOptionValue(snapshotOption)){
			String file = (String) cmdLineParser.getOptionValue(inputOption);
			queryInput = new FromSnapshot(new File(file));
		}
		else{
			printUsageAndExit();
		}
		
		String output = (String) cmdLineParser.getOptionValue(outputOption);
		
		long start = System.currentTimeMillis();
		QueryImpl<Patient> q = new GetNaiveSequences(queryInput);
		ToSnapshot<Patient> tss = new ToSnapshot<Patient>(q,new File(output+".snapshot"));
		ToMutationTable tmt = new ToMutationTable(q,new File(output));
		tss.generateOutput();
		tmt.generateOutput();
		long stop = System.currentTimeMillis();
		System.err.println("done in " + (stop - start) + " ms");
	}


}
