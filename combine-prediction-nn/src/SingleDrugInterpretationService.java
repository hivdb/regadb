import java.util.HashMap;
import java.util.Map;

import net.sf.regadb.service.wts.AbstractService;
import net.sf.regadb.service.wts.ServiceException;


public class SingleDrugInterpretationService extends AbstractService {

	private String algorithm;
	private String drug;
	private String sequence;

	public SingleDrugInterpretationService(String algorithm, String drug, String sequence){
		this.algorithm = algorithm;
		this.drug = drug;
		this.sequence = sequence;
	}
	
	@Override
	protected void init() {
		setService("SingleDrugInterpretService");
		setUrl("http://localhost:8080/wts/services/");
		Map<String, String> map = new HashMap<String, String>();
		map.put("drug", drug);
		map.put("algorithm", algorithm);
		map.put("sequence", sequence);
		setInputs(map);
		Map<String, String> out = new HashMap<String, String>();
		out.put("gss", "");
		out.put("errors", "");
		out.put("command", "");
		setOutputs(out);
	}

	private double result;
	
	@Override
	protected void processResults() throws ServiceException {
		Map<String, String> outputs = getOutputs();
		System.err.println("SingleDrugInterpretationService:");
		System.err.println(outputs);
		this.result = Double.parseDouble(outputs.get("gss"));
	}

	public double getGss() {
		return result;
	}	
}
