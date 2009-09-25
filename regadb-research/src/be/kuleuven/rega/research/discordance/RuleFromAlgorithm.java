package be.kuleuven.rega.research.discordance;

public class RuleFromAlgorithm {

	@Override
	public String toString() {
		return "algorithm " + algorithm + " rule=" + rule;
	}

	private Algorithm algorithm;
	private int rule;
	
	public Algorithm getAlgorithm() {
		return algorithm;
	}
	
	public int getRule() {
		return rule;
	}

//	Rule = 0 .. n
//	algorithm = 0 .. N_alg
//	-> rule*N_alg + alg is a unique id for each combination
	@Override
	public int hashCode() {
		return rule*Algorithm.values().length+algorithm.ordinal();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleFromAlgorithm other = (RuleFromAlgorithm) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (rule != other.rule)
			return false;
		return true;
	}

	public RuleFromAlgorithm(Algorithm algorithm, int rule) {
		super();
		this.algorithm = algorithm;
		this.rule = rule;
	}
	
}
