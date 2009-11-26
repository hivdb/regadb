package net.sf.regadb.ui.form.importTool;

import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.ui.form.importTool.data.DateDetails;
import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WCheckBox;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTableRow;
import eu.webtoolkit.jwt.WWidget;

public class DateDetailsForm extends DetailsForm {
	private SimpleTable mappingTable;
	
	private ImportRule rule;
	private DateDetails details = new DateDetails();
	
	private List<TextField> dateFormats = new ArrayList<TextField>();
	private List<WCheckBox> nullDates = new ArrayList<WCheckBox>();
	
	public DateDetailsForm(ImportRule rule, DateDetails localDetails) {
		super(tr("form.importTool.details.date.title"));
		this.rule = rule;
		
		mappingTable = new SimpleTable(this);
		ArrayList<CharSequence> headers = new ArrayList<CharSequence>();
		headers.add(tr("form.importTool.details.date.format"));
		headers.add(tr("form.importTool.details.date.nullDate"));
		if (rule.getForm().isEditable())
			headers.add(tr("form.importTool.details.date.delete"));
		mappingTable.setHeaders(headers);
		
		if (localDetails != null)
		for (String dateFormat : localDetails.getDateFormats()) {
			details.getDateFormats().add(dateFormat);
			if (localDetails.getNullDateFormats().contains(dateFormat))
				details.getNullDateFormats().add(dateFormat);
		}
		
		if (rule.getForm().isEditable()) {
			WPushButton add = new WPushButton(tr("form.importTool.details.date.addButton"), this);
			add.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
				public void trigger(WMouseEvent arg) {
					addRow("", false);
				}
			});
		}
	}

	@Override
	public void init() {
		mappingTable.clear();
		dateFormats.clear();
		nullDates.clear();
		for (String dateFormat : details.getDateFormats()) {
			boolean nullDate = details.getNullDateFormats().contains(dateFormat);
			addRow(dateFormat, nullDate);
		}
	}
	
	private void addRow(String dateFormat, boolean nullDate) {
		List<WWidget> widgets = new ArrayList<WWidget>();
		final TextField dateFormatTF = new TextField(rule.getForm().getInteractionState(), null);
		dateFormatTF.setText(dateFormat);
		dateFormats.add(dateFormatTF);
		widgets.add(dateFormatTF);
		final WCheckBox nullDateCB = new WCheckBox();
		nullDateCB.setEnabled(rule.getForm().isEditable());
		nullDateCB.setChecked(nullDate);
		nullDates.add(nullDateCB);
		widgets.add(nullDateCB);
		WPushButton delete = new WPushButton(tr("form.importTool.details.date.deleteButton"));
		if (rule.getForm().isEditable()) {
			widgets.add(delete);
		}
		final WTableRow row = mappingTable.addRow(widgets);
		delete.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				mappingTable.deleteRow(row.getRowNum());
				dateFormats.remove(dateFormatTF);
				nullDates.remove(nullDateCB);
			}
		});
	}

	@Override
	public void save(Rule rule) {
		rule.setDateDetails(details);
	}

	@Override
	public void save() {
		details.getDateFormats().clear();
		details.getNullDateFormats().clear();
		
		for (int i = 0; i < dateFormats.size(); i++) {
			String format = dateFormats.get(i).text().trim();
			details.getDateFormats().add(format);
			if (nullDates.get(i).isChecked())
				details.getNullDateFormats().add(format);
		}
	}

	@Override
	public WString validate() {
		return null;
	}
}
