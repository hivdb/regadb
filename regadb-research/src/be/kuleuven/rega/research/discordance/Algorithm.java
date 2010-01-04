package be.kuleuven.rega.research.discordance;

public enum Algorithm {

	HIVDB,
	Rega,
	ANRS, 
	other;

	public String getLatestVersion(){
		switch(this){
		case HIVDB: return " v5.1.3";
		case Rega: return " v8.0.1";
		case ANRS: return " v2008.17";
		default: return "";
		}
	}

	public static Algorithm getAlgorithmFor(String description) {
		for (Algorithm a : values()) {
			if (description.equalsIgnoreCase(a.toString()+a.getLatestVersion())) {
				return a;
			}
		}
		return other;
	}

	public static Algorithm[] knownAlgorithms() {
		return new Algorithm[] {HIVDB, Rega, ANRS};
	}

}