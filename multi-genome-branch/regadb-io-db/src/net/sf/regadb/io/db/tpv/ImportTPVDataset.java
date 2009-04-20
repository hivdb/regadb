package net.sf.regadb.io.db.tpv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.db.DrugGeneric;
import net.sf.regadb.db.NtSequence;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.TherapyGeneric;
import net.sf.regadb.db.TherapyGenericId;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.io.db.drugs.ImportDrugsFromCentralRepos;
import net.sf.regadb.io.db.util.ConsoleLogger;
import net.sf.regadb.io.db.util.Utils;
import net.sf.regadb.io.util.IOUtils;

import org.biojava.bio.seq.Sequence;
import org.biojavax.bio.seq.RichSequenceIterator;

public class ImportTPVDataset {
	private File path;
	
	private Map<String, Patient> patientMap = new HashMap<String, Patient>();
	
	private List<DrugGeneric> genericDrugs;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	private ImportTPVDataset(File path) {
		this.path = path;
		
        ImportDrugsFromCentralRepos imDrug = new ImportDrugsFromCentralRepos();
        genericDrugs = imDrug.getGenericDrugs();
	}
	
	private DrugGeneric getDrug(String genericId) {
		for(DrugGeneric dg : genericDrugs) {
			if(dg.getGenericId().toLowerCase().equals(genericId.toLowerCase())) {
				return dg;
			}
		}
		
		return null;
	}
	
	public void run() {
        try {
        	FileReader uploadedStream = new FileReader(path.getAbsolutePath()+File.separatorChar+"samples.fasta");
            BufferedReader br = new BufferedReader(uploadedStream);
            RichSequenceIterator xna = org.biojavax.bio.seq.RichSequence.IOTools.readFastaDNA(br, null);
            while(xna.hasNext()) {
            	Sequence seq = xna.nextRichSequence();
            	Patient p = new Patient();
            	p.setPatientId(seq.getName());
            	patientMap.put(seq.getName(), p);
                
            	DrugGeneric dg = null;
            	
                if(seq.getName().toUpperCase().charAt(0)=='T' || seq.getName().toUpperCase().charAt(0)=='N') {
                	dg = getDrug("TPV");
                } else {
                	dg = getDrug("PI");
                }
                
                if(dg!=null) {
	                Therapy t = p.createTherapy(sdf.parse("01/04/2009"));
	                t.setStopDate(sdf.parse("05/04/2009"));
	                TherapyGeneric tg = new TherapyGeneric(new TherapyGenericId(t, dg),false,false);
	                t.getTherapyGenerics().add(tg);
                }
                
                ViralIsolate vi = p.createViralIsolate();
                vi.setSampleDate(sdf.parse("02/04/2009"));
                vi.setSampleId(seq.getName());
                
                NtSequence nts = new NtSequence(vi);
                vi.getNtSequences().add(nts);
                nts.setNucleotides(Utils.clearNucleotides(seq.seqString()));
                nts.setLabel("Sequence 1");
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        IOUtils.exportPatientsXML(patientMap, path.getAbsolutePath()+File.separatorChar+"patients.xml", ConsoleLogger.getInstance());
        IOUtils.exportNTXMLFromPatients(patientMap, path.getAbsolutePath()+File.separatorChar+"vi.xml", ConsoleLogger.getInstance());
	}
	
	public static void main(String [] args) {
		ImportTPVDataset importTPV = new ImportTPVDataset(new File(args[0]));
		importTPV.run();
	}
}