package net.sf.hivgensim.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

import net.sf.hivgensim.queries.framework.DefaultQueryOutput;
import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

public class CheckTherapyOverlap extends DefaultQueryOutput<Patient> {
	
	
	
	public CheckTherapyOverlap(File file) throws FileNotFoundException {
		super(new PrintStream(file));
	}

	protected void generateOutput(List<Patient> patients) {
		for(Patient p : patients) {
			try{
				List<Therapy> th = QueryUtils.sortTherapies(p.getTherapies());
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
								QueryUtils.getDrugsString(t) + "," +
								QueryUtils.getDrugsString(t1)														
								);
					}
				}		
			}catch(NullPointerException exc){
				
			}
		}
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException {
		Query<Patient> q = new FromDatabase("gbehey0","bla123");
		new CheckTherapyOverlap(new File("/home/gbehey0/overlapping.therapies.csv")).generateOutput(q.getOutputList());


	}


}
