package net.sf.regadb.ui.form.importTool;

import java.util.List;

import net.sf.regadb.ui.form.importTool.data.MappingDetails;
import net.sf.regadb.ui.form.importTool.data.Rule;
import eu.webtoolkit.jwt.WString;

public class MappingDetailsForm extends DetailsForm {
	private MappingsDetailsTable table;
	private MappingDetails details = new MappingDetails();
	
	public MappingDetailsForm(List<String> originalValues, List<String> databaseValues, WString title, final ImportRule rule) {
		super(title);
		
		table = new MappingsDetailsTable(originalValues, databaseValues, rule, rule.getRule().getMappingDetails()) {
			public MappingDetails getMappingDetails() {
				return details;
			}
		};
		this.addWidget(table);
		table.init();
	}
	
	public void init() {
		table.init();
	}
	
	public void save() {
		table.save();
	}
	
	public void save(Rule rule) {
		table.save(rule);
	}

	public WString validate() {
		return table.validate();
	}
}
