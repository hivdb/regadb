package net.sf.hivgensim.bayesian;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncrementalBootstrap {

	private int minimumSupport;
	private Learner learner;
	private List<Variable> currentVariables;
	private List<List<String>> currentValues;
	
	public IncrementalBootstrap(int support, String vdIn, String idtIn, Learner learner) throws IOException {
		this.minimumSupport = support;
		this.currentVariables = new ArrayList<Variable>();
		this.currentValues = new ArrayList<List<String>>();
		readData(vdIn, idtIn);
		this.learner = learner;
	}

	private void readData(String vdIn, String idtIn) throws IOException {
		String line;
		BufferedReader read = new BufferedReader(new FileReader(vdIn));
		while((line = read.readLine()) != null){
			String[] fields = line.split("\\s");
			String name = fields[0];
			String[] values = new String[fields.length-1];
			for (int i = 1; i < fields.length; i++) {
				values[i-1] = fields[i];
			}
			this.currentVariables.add(new Variable(name, values));
		}
		read.close();
		
		read = new BufferedReader(new FileReader(idtIn));
		while((line = read.readLine()) != null){
			String[] entries = line.split("\\s");
			if(entries.length != currentVariables.size()){
				throw new IllegalArgumentException("idt file does not contain the same amount of variables as vd file:\n"+line);
			}
			List<String> tuple = new ArrayList<String>();
			for(String entry: entries){
				tuple.add(entry);
			}
			currentValues.add(tuple);
		}
	}

	public static void main(String[] args) {
		if(args.length != 9){
			System.out.println("Usage: IncrementalBootstrap bootstraps support data.vd data.idt learner iterations coolings ess param_cost");
			System.exit(1);
		}
		int arg = 0;
		int bootstraps = Integer.parseInt(args[arg]);
		arg++;
		int support = Integer.parseInt(args[arg]);
		arg++;
		String vdIn = args[arg];
		arg++;
		String idtIn = args[arg];
		arg++;
		String learnerPath = args[arg];
		arg++;
		int iterations = Integer.parseInt(args[arg]);
		arg++;
		int coolings = Integer.parseInt(args[arg]);
		arg++;
		double ess = Double.parseDouble(args[arg]);
		arg++;
		double paramCost = Double.parseDouble(args[arg]);
		Learner learner = new Learner(learnerPath, iterations, coolings, ess, paramCost, bootstraps);
		try {
			IncrementalBootstrap inc = new IncrementalBootstrap(support, vdIn, idtIn, learner);
			inc.bootstrap();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static final Pattern mutationPattern = Pattern.compile("(RT | PR)?[A-Z]?([0-9]+)([A-Z*]+|del|ins)");

	public void bootstrap() throws IOException{
		int boots = 0;
		BCourseNetwork network;
		
		while(true){
			writeVdFile(boots);

			List<BCourseNetwork> networks = learner.learnNetworks(boots, currentValues);
			ConsensusNetwork consensus = new ConsensusNetwork(networks);
			network = consensus.computeConsensus(minimumSupport);
			
			Set<Variable> connectedToDrug = new TreeSet<Variable>();
			for (Variable variable : network.getVariables()) {
				Matcher matcher = mutationPattern.matcher(variable.name);
				if(matcher.find()){
					if(hasPathToDrug(variable, connectedToDrug, network)){
						connectedToDrug.add(variable);
					}
				} else {
					connectedToDrug.add(variable);
				}
			}

			List<Variable> notConnected = new ArrayList<Variable>();
			for(Variable variable : network.getVariables()){
				if(!connectedToDrug.contains(variable)){
					notConnected.add(variable);
				}
			}
			if(notConnected.isEmpty()){
				break;
			}
			Variable remove = notConnected.get(new Random().nextInt(notConnected.size()));
			int index = this.currentVariables.indexOf(remove);
			this.currentVariables.remove(index);
			this.currentValues.remove(index);
			boots++;
		}

		network.save(new PrintStream("result.str"));
	}

	private void writeVdFile(int boots) throws IOException {
		String filename = boots+".vd";
		FileWriter fw = new FileWriter(filename);
		for (Variable var : currentVariables) {
			String line = var.name+"\t";
			int i=0;
			for(String value: var.values){
				if(i!=0){
					line += "\t";
				}
				line += value;
				i++;
			}
			fw.write(line+"\n");
		}
		fw.close();
	}

	private boolean hasPathToDrug(Variable variable,
			Set<Variable> connectedToDrug, BCourseNetwork network) {
		if(connectedToDrug.contains(variable)){
			return true;
		}
		for (Integer i : variable.parents) {
			Variable parent = network.getVariables().get(i);
			if(!mutationPattern.matcher(parent.name).find()){
				return true;
			}
			if(hasPathToDrug(parent, connectedToDrug, network)){
				connectedToDrug.add(variable);
				return true;
			}
		}
		return false;
	}

}
