package net.sf.regadb.ui.form.importTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.sf.regadb.ui.form.importTool.data.ImportDefinition;
import net.sf.regadb.ui.framework.RegaDBMain;
import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.ui.framework.forms.fields.FieldType;
import net.sf.regadb.ui.framework.forms.fields.FileUpload;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import net.sf.regadb.ui.framework.widgets.formtable.FormTable;
import net.sf.regadb.util.settings.RegaDBSettings;

import com.thoughtworks.xstream.XStream;

import eu.webtoolkit.jwt.WString;

public class ImportToolForm extends FormWidget {
	private FormTable formTable;
	
	private Label descriptionL;
	private TextField descriptionTF;
	private Label fileL;
	private FileUpload fileU;
	
	private ImportDefinition definition;

	public ImportToolForm(InteractionState interactionState, WString formName, ImportDefinition definition) {
		super(formName, interactionState);
		
		formTable = new FormTable(this);

		descriptionL = new Label(tr("form.importTool.description"));
		descriptionTF = new TextField(this.getInteractionState()==InteractionState.Adding?InteractionState.Adding:InteractionState.Viewing, this, FieldType.ALFANUMERIC);
		descriptionTF.setMandatory(true);
		formTable.addLineToTable(descriptionL, descriptionTF);
		
		if (getInteractionState() == InteractionState.Adding) {
			fileL = new Label(tr("form.importTool.excelFile"));
			fileU = new FileUpload(getInteractionState(), this);
			fileU.setMandatory(true);
			formTable.addLineToTable(fileL, fileU);
		}
		
		this.definition = definition;
		
		fillData();
		
		addControlButtons();
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
}
