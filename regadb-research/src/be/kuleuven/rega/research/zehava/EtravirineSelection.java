package be.kuleuven.rega.research.zehava;

import java.util.HashMap;

import net.sf.hivgensim.queries.framework.IQuery;
import net.sf.hivgensim.queries.framework.Query;

public class EtravirineSelection extends Query<EtravirineRecord, EtravirineRecord> {

	private HashMap<String, EtravirineRecord> naive = new HashMap<String, EtravirineRecord>();
	private HashMap<String, EtravirineRecord> treated = new HashMap<String, EtravirineRecord>();
	
	protected EtravirineSelection(IQuery<EtravirineRecord> nextQuery) {
		super(nextQuery);
	}

	public void process(EtravirineRecord input) {
		if(!input.getHivdbRemarks().equals("assumed: 318Y") ||  !input.getRegaRemarks().equals("assumed: 318Y")){
			if(!input.getHivdbRemarks().equals("null") && !input.getRegaRemarks().equals("null")){
				System.err.println(input.getHivdbRemarks()+" "+input.getRegaRemarks());
				return;
			}
		}
		if(input.isNaive()){
			if(!naive.containsKey(input.getPatientId()) || naive.get(input.getPatientId()).getSampleDate().before(input.getSampleDate())){
				naive.put(input.getPatientId(),input);
			}
		} else {
			if(!treated.containsKey(input.getPatientId()) || treated.get(input.getPatientId()).getSampleDate().before(input.getSampleDate())){
				treated.put(input.getPatientId(),input);
			}
		}
	}
	
	public void close() {
		for(EtravirineRecord er : naive.values()){
			getNextQuery().process(er);
		}
		for(EtravirineRecord er : treated.values()){
			getNextQuery().process(er);
		}
		getNextQuery().close();
	}

}
