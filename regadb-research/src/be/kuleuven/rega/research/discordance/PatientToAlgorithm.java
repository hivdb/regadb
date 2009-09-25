package be.kuleuven.rega.research.discordance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatientToAlgorithm {

	public static final Set<SimpleMutation> allMuts = new TreeSet<SimpleMutation>();
	public static final Map<RuleFromAlgorithm, Set<AlgoLine>> result = new HashMap<RuleFromAlgorithm, Set<AlgoLine>>();

	//	TODO wat doet de berekeningstool met ongedefinieerde mutaties? (meerdere aa) 
	public static void main(String[] args) throws IOException {
		if(args.length != 2){
			System.out.println("Usage: PatientToAlgorithm patientFile algorithmFile");
		}

		//		Read the discordant mutation sets from the algorithm analysis (sequencetool)
		BufferedReader data = new BufferedReader(new FileReader(args[0]));
		String line = null;
		while((line = data.readLine()) != null){
			readAlgoData(line);
		}

		BufferedReader br2 = new BufferedReader(new FileReader(args[1]));
		line = null;
		while((line = br2.readLine()) != null){
			//		Read the discordant mutation sets from the patients (PatientDiscordance)
			readPatientData(line);
		}

		List<AlgoLine> allLines = new ArrayList<AlgoLine>();
		for(RuleFromAlgorithm rfa: result.keySet()){
			allLines.addAll(result.get(rfa));
		}
		Collections.sort(allLines, new Comparator<AlgoLine>() {
			@Override
			public int compare(AlgoLine o1, AlgoLine o2) {
				int sizeComp = new Integer(o1.getMuts().size()).compareTo(o2.getMuts().size());
				if(sizeComp != 0){
					return sizeComp;
				}
				int weightComp = new Double(o1.getWeight()).compareTo(o2.getWeight());
				if(weightComp != 0){
					return -weightComp;
				}
				return o1.getAlgorithm().compareTo(o2.getAlgorithm());
			}
		});
		for(AlgoLine dline: allLines){
			System.out.println(dline);
		}
	}
	
	/*
	 * problem with weights:
	 * suppose the following configuration:
	 * 
	 *           algorithm X         |        algorithm Y
	 * rule 1:      A, B -> R        |            A -> I
	 * rule 2:        A  -> S        |
	 * 
	 * 100 patients with mutations (A, B), rules: X1, Y1
	 * 2 patients with mutations (A), rules: X2, Y2 
	 * 
	 * after the PatientToAlgorithm has completed, the weights are as follows:
	 * 
	 * algorithm Y rule 1: 102
	 * algorithm X rule 2: 2
	 * algorithm X rule 1: 100
	 * 
	 * If we look at the most important discordant mutation set, we see this is (A).
	 * Now suppose we want to improve algorithm X. If we take the most important
	 * discordant mutation set (A), we see that the rule fired in algorithm X for this 
	 * set is rule 2. We could therefore think that rule X2 is more important than
	 * rule X1. However, from the analysis, we see that X1 causes much more discordances
	 * than rule X2. 
	 * 
	 * -> if we are only focusing on one algorithm, sort by algorithm first. Rule Y1 can
	 * then be disregarded. In that case, it is much easier to see the combination of 
	 * A and B is more important than just A.
	 */

	public static void readAlgoData(String line) {
		AlgoLine dataLine = parseAlgorithmLine(line);
		
		//Keep track of which mutations appear in the algorithm file,
		//so not all patient mutations have to be saved
		for(SimpleMutation mut: dataLine.getMuts()){
			allMuts.add(mut);
		}

		//Save the parsed line per rule, so the set that a patient matches
		//needs only be found for the rule used for that patient.
		RuleFromAlgorithm rule = new RuleFromAlgorithm(dataLine.getAlgorithm(), dataLine.getRule());
		if(result.get(rule)==null)
			result.put(rule, new TreeSet<AlgoLine>());
		result.get(rule).add(dataLine);
	}

	public static void readPatientData(String line) {
		PatientLine pline = parsePatientLine(line);
		
		//	Find the algorithm discordant mutation set that causes the patient discordance
		//	This is the set generated from the same rule as this patient that is a subset
		//	of the patient's mutations
		for(RuleFromAlgorithm ruleA: pline.getRules()){
			if(ruleA.getRule() < 0){
				//patient does not match a rule in one system,
				//but does in another -> discordance
				continue;
			}
			Set<AlgoLine> dataLines = result.get(ruleA);
			if(dataLines==null || dataLines.isEmpty()){
				//patient does not match any discordant rule
				System.err.println("no discordant algorithm set?");
				System.err.println(ruleA);
				System.err.println(pline);
				System.err.println();
				continue;
			}
			for (AlgoLine algoLine: dataLines) {
				if(algoLine == null){
					continue;
				}
				boolean succes = true;
				for (SimpleMutation mut : algoLine.getMuts()) {
					if(!pline.getMutations().contains(mut)){
						succes = false;
						break;
					}
				}

				//	If found, update its weight. This set causes a patient
				//	discordance, hence it is more important
				if(succes){
					algoLine.setWeight(algoLine.getWeight()+pline.getError());
					algoLine.addConditionalWeight(pline.isTreated()+"/"+pline.getSubtype(), pline.getError());
					break;
				}
			}
		}
	}
	
	static Pattern mutations_pat = Pattern.compile("\\{(.*)\\}");
	static Pattern patient_rules = Pattern.compile("\\s*(\\w+):\\s*(-?\\d)\\s*");
	static Pattern patient_last = Pattern.compile("\\s*diff=(.+)\\s*;\\s*treated=(.+)\\s*;\\s*subtype=(.+)\\s*$");
	static Pattern algorithm_pat = Pattern.compile("\\s*([^:]+?)\\s*r\\s*(-?\\d+)\\s*:\\s*([SIR])/([^;]+)");
	static Pattern last_pat = Pattern.compile("\\s*(\\w+)\\s*rule\\s*(\\d);\\s*diff=(.+)\\s*$");

	public static PatientLine parsePatientLine(String line) {
		Matcher matcher = mutations_pat.matcher(line);
		if(!matcher.find()){
			throw new IllegalArgumentException(line+" does not match "+mutations_pat);
		}
		Collection<SimpleMutation> mutlist = parseMutations(false, matcher.group(1));
		line = line.substring(matcher.end());

		matcher = patient_last.matcher(line);
		if(!matcher.find()){
			throw new IllegalArgumentException(line+" does not match "+patient_last);
		}
		double error = Double.parseDouble(matcher.group(1));
		boolean treated = Boolean.parseBoolean(matcher.group(2));
		String subtype = matcher.group(3);
		
		line = line.substring(0, matcher.start()).trim();
		String[] algorithms = line.split(";");
		RuleFromAlgorithm[] rfas = new RuleFromAlgorithm[algorithms.length];

		for (int i = 0; i < algorithms.length; i++) {

			matcher = patient_rules.matcher(algorithms[i]);
			if(!matcher.find()){
				throw new IllegalArgumentException(algorithms[i]+" does not match "+patient_rules);
			}
			Algorithm algorithm = Algorithm.valueOf(matcher.group(1));
			int rule = Integer.parseInt(matcher.group(2));
			rfas[i] = new RuleFromAlgorithm(algorithm, rule);
		}

		return new PatientLine(mutlist, error, treated, subtype, rfas);
	}

	public static AlgoLine parseAlgorithmLine(String line){
		Matcher matcher = mutations_pat.matcher(line);

		if(!matcher.find()){
			throw new IllegalArgumentException(line+" does not match "+mutations_pat.pattern());
		}
		String muts = matcher.group(1);
		line = line.substring(matcher.end());
		Collection<SimpleMutation> mutlist = parseMutations(true, muts);

		matcher = last_pat.matcher(line);
		if(!matcher.find()){
			throw new IllegalArgumentException(line+" does not match "+last_pat.pattern());
		}
		Algorithm algorithm = Algorithm.valueOf(matcher.group(1));
		int rule = Integer.parseInt(matcher.group(2));
		double error = Double.parseDouble(matcher.group(3));
		line = line.substring(0, matcher.start());

		String[] algLine = line.trim().split(";");
		AlgorithmData[] alData = new AlgorithmData[algLine.length];
		for (int i = 0; i < algLine.length; i++) {
			matcher = algorithm_pat.matcher(algLine[i]);
			if(!matcher.find()){
				throw new IllegalArgumentException(algLine[i]+" does not match "+algorithm_pat.pattern());
			}
			String algorithmStr = matcher.group(1);
			int aRule = Integer.parseInt(matcher.group(2));
			SIR sir = SIR.valueOf(matcher.group(3));
			double gss = Double.parseDouble(matcher.group(4));
			alData[i] = new AlgorithmData(algorithmStr, aRule, gss, sir);
			line = line.substring(matcher.end());
		}

		return new AlgoLine(mutlist, alData, algorithm, rule, error);
	}

	public static Collection<SimpleMutation> parseMutations(boolean all, String muts) {
		String[] arr = muts.split(",");
		Collection<SimpleMutation> mutlist = new TreeSet<SimpleMutation>();
		for (String mut : arr) {
			mut = mut.trim();
			char[] mutCh = mut.toCharArray();
			SimpleMutation smut = null;
			for (int i = mutCh.length-1; i >= 0; i--) {
				char c = mutCh[i];
				if(Character.isDigit(c)){
					int position = Integer.parseInt(new String(Arrays.copyOfRange(mutCh, 0, i+1)));
					if(i == mutCh.length-2){
						smut = new SimpleMutation(position, mutCh[i+1]);
					} else {
						smut = new SimpleMutation(position);
					}
					break;
				}
			}
			if(smut==null)
				throw new IllegalArgumentException("Could not parse mutations "+muts);

			if(all || allMuts.contains(smut)){
				mutlist.add(smut);
			}
		}
		return mutlist;
	}
}
