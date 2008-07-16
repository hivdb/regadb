package com.pharmadm.custom.rega.queryeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OutputJoin implements Join {

	private OutputVariable ovar;

	public OutputJoin(OutputVariable ovar) {
		this.ovar = ovar;
	}

	@Override
	public List<String> getJoinedVariables() {
		List<String> joined = new ArrayList<String>();
		joined.add(ovar.getUniqueName());
		return joined;
	}

	@Override
	public Join cloneInContext(
			Map<ConfigurableWord, ConfigurableWord> originalToCloneMap)
			throws CloneNotSupportedException {
		OutputJoin join = new OutputJoin((OutputVariable) originalToCloneMap.get(ovar));
		return join;
	}

}
