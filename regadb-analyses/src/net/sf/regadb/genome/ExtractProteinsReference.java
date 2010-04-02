package net.sf.regadb.genome;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sf.regadb.db.Genome;
import net.sf.regadb.db.OpenReadingFrame;
import net.sf.regadb.db.Protein;
import net.sf.regadb.io.importXML.ImportGenomes;
import net.sf.regadb.service.wts.RegaDBWtsServer;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ExtractProteinsReference {
	public static void main(String [] args) {
		if (args.length < 1) {
			System.err.println("ExtractProteinsReference organism");
		}
		
		RegaDBSettings.createInstance();
		
		String genome = args[0];
		
        File genomesFile = null;
        try {
            genomesFile = RegaDBWtsServer.getGenomes();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        final ImportGenomes imp = new ImportGenomes();
        Collection<Genome> genomes = imp.importFromXml(genomesFile);
        
        for (Genome g : genomes) {
        	if (g.getOrganismName().equals(genome))
        		for (OpenReadingFrame orf : g.getOpenReadingFrames()) {
        			for (Protein p : orf.getProteins()) {
        				String reference = 
        					orf.getReferenceSequence().substring(
        							p.getStartPosition() - 1,
        							p.getStopPosition() - 1);
        				System.out.println(p.getAbbreviation() + ":" + reference);
        			}
        		}
        }
	}
}
