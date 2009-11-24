package net.sf.regadb.ui.form.importTool.data;

import java.util.ArrayList;
import java.util.List;

public class RegimenDetails extends MappingDetails {
	private List<String> delimiters = new ArrayList<String>();

	public List<String> getDelimiters() {
		return delimiters;
	}

	public void setDelimiters(List<String> delimiters) {
		this.delimiters = delimiters;
	}
}
