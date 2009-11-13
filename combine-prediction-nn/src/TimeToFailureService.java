import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.hivgensim.queries.framework.utils.DateUtils;
import net.sf.regadb.db.Patient;
import net.sf.regadb.db.TestResult;
import net.sf.regadb.db.TestType;
import net.sf.regadb.db.Therapy;
import net.sf.regadb.db.meta.Equals;
import net.sf.regadb.io.util.StandardObjects;
import net.sf.regadb.service.wts.AbstractService;
import net.sf.regadb.service.wts.ServiceException;

public class TimeToFailureService extends AbstractService {

	private double generations;
	private String sequence;
	private String doublepositions;
	private String wildtypes;
	private String landscape;
	private String network;
	
	public TimeToFailureService(String baneDir, String network, String landscape, String sequence){
		String fs = System.getProperty("file.separator");
		if(!baneDir.endsWith(fs)){
			baneDir += fs;
		}
		
		this.network = baneDir+network;
		this.landscape = baneDir+landscape;
		this.wildtypes = baneDir+"wildtypes";
		this.doublepositions = baneDir+"doublepositions";
		this.sequence = ">id\n"+sequence+"\n";
	}

	@Override
	protected void init() {
		setService("TimeToFailure");
		setUrl("http://localhost:8080/wts/services/");
		Map<String, String> map = new HashMap<String, String>();
		map.put("network", network);
		map.put("landscape", landscape);
		map.put("wildtypes", wildtypes);
		map.put("doublepositions", doublepositions);
		map.put("sequence", sequence);
		setInputs(map);
		Map<String, String> out = new HashMap<String, String>();
		out.put("generations", "");
		out.put("errors", "");
		out.put("command", "");
		setOutputs(out);
	}

	@Override
	protected void processResults() throws ServiceException {
		Map<String, String> outputs = getOutputs();
		System.err.println("TimeToFailureService:");
		System.err.println(outputs);
		String gensOut = outputs.get("generations");
		this.generations = Double.parseDouble(gensOut.substring("id,".length(),gensOut.length()));
	}

	public double getGenerations() {
		return generations;
	}
	
	private static TestType vltt = StandardObjects.getHiv1ViralLoadTestType();

	public static List<TestResult> filterTestResults(Collection<TestResult> trs,
			TestType testType) {
		List<TestResult> filteredTestResults = new ArrayList<TestResult>();
		for (TestResult tr : trs) {           
			if (Equals.isSameTestType(tr.getTest().getTestType(), vltt)
					&& tr.getTestDate() != null) {
				filteredTestResults.add(tr);
			}
		}

		return filteredTestResults;
	}
	
	public static long timeToFailure(Patient p, Therapy t) {
		try {
			long days = 0;
			for (TestResult tr : filterTestResults(p.getTestResults(), vltt)) {
				if (tr.getTestDate().after(t.getStopDate())
						|| tr.getTestDate().before(t.getStartDate())) {
					continue;
				}
				if (Integer.parseInt(tr.getValue().substring(1)) > 400) {
					long ndays = DateUtils.daysBetween(t.getStartDate(), tr.getTestDate());
					if(ndays > 56){
						days = days == 0 ? ndays : Math.min(ndays, days);
					}
				}
			}
			return days;
		} catch (Exception e) {
			return 0;
		}
	}

	public static double getTTFGens(String sequence, String baneDir, String network, String landscape) {
		TimeToFailureService ttf = new TimeToFailureService(baneDir, network, landscape, sequence);
		try {
			ttf.launch();
		} catch (ServiceException e) {
			System.out.println("Service "+ttf.getService()+" failed");
			e.printStackTrace();
		}
		return ttf.getGenerations();
	}
	
}
