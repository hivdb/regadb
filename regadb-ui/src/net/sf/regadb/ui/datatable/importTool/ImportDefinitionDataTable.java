package net.sf.regadb.ui.datatable.importTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.framework.forms.SelectForm;
import net.sf.regadb.ui.framework.widgets.datatable.DefaultDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.util.settings.RegaDBSettings;
import eu.webtoolkit.jwt.WString;

public class ImportDefinitionDataTable extends DefaultDataTable<ImportDefinition> {
    public ImportDefinitionDataTable(SelectForm<ImportDefinition> form) {
		super(form);
	}

	private static WString [] colNames = {
        WString.tr("dataTable.importDefinition.colName.description")};
    private static int[] colWidths = {100};
    private static boolean [] sortable = {false};
    
	public CharSequence[] getColNames() {
		return colNames;
	}

	public int[] getColumnWidths() {
		return colWidths;
	}

	public List<ImportDefinition> getDataBlock(Transaction t, int startIndex,
			int amountOfRows, int sortIndex, boolean isAscending) {
		File importDir = RegaDBSettings.getInstance().getInstituteConfig().getImportToolDir();
		List<File> files = Arrays.asList(importDir.listFiles());
		Collections.sort(files);
		
		int untill = startIndex + amountOfRows;
		if (files.size() < untill)
			untill = files.size();
		
		List<File> subSection = files.subList(startIndex, untill);
		List<ImportDefinition> definitions = new ArrayList<ImportDefinition>();
		for (File file : subSection) {
			definitions.add(ImportDefinition.getImportDefinition(file));
		}
		
		return definitions;
	}

	public long getDataSetSize(Transaction t) {
		File importDir = RegaDBSettings.getInstance().getInstituteConfig().getImportToolDir();
		String [] files = importDir.list();
		return files.length;
	}

	public String[] getFieldNames() {
		return null;
	}

	public IFilter[] getFilters() {
		return null;
	}

	public String[] getRowData(ImportDefinition type) {
		String [] cols = new String [1];
		cols[0] = type.getDescription();
		return cols;
	}

	public String[] getRowTooltips(ImportDefinition type) {
		return null;
	}

	public void init(Transaction t) {

	}

	public boolean[] sortableFields() {
		return sortable;
	}
}
