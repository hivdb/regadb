package com.pharmadm.custom.rega.queryeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class InputJoin implements Join {
	private InputVariable ivar1;
	private InputVariable ivar2;
	
	public InputJoin(InputVariable ivar1, InputVariable ivar2) {
		this.ivar1 = ivar1;
		this.ivar2 = ivar2;
	}
	
	public List<String> getJoinedVariables() {
		List<String> joined = new ArrayList<String>();
		joined.add(ivar1.getOutputVariable().getUniqueName());
		joined.add(ivar2.getOutputVariable().getUniqueName());
		return joined;
	}

	public Join cloneInContext(
			Map<ConfigurableWord, ConfigurableWord> originalToCloneMap)
			throws CloneNotSupportedException {
		InputJoin join = new InputJoin((InputVariable) originalToCloneMap.get(ivar1), (InputVariable) originalToCloneMap.get(ivar2));
		return join;
	}

}
