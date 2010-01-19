package net.sf.regadb.ui.datatable.importTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sf.regadb.db.Transaction;
import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.widgets.datatable.IDataTable;
import net.sf.regadb.ui.framework.widgets.datatable.IFilter;
import net.sf.regadb.util.settings.RegaDBSettings;

import com.thoughtworks.xstream.XStream;

import eu.webtoolkit.jwt.WString;

public class ImportDefinitionDataTable implements IDataTable<ImportDefinition> {
    private static WString [] colNames = {
        WString.tr("dataTable.importDefinition.colName.description")};
    private static int[] colWidths = {100};
    private static boolean [] sortable = {false};
    
	@Override
	public CharSequence[] getColNames() {
		return colNames;
	}

	@Override
	public int[] getColumnWidths() {
		return colWidths;
	}

	@Override
	public List<ImportDefinition> getDataBlock(Transaction t, int startIndex,
			int amountOfRows, int sortIndex, boolean isAscending) {
		File importDir = RegaDBSettings.getInstance().getInstituteConfig().getImportToolDir();
		List<File> files = Arrays.asList(importDir.listFiles());
		Collections.sort(files);
		
		int untill = startIndex + amountOfRows;
		if (files.size() < untill)
			untill = files.size();
		
		XStream xstream = new XStream();
		
		List<File> subSection = files.subList(startIndex, untill);
		List<ImportDefinition> definitions = new ArrayList<ImportDefinition>();
		for (File file : subSection) {
			ImportDefinition id = null;
			try {
				id = (ImportDefinition)xstream.fromXML(new FileReader(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			id.setXmlFile(file);
			id.setDescription(id.getXmlFile().getName().substring(0, id.getXmlFile().getName().indexOf(".")));
			definitions.add(id);
		}
		
		return definitions;
	}

	@Override
	public long getDataSetSize(Transaction t) {
		File importDir = RegaDBSettings.getInstance().getInstituteConfig().getImportToolDir();
		String [] files = importDir.list();
		return files.length;
	}

	@Override
	public String[] getFieldNames() {
		return null;
	}

	@Override
	public IFilter[] getFilters() {
		return null;
	}

	@Override
	public String[] getRowData(ImportDefinition type) {
		String [] cols = new String [1];
		cols[0] = type.getDescription();
		return cols;
	}

	@Override
	public String[] getRowTooltips(ImportDefinition type) {
		return null;
	}

	@Override
	public void init(Transaction t) {

	}

	@Override
	public void selectAction(ImportDefinition selectedItem) {
        RegaDBMain.getApp().getTree().getTreeContent().importToolSelected.setSelectedItem(selectedItem);
        RegaDBMain.getApp().getTree().getTreeContent().importToolSelected.expand();
        RegaDBMain.getApp().getTree().getTreeContent().importToolSelected.refreshAllChildren();
        RegaDBMain.getApp().getTree().getTreeContent().importToolSelectedView.selectNode();
	}

	@Override
	public boolean[] sortableFields() {
		return sortable;
	}
}
