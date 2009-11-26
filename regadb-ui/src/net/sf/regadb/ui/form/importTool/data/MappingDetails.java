package net.sf.regadb.ui.form.importTool.data;

import java.util.HashMap;
import java.util.Map;

import eu.webtoolkit.jwt.WString;

public class MappingDetails {
	private Map<String, String> mappings = new HashMap<String, String>();

	public Map<String, String> getMappings() {
		return mappings;
	}

	public void setMappings(Map<String, String> mappings) {
		this.mappings = mappings;
	}
}
