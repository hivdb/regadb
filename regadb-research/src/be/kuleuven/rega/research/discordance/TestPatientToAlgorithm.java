package be.kuleuven.rega.research.discordance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

public class TestPatientToAlgorithm extends TestCase {

	public void setUp(){
		PatientToAlgorithm.allMuts.clear();
		PatientToAlgorithm.result.clear();
	}
	
	public void testMutationsParsing(){
		//		single mutation
		Collection<SimpleMutation> muts = PatientToAlgorithm.parseMutations(true, "1A");
		assertEquals(muts.size(),1);
		SimpleMutation theMut = muts.iterator().next();
		assertEquals(theMut,new SimpleMutation(1, 'A'));
		assertFalse(theMut.equals(new SimpleMutation(2,'A')));
		assertFalse(theMut.equals(new SimpleMutation(1,'B')));

//		test long position
		Collection<SimpleMutation> muts2b = PatientToAlgorithm.parseMutations(true, "12345A");
		assertEquals(muts2b.size(),1);
		assertEquals(muts2b.iterator().next(),new SimpleMutation(12345, 'A'));
		
//		test no saving
		PatientToAlgorithm.allMuts.add(new SimpleMutation(234, 'A'));
		Collection<SimpleMutation> muts2 = PatientToAlgorithm.parseMutations(false, "234V");
		assertEquals(muts2.size(),0);
		
//		test ambiguous
		Collection<SimpleMutation> muts3 = PatientToAlgorithm.parseMutations(true, "1ABC");
		assertEquals(muts3.size(), 1);
		SimpleMutation theMut2 = muts3.iterator().next();
		assertEquals(theMut2,new SimpleMutation(1, 'B'));
		assertEquals(theMut2,new SimpleMutation(1));
		assertEquals(theMut2,new SimpleMutation(1, 'C'));
		assertEquals(theMut2,new SimpleMutation(1, 'A'));
		assertFalse(theMut2.equals(new SimpleMutation(2,'A')));
		
//		test wrong syntax
		String[] wrong = new String[] {"1A B", "A", "A2", "1A2B", "1A 2B", "34"};
		for (int j = 0; j < wrong.length; j++) {
			try{
			PatientToAlgorithm.parseMutations(true, wrong[j]);
			assert(false);
			} catch(IllegalArgumentException e){ }
		}
		
//		test multiple
		Collection<SimpleMutation> muts4 = PatientToAlgorithm.parseMutations(true, "1A, 2B, 4D");
		assertEquals(muts4.size(),3);
		assertTrue(muts4.contains(new SimpleMutation(1, 'A')));
		assertTrue(muts4.contains(new SimpleMutation(2, 'B')));
		assertTrue(muts4.contains(new SimpleMutation(4, 'D')));
	}
	
	public void testAlgorithmReadingSingle(){
		PatientToAlgorithm.readAlgoData("(1A) ANRS r-1: S/1; HIVDB r3: I/0.5; Rega rule 3; diff=0.5");
		Map<RuleFromAlgorithm, Set<AlgoLine>> resulttest = PatientToAlgorithm.result;
		assertEquals(resulttest.keySet().size(),1);
		RuleFromAlgorithm keytest = resulttest.keySet().iterator().next();
		assertEquals(keytest,new RuleFromAlgorithm(Algorithm.Rega, 3));
		assertEquals(resulttest.get(keytest).size(), 1);
		Set<SimpleMutation> lines = new TreeSet<SimpleMutation>();
		lines.add(new SimpleMutation(1, 'A'));
		AlgorithmData[] data = new AlgorithmData[] {new AlgorithmData("ANRS", -1, 1, SIR.S), new AlgorithmData("HIVDB", 3, 0.5, SIR.I)};
		assertEquals(resulttest.get(keytest).iterator().next(), new AlgoLine(lines, data, Algorithm.Rega, 3, 0.5));
	}
	
