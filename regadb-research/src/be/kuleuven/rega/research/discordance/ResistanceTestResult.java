package be.kuleuven.rega.research.discordance;

public class ResistanceTestResult {

	private boolean treated;
	private double gss;
	private int rule;
	private char sir;

	public ResistanceTestResult(int rule, char sir, double gss, boolean treated) {
		this.rule = rule;
		this.sir = sir;
		this.gss = gss;
		this.treated = treated;
	}

	public char getSir() {
		return sir;
	}

	public boolean isTreated() {
		return treated;
	}

	public double getGss() {
		return gss;
	}

	public int getRule() {
		return rule;
	}

}
