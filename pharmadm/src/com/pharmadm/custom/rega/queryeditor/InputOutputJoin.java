package com.pharmadm.custom.rega.queryeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InputOutputJoin implements Join {
	private InputVariable ivar;
	private OutputVariable ovar;
	
	public InputOutputJoin(InputVariable ivar, OutputVariable ovar) {
		this.ivar = ivar;
		this.ovar = ovar;
	}
	
	@Override
	public List<String> getJoinedVariables() {
		List<String> joined = new ArrayList<String>();
		joined.add(ivar.getOutputVariable().getUniqueName());
		joined.add(ovar.getUniqueName());
		return joined;
	}

	@Override
	public Join cloneInContext(
			Map<ConfigurableWord, ConfigurableWord> originalToCloneMap)
			throws CloneNotSupportedException {
		InputOutputJoin join = new InputOutputJoin((InputVariable) originalToCloneMap.get(ivar), (OutputVariable) originalToCloneMap.get(ovar));
		return join;
	}

}
