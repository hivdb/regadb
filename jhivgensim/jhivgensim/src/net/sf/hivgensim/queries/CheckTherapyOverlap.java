package net.sf.hivgensim.queries;

import java.io.File;
import java.util.Date;
import java.util.List;

import net.sf.hivgensim.queries.framework.Query;
import net.sf.hivgensim.queries.framework.QueryOutput;
import net.sf.hivgensim.queries.framework.QueryUtils;
import net.sf.hivgensim.queries.input.FromDatabase;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.Therapy;

public class CheckTherapyOverlap extends QueryOutput<Patient> {
	
	
	
	public CheckTherapyOverlap(File file) {
		super(file);
	}

	public void generateOutput(Patient p) {
		try{
			List<Therapy> th = QueryUtils.sortTherapies(p.getTherapies());
			for(int i = 0 ; i < th.size() - 1 ; i++){
				Therapy t = th.get(i);
				Therapy t1 = th.get(i+1);
				Date d = t.getStopDate();
				Date d1 = t1.getStartDate();
				if(d.after(d1)){
					out.println(
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
	
	
	
	public static void main(String[] args) {
		Query<Patient> q = new FromDatabase("gbehey0","bla123");
		new CheckTherapyOverlap(new File("/home/gbehey0/overlapping.therapies.csv")).generateOutput(q);


	}


}
