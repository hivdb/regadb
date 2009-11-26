package net.sf.regadb.ui.form.importTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.regadb.ui.form.importTool.data.MappingDetails;
import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTableRow;
import eu.webtoolkit.jwt.WWidget;

public class MappingDetailsForm extends DetailsForm {
	private SimpleTable mappingTable;
	private List<TextField> originalValues = new ArrayList<TextField>();
	private List<TextField> regadbValues = new ArrayList<TextField>();
	
	public MappingDetailsForm(Map<String, String> values, WString title, final ImportRule rule) {
		super(title);
		
		mappingTable = new SimpleTable(this);
		ArrayList<CharSequence> headers = new ArrayList<CharSequence>();
		headers.add(tr("form.importTool.details.mappings.originalValue"));
		headers.add(tr("form.importTool.details.mappings.regadbValue"));
		if (rule.getForm().isEditable())
			headers.add(tr("form.importTool.details.mappings.delete"));
		mappingTable.setHeaders(headers);
		
		MappingDetails details = rule.getRule().getMappingDetails();
		if (details != null) {
			for (Map.Entry<String, String> e: details.getMappings().entrySet()) {
				addRow(rule.getForm().getInteractionState(), e.getKey(), e.getValue());
			}
		} else {
			for (Map.Entry<String, String> e : values.entrySet()) {
				addRow(rule.getForm().getInteractionState(), e.getKey(), e.getValue());
			}
		}
		
		WPushButton add = new WPushButton(tr("form.importTool.details.mappings.addButton"));
		add.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				addRow(rule.getForm().getInteractionState(), "", "");
			}
		});
	}
	
	private void addRow(InteractionState is, String originalValue, String regadbValue) {
		List<WWidget> widgets = new ArrayList<WWidget>();
		final TextField original = new TextField(is, null, FieldType.ALFANUMERIC);
		original.setText(originalValue);
		widgets.add(original);
		originalValues.add(original);
		final TextField regadb = new TextField(is, null, FieldType.ALFANUMERIC);
		regadb.setText(regadbValue);
		widgets.add(regadb);
		regadbValues.add(regadb);
		WPushButton delete = new WPushButton("form.importTool.details.mappings.deleteButton");
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

	public void save(Rule rule) {
		MappingDetails details = new MappingDetails();
		for (int i = 0; i < originalValues.size(); i++) {
			details.getMappings().put(originalValues.get(i).text().trim(), 
					regadbValues.get(i).text().trim());
		}
		rule.setMappingDetails(details);
	}

	public WString validate() {
		for (TextField original : originalValues) {
			if (original.text().trim().equals("")) 
				return tr("form.importTool.details.mappings.emptyOriginalValues");
		}
		
		for (TextField regadb : regadbValues) {
			if (regadb.text().trim().equals(""))
				return tr("form.importTool.details.mappings.emptyRegaDBValues");
		}
		
		if (originalValues.size() == 0)
			return tr("form.importTool.details.mappings.noMappingsDefined");
		
		return null;
	}
}
