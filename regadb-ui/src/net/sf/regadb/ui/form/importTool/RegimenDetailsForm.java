package net.sf.regadb.ui.form.importTool;

import java.util.List;
import java.util.StringTokenizer;

import net.sf.regadb.ui.form.importTool.data.MappingDetails;
import net.sf.regadb.ui.form.importTool.data.RegimenDetails;
import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;

public class RegimenDetailsForm extends DetailsForm {
	private Label delimiterL;
	private TextField delimiterTF;
	private MappingsDetailsTable table;
	
	private RegimenDetails details = new RegimenDetails();
	
	private List<String> databaseValues;
	
	public RegimenDetailsForm(List<String> originalValues, List<String> databaseValues, final ImportRule rule) {
		super(WString.tr("form.importTool.details.regimen.title"));
		this.databaseValues = databaseValues;
		
		delimiterL = new Label(tr("form.importTool.details.regimen.delimiter"));
		delimiterTF = new TextField(rule.getForm().getInteractionState(), null, FieldType.ALFANUMERIC);
		WPushButton updateTable = new WPushButton(tr("form.importTool.details.regimen.updateTable"));
		updateTable.clicked().addListener(updateTable, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				updateTable();
			}
		});
		
		table = new MappingsDetailsTable(null, databaseValues, rule, rule.getRule().getRegimenDetails()){
			public MappingDetails getMappingDetails() {
				return details;
			}
		};
		
		WTable layout = new WTable(this);
		layout.getElementAt(0, 0).addWidget(delimiterL);
		layout.getElementAt(0, 1).addWidget(delimiterTF);
		layout.getElementAt(0, 2).addWidget(updateTable);
		layout.getElementAt(1, 0).addWidget(table);
		layout.getElementAt(1, 0).setColumnSpan(3);
	}
	
	private void updateTable() {
		List<String> columnData = table.getRule().getCurrentColumnData();
		for (String drugs : columnData) {
			StringTokenizer tokenizer = new StringTokenizer(drugs.trim(), delimiterTF.text().trim());
			while (tokenizer.hasMoreTokens()) {
				String drug = tokenizer.nextToken();
				if (details.getMappings().get(drug) == null) 
					details.getMappings().put(drug, table.autoMap(drug, databaseValues));
			}
		}
		
		init();
	}

	public void init() {
		table.init();
	}

	public void save(Rule rule) {		
		rule.setRegimenDetails(details);
	}

	public void save() {
		table.save();
	}

	public WString validate() {
		return null;
	}
}
