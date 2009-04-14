package be.kuleuven.rega.research.conserved;

import java.util.Map;

public interface ConservedRegionsOutputter {
	public void export(String group, Map<Integer, MutationsPrevalence> prevalences, int amountOfSequences);
}
