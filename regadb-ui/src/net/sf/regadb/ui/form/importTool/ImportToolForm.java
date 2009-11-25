package net.sf.regadb.ui.form.importTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.read.biff.BiffException;
import net.sf.regadb.ui.form.importTool.data.DataProvider;
import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.framework.widgets.table.TableHeader;
import net.sf.regadb.util.settings.RegaDBSettings;

import com.thoughtworks.xstream.XStream;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;

public class ImportToolForm extends FormWidget {
	private FormTable formTable;
	
	private Label descriptionL;
	private TextField descriptionTF;
	private Label fileL;
	private FileUpload fileU;
	
	private ImportDefinition definition;
	
	private WTable ruleTable;
	private List<ImportRule> rules = new ArrayList<ImportRule>();
	private WPushButton addRuleButton;

	public ImportToolForm(InteractionState interactionState, WString formName, ImportDefinition definition) {
		super(formName, interactionState);
		
		formTable = new FormTable(this);

		descriptionL = new Label(tr("form.importTool.description"));
		descriptionTF = new TextField(this.getInteractionState()==InteractionState.Adding?InteractionState.Adding:InteractionState.Viewing, this, FieldType.ALFANUMERIC);
		descriptionTF.setMandatory(true);
		formTable.addLineToTable(descriptionL, descriptionTF);
		
		if (getInteractionState() == InteractionState.Editing || 
				getInteractionState() == InteractionState.Adding) {
			fileL = new Label(tr("form.importTool.excelFile"));
			fileU = new FileUpload(getInteractionState(), this);
			fileU.setMandatory(true);
			formTable.addLineToTable(fileL, fileU);
		}
		
		this.definition = definition;
		
		ruleTable = new WTable(this);
		addHeader("form.importTool.rules.column");
		addHeader("form.importTool.rules.type");
		addHeader("form.importTool.rules.number");
		addHeader("form.importTool.rules.name");
		addHeader("form.importTool.rules.detail");
		addHeader("form.importTool.rules.delete");
		
		addRuleButton = new WPushButton(tr("form.importTool.addRuleButton"), this);
		addRuleButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			@Override
			public void trigger(WMouseEvent arg) {
				boolean canAdd = false;
				if (!fileU.getFileUpload().getSpoolFileName().equals("")) {
					Workbook book = null;
					try {
						book = Workbook.getWorkbook(new File(fileU.getFileUpload().getSpoolFileName()));
					} catch (BiffException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (book != null) {
						DataProvider dataProvider = new DataProvider(book.getSheet(0));
						ImportRule rule = new ImportRule(dataProvider, ImportToolForm.this, ruleTable.getRowAt(ruleTable.getRowCount()), new Rule());
						rules.add(rule);
						canAdd = true;
					}
				}
				
				if (!canAdd) {
					//TODO give error message
				}
			}
		});
		
		fillData();
		
		addControlButtons();
	}
	
	private void addHeader(String header) {
		ruleTable.getElementAt(0, ruleTable.getColumnCount()).addWidget(new TableHeader(tr(header)));
	}
    
    private void fillData()
    {        
    	if (definition != null) {
    		descriptionTF.setText(definition.getDescription());
    	}
    }
	
	@Override
	public void cancel() {
        if(getInteractionState()==InteractionState.Adding)
            redirectToSelect(RegaDBMain.getApp().getTree().getTreeContent().importTool, RegaDBMain.getApp().getTree().getTreeContent().importToolSelect);
        else
            redirectToView(RegaDBMain.getApp().getTree().getTreeContent().importToolSelected, RegaDBMain.getApp().getTree().getTreeContent().importToolSelectedView);
	}

	@Override
	public WString deleteObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void redirectAfterDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveData() {
		if (definition == null)
			definition = new ImportDefinition();
		
		definition.setDescription(descriptionTF.text());
		
		XStream xstream = new XStream();
		File f = 
			new File(RegaDBSettings.getInstance().getInstituteConfig().getImportToolDir().getAbsolutePath()
					+ File.separatorChar + descriptionTF.text() + ".xml");

		try {
			FileWriter fw = new FileWriter(f);
			xstream.toXML(definition, fw);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RegaDBMain.getApp().getTree().getTreeContent().importToolSelected.setSelectedItem(definition);
		redirectToView(RegaDBMain.getApp().getTree().getTreeContent().importToolSelected, RegaDBMain.getApp().getTree().getTreeContent().importToolSelectedView);
	}
	
	public List<ImportRule> getRules() {
		return rules;
	}
}
