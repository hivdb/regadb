package net.sf.regadb.ui.form.importTool.data;

import java.util.ArrayList;
import java.util.List;

public class ScriptDefinition {
	private String script;
	private List<String> newColumns = new ArrayList<String>();
	
	public ScriptDefinition() {
		script = "";
	}
	
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public List<String> getNewColumns() {
		return newColumns;
	}

	public void setNewColumns(List<String> newColumns) {
		this.newColumns = newColumns;
	}
}
