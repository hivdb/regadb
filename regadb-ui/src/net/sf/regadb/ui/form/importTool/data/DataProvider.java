package net.sf.regadb.ui.form.importTool.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import net.sf.regadb.util.script.Python;

public class DataProvider {
	private Sheet sheet;
	private ScriptDefinition script;

	public DataProvider(Sheet sheet, ScriptDefinition script) {
		this.sheet = sheet;
		this.script = script;
	}
 	
	public List<String> getHeaders() {
		List<String> headers = new ArrayList<String>();
		
		for (int i = 0; i < sheet.getColumns(); i++) {
			headers.add(sheet.getCell(i, 0).getContents().trim());
		}
		
		for (String newCol : script.getNewColumns()) {
			headers.add(newCol.trim());
		}
		
		return headers;
	}
	
	public String getValue(int row, String header) {
		Map<String, String> rowValues = new HashMap<String, String>();
		
		for (int r = 1; r < sheet.getRows(); r++) {
			if (row == r) {
				for (int c = 0; c < sheet.getColumns(); c++) {
					String h = sheet.getCell(c, 0).getContents().trim();
					String v = sheet.getCell(c, r).getContents().trim();
					rowValues.put(h, v);
				}
			}
		}
		
		if (!script.getScript().trim().equals("")) {
			String function = "def convert_row(row_dict):";
			String [] lines = script.getScript().split("\n");
			for (String l : lines) {
				function += "\n\t" + l;
			}
			function += "\n\treturn";
			Python.getInstance().execute(function, "convert_row", rowValues);
		}
		
		String toReturn = rowValues.get(header);
		
		return toReturn == null ? "":toReturn;
	}
	
	public List<String> getValues(String header) {
		List<String> values = new ArrayList<String>();
		
		for (int i = 1; i < sheet.getRows(); i++) {
			values.add(getValue(i, header));
		}
		
		return values;
	}
	
	public int getNumberRows() {
		return sheet.getRows() - 1;
	}
	
	public void setScript(ScriptDefinition script) {
		this.script = script;
	}
}
