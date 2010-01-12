package be.kuleuven.rega.research.discordance;

public class AlgorithmData {

	private Algorithm algorithm;
	private double gss;
	private SIR sir;
	private int rule;
	
	public AlgorithmData(String algorithm, int rule, double gss, SIR sir) {
		super();
		this.algorithm = Algorithm.valueOf(algorithm);
		this.gss = gss;
		this.sir = sir;
		this.rule = rule;
	}

	public final int getRule() {
		return rule;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlgorithmData other = (AlgorithmData) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (Double.doubleToLongBits(gss) != Double.doubleToLongBits(other.gss))
			return false;
		if (sir == null) {
			if (other.sir != null)
				return false;
		} else if (!sir.equals(other.sir))
			return false;
		return true;
	}

	public Algorithm getAlgorithm() {
		return algorithm;
	}
	
	public double getGss() {
		return gss;
	}
	
	public SIR getSir() {
		return sir;
	}

	@Override
	public String toString() {
		return "[algorithm=" + algorithm + ", gss=" + gss
				+ ", sir=" + sir + "]";
	}
	
}
