package net.sf.regadb.ui.form.importTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.regadb.ui.form.importTool.data.MappingDetails;
import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.form.singlePatient.DataComboMessage;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.ComboBox;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTableRow;
import eu.webtoolkit.jwt.WWidget;

public abstract class MappingsDetailsTable extends WContainerWidget {
	private SimpleTable mappingTable;
	private List<TextField> originalValues = new ArrayList<TextField>();
	private List<ComboBox<String>> regadbValues = new ArrayList<ComboBox<String>>();
	
	private ImportRule rule;

	private List<String> databaseValues;

	public MappingsDetailsTable(List<String> originalValues, List<String> databaseValues, ImportRule rule, MappingDetails localDetails) {
		this.rule = rule;
		this.databaseValues = databaseValues;
		
		mappingTable = new SimpleTable(this);
		ArrayList<CharSequence> headers = new ArrayList<CharSequence>();
		headers.add(tr("form.importTool.details.mappings.originalValue"));
		headers.add(tr("form.importTool.details.mappings.regadbValue"));
		if (rule.getForm().isEditable())
			headers.add(tr("form.importTool.details.mappings.delete"));
		mappingTable.setHeaders(headers);
		
		if (localDetails != null) {
			for (Map.Entry<String, String> e: localDetails.getMappings().entrySet()) {
				getMappingDetails().getMappings().put(e.getKey(), e.getValue());
			}
		} else if (originalValues != null) {
			for (String o : originalValues) {
				if (!o.trim().equals("")) {
					String mapping = autoMap(o, databaseValues);
					getMappingDetails().getMappings().put(o, mapping);
				}
			}
		}
		
		if (rule.getForm().isEditable()) {
			WPushButton add = new WPushButton(tr("form.importTool.details.mappings.addButton"), this);
			add.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
				public void trigger(WMouseEvent arg) {
					addRow(MappingsDetailsTable.this.rule.getForm().getInteractionState(), "", "");
				}
			});
		}
	}
	
	public void init() {
		mappingTable.clearData();
		originalValues.clear();
		regadbValues.clear();
		
		for (Map.Entry<String, String> e : getMappingDetails().getMappings().entrySet()) {
			addRow(rule.getForm().getInteractionState(), e.getKey(), e.getValue());
		}
	}
	
	public void save() {
		getMappingDetails().getMappings().clear();
		for (int i = 0; i < originalValues.size(); i++) {
			getMappingDetails().getMappings().put(originalValues.get(i).text().trim(), 
					regadbValues.get(i).currentValue());
		}
	}
	
	public void save(Rule rule) {
		rule.setMappingDetails(getMappingDetails());
	}
	
	private void addRow(InteractionState is, String originalValue, String regadbValue) {
		List<WWidget> widgets = new ArrayList<WWidget>();
		final TextField original = new TextField(is, null, FieldType.ALFANUMERIC);
		original.setText(originalValue);
		widgets.add(original);
		originalValues.add(original);
		final ComboBox<String> regadb = new ComboBox<String>(is, null);
		regadb.addNoSelectionItem();
		for (String d : databaseValues) {
			regadb.addItem(new DataComboMessage<String>(d, d));
		}
		regadb.selectItem(regadbValue);
		widgets.add(regadb);
		regadbValues.add(regadb);
		WPushButton delete = new WPushButton(tr("form.importTool.details.mappings.deleteButton"));
		if (is == InteractionState.Adding || is == InteractionState.Editing) {
			widgets.add(delete);
		}
		final WTableRow row = mappingTable.addRow(widgets);
		delete.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				mappingTable.deleteRow(row.getRowNum());
				originalValues.remove(original);
				regadbValues.remove(regadb);
			}
		});
	}
	
	public WString validate() {
		for (TextField original : originalValues) {
			if (original.text().trim().equals("")) 
				return tr("form.importTool.details.mappings.emptyOriginalValues");
		}
		
		if (originalValues.size() == 0)
			return tr("form.importTool.details.mappings.noMappingsDefined");
		
		return null;
	}
	
	public ImportRule getRule() {
		return rule;
	}
	
	public String autoMap(String original, List<String> database) {
		for (String d : database) {
			if (d.trim().toLowerCase().contains(original.toLowerCase()))
				return d.trim();
		}
		
		return "";
	}
	
	public abstract MappingDetails getMappingDetails();
}
