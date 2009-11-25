package net.sf.regadb.ui.form.importTool.data;

import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;

public class DataProvider {
	private Sheet sheet;
	public DataProvider(Sheet sheet) {
		this.sheet = sheet;
	}
 	
	public List<String> getHeaders() {
		List<String> headers = new ArrayList<String>();
		for (int i = 0; i < sheet.getColumns(); i++) {
			headers.add(sheet.getCell(0, i).getContents().trim());
		}
		return headers;
	}
	
	public String getValue(int row, String header) {
		//TODO add support for the script
		for (int i = 0; i < sheet.getColumns(); i++) {
			if (sheet.getCell(0, i).getContents().trim().equals(header)) {
				for (int j = 0; j < sheet.getRows(); j++) {
					String value = sheet.getCell(j, i).getContents().trim();
					if (row == j)
						return value;
				}
			}
		}
		return null;
	}
	
	public int getNumberRows() {
		return sheet.getRows() - 1;
	}
}
