package net.sf.regadb.ui.form.importTool.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.regadb.util.script.Python;
import net.sf.regadb.util.xls.ExcelTable;

public class DataProvider {
	private ExcelTable sheet;
	private ScriptDefinition script;

	public DataProvider(ExcelTable sheet, ScriptDefinition script) {
		this.sheet = sheet;
		this.script = script;
	}
 	
	public List<String> getHeaders() {
		List<String> headers = new ArrayList<String>();
		
		for (int i = 0; i < sheet.columnCount(); i++) {
			headers.add(sheet.getCell(0, i).trim());
		}
		
		for (String newCol : script.getNewColumns()) {
			headers.add(newCol.trim());
		}
		
		return headers;
	}
	
	public Map<String, String> getRowValues(int row) {
		Map<String, String> rowValues = new HashMap<String, String>();
		
		for (int r = 1; r < sheet.rowCount(); r++) {
			if (row == r) {
				for (int c = 0; c < sheet.columnCount(); c++) {
					String h = sheet.getCell(0, c).trim();
					String v = sheet.getCell(r, c).trim();
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
		
		return rowValues;
	}
	
	public String getValue(int row, String header) {
		String toReturn = getRowValues(row).get(header);
		
		return toReturn == null ? "":toReturn;
	}
	
	public List<String> getValues(String header) {
		List<String> values = new ArrayList<String>();
		
		for (int i = 1; i < sheet.rowCount(); i++) {
			values.add(getValue(i, header));
		}
		
		return values;
	}
	
	public int getNumberRows() {
		return sheet.rowCount() - 1;
	}
	
	public void setScript(ScriptDefinition script) {
		this.script = script;
	}
}
