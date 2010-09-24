package be.kuleuven.rega.variability.hiv2;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import net.sf.hivgensim.queries.framework.QueryInput;
import net.sf.hivgensim.queries.framework.TableQueryOutput;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.csv.Table;
import net.sf.regadb.db.DrugClass;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.ViralIsolate;
import net.sf.regadb.util.settings.RegaDBSettings;

public class ExportTherapyTable extends TableQueryOutput<Patient> {
	private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public ExportTherapyTable(Table out, File file, TableOutputType type) {
		super(out, file, type);
	}

	public void process(Patient p) {
		for (ViralIsolate vi : p.getViralIsolates()) {
			if (vi.getNtSequences().size() > 1)
				throw new RuntimeException("Viral isolate " + vi.getSampleId() + " has >1 sequences.");
			
			Collection<DrugClass> classes = 
				TherapyUtils.getClassesBefore(p.getTherapies(), vi.getSampleDate());
			
			addColumn(vi.getSampleId());
			addColumn(dateFormat.format(vi.getSampleDate()));
			addColumn(experience(classes), true);
		}
	}
	
	private String experience(Collection<DrugClass> classes) {
		String experience = TherapyUtils.getDrugClassesString(classes);
		if (experience.equals(""))
			experience = "naive";

		return experience;
	}
	
	public static void main(String [] args) {
		if(args.length != 3){
			System.err.println("Usage: ExportTherapyTable output_file uid passwd");
			System.exit(1);
		}
        RegaDBSettings.createInstance();
		
		ExportTherapyTable ett = new ExportTherapyTable(new Table(), new File(args[0]), TableOutputType.CSV);
		QueryInput qi = new FromDatabase(args[1], args[2], ett);
		qi.run();
	}
}
