package net.sf.regadb.ui.form.importTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.ui.form.importTool.data.DataProvider;
import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.form.importTool.data.Rule;
import net.sf.regadb.ui.form.importTool.data.ScriptDefinition;
import net.sf.regadb.ui.form.importTool.data.ValidateRules;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.ObjectForm;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.SimpleTable;
import net.sf.regadb.ui.framework.widgets.UIUtils;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.ui.tree.ObjectTreeNode;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.xls.ExcelTable;

import com.thoughtworks.xstream.XStream;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;
import eu.webtoolkit.jwt.WString;

public class ImportToolForm extends ObjectForm<ImportDefinition>{
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
	
	private DataProvider dataProvider;
	
	private StartImportForm startImportForm;

	public ImportToolForm(WString formName, InteractionState interactionState, ObjectTreeNode<ImportDefinition> node, ImportDefinition definition) {
		super(formName, interactionState, node, definition);
		
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
					ExcelTable table = new ExcelTable("dd/MM/yyyy");
					try {
						table.loadFile(new File(fileU.getFileUpload().getSpoolFileName()));
					} catch (IOException e) {
						e.printStackTrace();
					}
					dataProvider = new DataProvider(table, getScriptDefinition());
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
		
		startImportForm = new StartImportForm(this);
		
		if (getInteractionState() == InteractionState.Editing || 
				getInteractionState() == InteractionState.Adding) {
			addRuleButton = new WPushButton(tr("form.importTool.addRuleButton"), this);
			addRuleButton.clicked().addListener(this, new Signal1.Listener<WMouseEvent>(){
				
				public void trigger(WMouseEvent arg) {
					boolean canAdd = false;
					if (dataProvider != null) {
						addRule(new Rule());
						canAdd = true;
					}
					
					if (!canAdd) 
						UIUtils.showWarningMessageBox(ImportToolForm.this, tr("form.importTool.cannotAddRule"));
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
				WString error = validate.validateRules(ImportToolForm.this.getObject().getRules());
				if (error != null) {
					UIUtils.showWarningMessageBox(ImportToolForm.this, error);
				} else {
					startImportForm.show();
				}
			}
		});
		startImport.setDisabled(getInteractionState() == InteractionState.Deleting);
		
		addControlButtons();
	}

	private void addRule(Rule r) {
		ImportRule rule = new ImportRule(dataProvider, ImportToolForm.this, ruleTable.getRowAt(ruleTable.getRowCount()), r);
		rules.add(rule);
	}
    
    private void fillData()
    {        
    	if (getObject() != null) {
    		descriptionTF.setText(getObject().getDescription());
    		
    		for (Rule r : getObject().getRules()) {
    			addRule(r);
    		}
    	}
    }
	
	@Override
	public void cancel() {
	}

	@Override
	public WString deleteObject() {
		File f = new File(RegaDBSettings.getInstance().getInstituteConfig().getImportToolDir().getAbsolutePath()
					+ File.separatorChar + descriptionTF.text() + ".xml");
		
		if (f.exists())
			f.delete();
		
		return null;
	}

	private void createDefinitionObject() {
		if (getObject() == null)
			setObject(new ImportDefinition());
		
		getObject().setDescription(descriptionTF.text());
		
		getObject().getRules().clear();
		for (ImportRule ir : this.rules) {
			ir.saveRule();
			getObject().getRules().add(ir.getRule());
		}
		
		getObject().setScript(scriptForm.getScript());
	}
	
	@Override
	public void saveData() {
		createDefinitionObject();
		
		XStream xstream = new XStream();
		File f = 
			new File(RegaDBSettings.getInstance().getInstituteConfig().getImportToolDir().getAbsolutePath()
					+ File.separatorChar + descriptionTF.text() + ".xml");
		
		if (getInteractionState() == InteractionState.Adding && f.exists()) {
			UIUtils.showWarningMessageBox(this, tr("form.importTool.fileAlreadyExists").arg(descriptionTF.text()));
			return;
		}

		try {
			FileWriter fw = new FileWriter(f);
			xstream.toXML(getObject(), fw);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<ImportRule> getRules() {
		return rules;
	}
	
	public DataProvider getDataProvider() {
		return dataProvider;
	}
	
	public ScriptDefinition getScriptDefinition() {
		return scriptForm.getScript();
	}
}