	public void testAlgorithmReadingMultiple(){
		PatientToAlgorithm.readAlgoData("(1A) ANRS r0: S/1; Rega rule 3; diff=0.5");
		PatientToAlgorithm.readAlgoData("(1A) ANRS r0: S/1; HIVDB rule 5; diff=0.5");
		PatientToAlgorithm.readAlgoData("(2F) ANRS r0: S/1; Rega rule 3; diff=0.5");
		PatientToAlgorithm.readAlgoData("(2F) ANRS r0: S/1; ANRS rule 0; diff=0.5");
		PatientToAlgorithm.readAlgoData("(2F) ANRS r0: S/1; HIVDB rule 2; diff=0.5");
		Map<RuleFromAlgorithm, Set<AlgoLine>> resulttest = PatientToAlgorithm.result;
		Set<RuleFromAlgorithm> rfas = resulttest.keySet();
		assertEquals(rfas.size(), 4);
		RuleFromAlgorithm rega = new RuleFromAlgorithm(Algorithm.Rega, 3);
		RuleFromAlgorithm h1 = new RuleFromAlgorithm(Algorithm.HIVDB, 5);
		RuleFromAlgorithm h2 = new RuleFromAlgorithm(Algorithm.HIVDB, 2);
		RuleFromAlgorithm anrs = new RuleFromAlgorithm(Algorithm.ANRS, 0);
		assertTrue(rfas.contains(rega));
		assertTrue(rfas.contains(h1));
		assertTrue(rfas.contains(h2));
		assertTrue(rfas.contains(anrs));
		AlgorithmData[] alList = new AlgorithmData[] {new AlgorithmData("ANRS", 0, 1, SIR.S)};
		Collection<SimpleMutation> firstMut = new TreeSet<SimpleMutation>();
		firstMut.add(new SimpleMutation(1, 'A'));
		Collection<SimpleMutation> secondMut = new TreeSet<SimpleMutation>();
		secondMut.add(new SimpleMutation(2, 'F'));
		Set<AlgoLine> regamut = resulttest.get(rega);
		Set<AlgoLine> h1mut = resulttest.get(h1);
		Set<AlgoLine> h2mut = resulttest.get(h2);
		Set<AlgoLine> anrsmut = resulttest.get(anrs);
		assertEquals(regamut.size(), 2);
		assertTrue(regamut.contains(new AlgoLine(firstMut, alList, Algorithm.Rega, 3, 0.5)));
		assertTrue(regamut.contains(new AlgoLine(secondMut, alList, Algorithm.Rega, 3, 0.5)));
		assertEquals(h1mut.size(), 1);
		assertEquals(h1mut.iterator().next(), new AlgoLine(firstMut, alList, Algorithm.HIVDB, 5, 0.5));
		assertEquals(h2mut.size(), 1);
		assertEquals(h2mut.iterator().next(), new AlgoLine(secondMut, alList, Algorithm.HIVDB, 2, 0.5));
		assertEquals(anrsmut.size(), 1);
		assertEquals(anrsmut.iterator().next(), new AlgoLine(secondMut, alList, Algorithm.ANRS, 0, 0.5));
	}
	
	public void testPatientReading(){
		SimpleMutation first = new SimpleMutation(2, 'D');
		SimpleMutation second = new SimpleMutation(5, 'F');
		PatientToAlgorithm.allMuts.add(first);
		PatientToAlgorithm.allMuts.add(second);
		PatientLine pline = PatientToAlgorithm.parsePatientLine("(1A, 2D, 4E, 5F) Rega: 5; HIVDB: 3; diff=0.01; treated=true; subtype=HIV1 subtype B");
		assertEquals(pline.getMutations().size(), 2);
		Iterator<SimpleMutation> mutit = pline.getMutations().iterator();
		assertEquals(mutit.next(), first);
		assertEquals(mutit.next(), second);
		assertEquals(pline.getRules().length, 2);
		List<RuleFromAlgorithm> rules = Arrays.asList(pline.getRules());
		assertTrue(rules.contains(new RuleFromAlgorithm(Algorithm.Rega, 5)));
		assertTrue(rules.contains(new RuleFromAlgorithm(Algorithm.Rega, 5)));
		assertEquals(pline.getError(), 0.01);
		assertEquals(pline.isTreated(), true);
		assertEquals(pline.getSubtype(), "HIV1 subtype B");
	}

	public void testWeightSingleExactMatch(){
		PatientToAlgorithm.readAlgoData("(1A, 2B) Rega r0: R/3; Rega rule 3; diff=0.5");
		PatientToAlgorithm.readPatientData("(1A, 2B) Rega: 3; diff=3; treated=true; subtype=C");
		Map<RuleFromAlgorithm, Set<AlgoLine>> result = PatientToAlgorithm.result;
		AlgoLine line = result.get(new RuleFromAlgorithm(Algorithm.Rega, 3)).iterator().next();
		assertEquals(line.getWeight(), 3d);
	}
	
	public void testWeightSingleSuperset(){
		PatientToAlgorithm.readAlgoData("(1A) Rega r0: R/3; Rega rule 3; diff=0.5");
		PatientToAlgorithm.readPatientData("(1A, 2B) Rega: 3; diff=3; treated=true; subtype=C");
		Map<RuleFromAlgorithm, Set<AlgoLine>> result = PatientToAlgorithm.result;
		AlgoLine line = result.get(new RuleFromAlgorithm(Algorithm.Rega, 3)).iterator().next();
		assertEquals(line.getWeight(), 3d);
	}
	
	public void testWeightMultiplePatients(){
		PatientToAlgorithm.readAlgoData("(1A) Rega r0: R/3; Rega rule 3; diff=0.5");
		PatientToAlgorithm.readPatientData("(1A, 2B) Rega: 3; diff=3; treated=true; subtype=C");
		PatientToAlgorithm.readPatientData("(1A, 3D) Rega: 3; diff=2; treated=true; subtype=C");
		Map<RuleFromAlgorithm, Set<AlgoLine>> result = PatientToAlgorithm.result;
		AlgoLine line = result.get(new RuleFromAlgorithm(Algorithm.Rega, 3)).iterator().next();
		assertEquals(line.getWeight(), 5d);
	}
	
