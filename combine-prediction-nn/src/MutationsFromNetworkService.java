import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.regadb.service.wts.AbstractService;
import net.sf.regadb.service.wts.ServiceException;


public class MutationsFromNetworkService extends AbstractService {

	private String network;

	public MutationsFromNetworkService(String network){
		this.network = network;
	}
	
	@Override
	protected void init() {
		setService("MutationsFromNetwork");
		setUrl("http://localhost:8080/wts/services/");
		Map<String, String> map = new HashMap<String, String>();
		map.put("network", network);
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
		System.err.println("MutationsFromNetwork:");
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
