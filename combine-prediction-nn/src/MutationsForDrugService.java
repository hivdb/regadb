import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.service.wts.AbstractService;
import net.sf.regadb.service.wts.ServiceException;


public class MutationsForDrugService extends AbstractService {

	private String algorithm;
	private String drug;

	public MutationsForDrugService(String algorithm, String drug){
		this.algorithm = algorithm;
		this.drug = drug;
	}
	
	@Override
	protected void init() {
		setService("MutationsForDrug");
		setUrl("http://localhost:8080/wts/services/");
		Map<String, String> map = new HashMap<String, String>();
		map.put("drug", drug);
		map.put("algorithm", algorithm);
		setInputs(map);
		Map<String, String> out = new HashMap<String, String>();
		out.put("mutations", "");
		out.put("errors", "");
		out.put("command", "");
		setOutputs(out);
	}

	private String result;
	
	@Override
	protected void processResults() throws ServiceException {
		Map<String, String> outputs = getOutputs();
		System.err.println("MutationsForDrugService:");
		System.err.println(outputs);
		this.result = outputs.get("mutations");
	}

	public String getResult() {
		return result;
	}	
	
	public Set<String> getMutations(){
		HashSet<String> set = new HashSet<String>();
		for(String mut: Arrays.asList(result.split(","))){
			set.add(mut.trim());
		}
		return set;
	}
}
