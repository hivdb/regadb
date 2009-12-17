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
import net.sf.regadb.ui.form.importTool.data.ScriptDefinition;
import net.sf.regadb.ui.form.importTool.data.ValidateRules;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.RegaDBSettings;

import com.thoughtworks.xstream.XStream;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;

public class ImportToolForm extends FormWidget {
	private FormTable formTable;
	
	private Label descriptionL;
	private TextField descriptionTF;
	private Label fileL;
	private FileUpload fileU;
	private Label scriptL;
	private WPushButton scriptB;
	
	private SimpleTable ruleTable;
	private List<ImportRule> rules = new ArrayList<ImportRule>();
	private WPushButton addRuleButton;
	
	private ScriptForm scriptForm;
	
	private ImportDefinition definition;
	private DataProvider dataProvider;

	public ImportToolForm(InteractionState interactionState, WString formName, ImportDefinition definition) {
		super(formName, interactionState);
		
		this.definition = definition;
		
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
			fileU.getFileUpload().uploaded().addListener(this, new Signal.Listener(){
				public void trigger() {
					Workbook book = null;
					try {
						book = Workbook.getWorkbook(new File(fileU.getFileUpload().getSpoolFileName()));
					} catch (BiffException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					dataProvider = new DataProvider(book.getSheet(0), getScriptDefinition());
				}
			});
			formTable.addLineToTable(fileL, fileU);
		}
		
		scriptL = new Label(tr("form.importTool.script"));
		scriptB = new WPushButton(tr("form.importTool.scriptButton"));
		scriptForm = new ScriptForm(ImportToolForm.this);
		scriptB.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				scriptForm.setDataProvider(dataProvider);
				scriptForm.show();
			}
		});
		formTable.addLineToTable(scriptL, scriptB);
		
		ruleTable = new SimpleTable(this);
		List<WString> headers = new ArrayList<WString>();
		headers.add(tr("form.importTool.rules.column"));
		headers.add(tr("form.importTool.rules.type"));
		headers.add(tr("form.importTool.rules.name"));
		headers.add(tr("form.importTool.rules.number"));
		headers.add(tr("form.importTool.rules.detail"));
		if (getInteractionState() == InteractionState.Editing || 
				getInteractionState() == InteractionState.Adding) {
			headers.add(tr("form.importTool.rules.delete"));
		}
		ruleTable.setHeaders(headers);
		
		if (getInteractionState() == InteractionState.Editing || 
				getInteractionState() == InteractionState.Adding) {
			addRuleButton = new WPushButton(tr("form.importTool.addRuleButton"), this);
			addRuleButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
				@Override
				public void trigger(WMouseEvent arg) {
					boolean canAdd = false;
					if (dataProvider != null) {
						addRule(new Rule());
						canAdd = true;
					}
					
					if (!canAdd) {
						UIUtils.showWarningMessageBox(ImportToolForm.this, tr("form.importTool.cannotAddRule"));
					}
				}
			});
		}
		
		fillData();
		
		WPushButton startImport = new WPushButton(tr("form.importTool.startImportButton"));
		getExtraButtons().add(startImport);
		startImport.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
			public void trigger(WMouseEvent arg) {
				ValidateRules validate = new ValidateRules();
				createDefinitionObject();
				WString error = validate.validateRules(ImportToolForm.this.definition.getRules());
				if (error != null) {
					UIUtils.showWarningMessageBox(ImportToolForm.this, error);
				}
			}
		});
		
		addControlButtons();
	}

	private void addRule(Rule r) {
		ImportRule rule = new ImportRule(dataProvider, ImportToolForm.this, ruleTable.getRowAt(ruleTable.getRowCount()), r);
		rules.add(rule);
	}
    
    private void fillData()
    {        
    	if (definition != null) {
    		descriptionTF.setText(definition.getDescription());
    		
    		for (Rule r : definition.getRules()) {
    			addRule(r);
    		}
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

	private void createDefinitionObject() {
		if (definition == null)
			definition = new ImportDefinition();
		
		definition.setDescription(descriptionTF.text());
		
		definition.getRules().clear();
		for (ImportRule ir : this.rules) {
			ir.saveRule();
			definition.getRules().add(ir.getRule());
		}
		
		definition.setScript(scriptForm.getScript());
	}
	
	@Override
	public void saveData() {
		createDefinitionObject();
		
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
	
	public DataProvider getDataProvider() {
		return dataProvider;
	}
	
	public ImportDefinition getDefinition() {
		return definition;
	}
	
	public ScriptDefinition getScriptDefinition() {
		return scriptForm.getScript();
	}
}