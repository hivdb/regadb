package net.sf.regadb.ui.form.importTool.data;

import java.util.ArrayList;
import java.util.List;

public class DateDetails {
	private List<String> dateFormats = new ArrayList<String>();
	private List<String> nullDateFormats = new ArrayList<String>();
 
	public List<String> getDateFormats() {
		return dateFormats;
	}

	public void setDateFormats(List<String> dateFormats) {
		this.dateFormats = dateFormats;
	}
	
	public List<String> getNullDateFormats() {
		return nullDateFormats;
	}

	public void setNullDateFormats(List<String> nullDateFormats) {
		this.nullDateFormats = nullDateFormats;
	}
}
