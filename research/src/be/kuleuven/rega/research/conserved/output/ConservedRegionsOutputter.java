package be.kuleuven.rega.research.conserved.output;

import java.util.Map;

import be.kuleuven.rega.research.conserved.MutationsPrevalence;

public interface ConservedRegionsOutputter {
	public void export(String group, Map<Integer, MutationsPrevalence> prevalences, int amountOfSequences);
}