	public void testWeightMultipleAlgo(){
		List<String> algoLines = new ArrayList<String>();
		algoLines.add("(1A) Rega r0: R/3; Rega rule 3; diff=0.5");
		algoLines.add("(2B) Rega r0: R/3; Rega rule 3; diff=3");
		algoLines.add("(3F) Rega r0: R/3; Rega rule 3; diff=0.5");
		algoLines.add("(1A, 3F) Rega r0: R/3; Rega rule 3; diff=0.01");
		Collections.shuffle(algoLines);
		for (String line : algoLines) {
			PatientToAlgorithm.readAlgoData(line);
		}

		List<String> patLines = new ArrayList<String>();
		patLines.add("(1A, 2B) Rega: 3; diff=3; treated=true; subtype=C"); //should match the second, since higher diff
		patLines.add("(1A, 3D) Rega: 3; diff=2; treated=true; subtype=C"); //should match the first, since no other
		patLines.add("(1A, 3F, 2B) Rega: 3; diff=4; treated=true; subtype=C"); //should match the fourth, since larger set
		Collections.shuffle(patLines);
		for (String line : patLines) {
			PatientToAlgorithm.readPatientData(line);
		}
		
		Map<RuleFromAlgorithm, Set<AlgoLine>> result = PatientToAlgorithm.result;
		Set<SimpleMutation> first = new TreeSet<SimpleMutation>();
		first.add(new SimpleMutation(1, 'A'));
		Set<SimpleMutation> second = new TreeSet<SimpleMutation>();
		second.add(new SimpleMutation(2, 'B'));
		Set<SimpleMutation> third = new TreeSet<SimpleMutation>();
		third.add(new SimpleMutation(1, 'A'));
		third.add(new SimpleMutation(3, 'F'));
		Set<AlgoLine> resultSet = result.get(new RuleFromAlgorithm(Algorithm.Rega, 3));
		for (AlgoLine algoLine : resultSet) {
			if(algoLine.getMuts().equals(first)){
				assertEquals(algoLine.getWeight(), 2d);
			} else if (algoLine.getMuts().equals(second)){
				assertEquals(algoLine.getWeight(), 3d);
			} else if (algoLine.getMuts().equals(third)){
				assertEquals(algoLine.getWeight(), 4d);
			}
		}
	}
	
	public void testWeightConditional(){
		List<String> algoLines = new ArrayList<String>();
		algoLines.add("(1A) Rega r0: R/3; Rega rule 3; diff=0.5");
		algoLines.add("(2B) Rega r0: R/3; Rega rule 3; diff=3");
		Collections.shuffle(algoLines);
		for (String line : algoLines) {
			PatientToAlgorithm.readAlgoData(line);
		}

		List<String> patLines = new ArrayList<String>();
		patLines.add("(1A) Rega: 3; diff=1; treated=true; subtype=B");
		patLines.add("(1A) Rega: 3; diff=10; treated=true; subtype=C");
		patLines.add("(1A) Rega: 3; diff=100; treated=false; subtype=C");
		patLines.add("(1A) Rega: 3; diff=1000; treated=false; subtype=B");
		patLines.add("(1A) Rega: 3; diff=10000; treated=false; subtype=E");
		patLines.add("(2B) Rega: 3; diff=10; treated=true; subtype=C");
		patLines.add("(2B) Rega: 3; diff=1; treated=true; subtype=C");
		Collections.shuffle(patLines);
		for (String line : patLines) {
			PatientToAlgorithm.readPatientData(line);
		}
		
		Map<RuleFromAlgorithm, Set<AlgoLine>> result = PatientToAlgorithm.result;
		Set<SimpleMutation> first = new TreeSet<SimpleMutation>();
		first.add(new SimpleMutation(1, 'A'));
		Set<SimpleMutation> second = new TreeSet<SimpleMutation>();
		second.add(new SimpleMutation(2, 'B'));
		Set<AlgoLine> resultSet = result.get(new RuleFromAlgorithm(Algorithm.Rega, 3));
		for (AlgoLine algoLine : resultSet) {
			if(algoLine.getMuts().equals(first)){
				assertEquals(algoLine.getWeight(), 11111d);
				assertEquals(algoLine.getConditionalWeight("true/B"), 1d);
				assertEquals(algoLine.getConditionalWeight("true/C"), 10d);
				assertEquals(algoLine.getConditionalWeight("false/B"), 1000d);
				assertEquals(algoLine.getConditionalWeight("false/C"), 100d);
				assertEquals(algoLine.getConditionalWeight("false/E"), 10000d);
			} else if (algoLine.getMuts().equals(second)){
				assertEquals(algoLine.getWeight(), 11d);
				assertEquals(algoLine.getConditionalWeight("true/C"), 11d);
				assertEquals(algoLine.getConditionalWeight("false/C"),-1d);
				assertEquals(algoLine.getConditionalWeight("true/B"),-1d);
			}
		}
	}
}
