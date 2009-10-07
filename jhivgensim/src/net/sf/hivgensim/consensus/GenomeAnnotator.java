package net.sf.hivgensim.consensus;

import net.sf.hivgensim.preprocessing.SelectionWindow;
import net.sf.regadb.util.settings.RegaDBSettings;

public class GenomeAnnotator {

	public static enum Annotation {
		WILD_TYPE, MUTATION
	};

	public char referenceWildtype(String genome, String orf, String protein, int pos) {
		return SelectionWindow.getWindow(genome, orf, protein).getReferenceAaSequence().charAt(pos-1);
	}
	
	public Annotation annotate(String genome, String orf, String protein, int pos, String AA) {
		char aa = referenceWildtype(genome, orf, protein, pos);
		if (String.valueOf(aa).equals(AA)) {
			return Annotation.WILD_TYPE;
		}
		return Annotation.MUTATION;
	}

	public static void main(String[] args) {
		if (args.length != 4 && args.length != 5) {
			System.out.println("Annotate organism orf protein position [aminoacid]");
			System.exit(0);
		}
		
		String organism = args[0];
		String orf = args[1];
		String protein = args[2];
		int pos = Integer.parseInt(args[3]);
		String aa = args.length == 5 ? args[4] : null;

		RegaDBSettings.createInstance();		
		GenomeAnnotator ga = new GenomeAnnotator();
		if (aa == null) {
			System.out.println(ga.referenceWildtype(organism, orf, protein, pos));
		} else {
			Annotation a = ga.annotate(organism, orf, protein, pos, aa);
			System.out.println(a);
		}
	}

}
