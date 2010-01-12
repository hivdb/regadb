package be.kuleuven.rega.research.conserved.workshop2009;

import java.io.File;

import net.sf.hivgensim.preprocessing.Utils;
import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.db.Protein;
import be.kuleuven.rega.research.conserved.ConservedRegions;
import be.kuleuven.rega.research.conserved.ConservedRegionsOutput;
import be.kuleuven.rega.research.conserved.Selector;
import be.kuleuven.rega.research.conserved.SequencesExperience;
import be.kuleuven.rega.research.conserved.groupers.SubtypeGrouper;
import be.kuleuven.rega.research.conserved.output.MutationSetOutputter;
import be.kuleuven.rega.research.conserved.selector.ClassExperienceSelector;
import be.kuleuven.rega.research.conserved.selector.RegimenExperienceSelector;
import be.kuleuven.rega.research.conserved.selector.TreatmentSelector;
import be.kuleuven.rega.research.conserved.selector.TreatmentSelector.Mode;

public class PrevalenceTPVMutations {
	private String account;
	private String password;
	
	public static void main(String [] args) {
		String [] mutations = {"24M", "55R", "74P", "83D"};
		
		ClassExperienceSelector pi_experience = new ClassExperienceSelector("PI");
		pi_experience.excludeDrug("TPV");

		PrevalenceTPVMutations prev = new PrevalenceTPVMutations(args[0], args[1]);
		
		prev.exportOneTable(args[2], "naive.csv", new TreatmentSelector(Mode.Naive), mutations);
		prev.exportOneTable(args[2], "pi-tpv.csv", pi_experience, mutations);	
		prev.exportOneTable(args[2], "tpv.csv", new RegimenExperienceSelector("TPV"), mutations);
	}
	
	public PrevalenceTPVMutations(String account, String password) {
		this.account = account;
		this.password = password;
	}
	
	public void exportOneTable(String path, String fileName, Selector selector, String ... mutations) {
		Protein p = Utils.getProtein("HIV-1", "pol", "PR");
		
		ConservedRegionsOutput cro = new ConservedRegionsOutput(p);
		MutationSetOutputter mso = new MutationSetOutputter(new File(path + File.separatorChar + fileName), mutations);
		cro.addOutputter(mso);
		
		ConservedRegions cr = new ConservedRegions(cro, selector);
		
		SequencesExperience se = new SequencesExperience(cr, p, new SubtypeGrouper());

		QueryInput input = new FromDatabase(account, password, se);
		
		input.run();
		mso.writeTable();
	}
}
