package net.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.hivgensim.queries.framework.utils.TherapyUtils;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

public class CheckTherapyOverlap extends DefaultQueryOutput<Patient> {

	public CheckTherapyOverlap(File file) throws FileNotFoundException {
		super(new PrintStream(file));
	}

	public void process(Patient p) {
		try{
			List<Therapy> th = TherapyUtils.sortTherapies(p.getTherapies());
			for(int i = 0 ; i < th.size() - 1 ; i++){
				Therapy t = th.get(i);
				Therapy t1 = th.get(i+1);
				Date d = t.getStopDate();
				Date d1 = t1.getStartDate();
				if(d.after(d1)){
					getOut().println(
							p.getDatasets().iterator().next().getDescription() + "," +
							p.getPatientId() + "," +
							t.getStartDate() + "," +
							t.getStopDate() + "," + 
							t1.getStartDate() + "," +
							t1.getStopDate() + "," +
							t1.getStopDate().before(t.getStopDate()) + "," +
							TherapyUtils.getDrugsString(t) + "," +
							TherapyUtils.getDrugsString(t1)														
					);
				}
			}		
		}catch(NullPointerException e){
			e.printStackTrace();
		}
	}
}
