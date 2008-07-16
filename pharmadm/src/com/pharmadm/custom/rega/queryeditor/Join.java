package com.pharmadm.custom.rega.queryeditor;

import java.util.List;
import java.util.Map;

public interface Join {
	public List<String> getJoinedVariables();
	public Join cloneInContext(Map<ConfigurableWord, ConfigurableWord> originalToCloneMap) throws CloneNotSupportedException;
}
