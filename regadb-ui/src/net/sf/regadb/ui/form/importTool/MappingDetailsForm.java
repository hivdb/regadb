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
	
	private ImportRule rule;
	
	private MappingDetails details = new MappingDetails();
	
	public MappingDetailsForm(Map<String, String> values, WString title, final ImportRule rule) {
		super(title);
		this.rule = rule;
		
		mappingTable = new SimpleTable(this);
		ArrayList<CharSequence> headers = new ArrayList<CharSequence>();
		headers.add(tr("form.importTool.details.mappings.originalValue"));
		headers.add(tr("form.importTool.details.mappings.regadbValue"));
		if (rule.getForm().isEditable())
			headers.add(tr("form.importTool.details.mappings.delete"));
		mappingTable.setHeaders(headers);
		
		MappingDetails localDetails = rule.getRule().getMappingDetails();
		if (localDetails != null) {
			for (Map.Entry<String, String> e: localDetails.getMappings().entrySet()) {
				details.getMappings().put(e.getKey(), e.getValue());
			}
		} else {
			for (Map.Entry<String, String> e : values.entrySet()) {
				details.getMappings().put(e.getKey(), e.getValue());
			}
		}
		
		if (rule.getForm().isEditable()) {
			WPushButton add = new WPushButton(tr("form.importTool.details.mappings.addButton"), this);
			add.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
				public void trigger(WMouseEvent arg) {
					addRow(rule.getForm().getInteractionState(), "", "");
				}
			});
		}
		
		init();
	}
	
	public void init() {
		mappingTable.clear();
		originalValues.clear();
		regadbValues.clear();
		
		for (Map.Entry<String, String> e : details.getMappings().entrySet()) {
			addRow(rule.getForm().getInteractionState(), e.getKey(), e.getValue());
		}
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
	
	public void save() {
		details.getMappings().clear();
		for (int i = 0; i < originalValues.size(); i++) {
			details.getMappings().put(originalValues.get(i).text().trim(), 
					regadbValues.get(i).text().trim());
		}
	}
	
	public void save(Rule rule) {
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
