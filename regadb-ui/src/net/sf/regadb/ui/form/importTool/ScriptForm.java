package net.sf.regadb.ui.form.importTool;

import java.util.Arrays;
import java.util.List;

import net.sf.regadb.ui.form.importTool.data.DataProvider;
import net.sf.regadb.ui.form.importTool.data.ScriptDefinition;
import net.sf.regadb.ui.framework.forms.fields.Label;
import net.sf.regadb.ui.framework.forms.fields.TextArea;
import net.sf.regadb.ui.framework.forms.fields.TextField;
import eu.webtoolkit.jwt.JSlot;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WDialog;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WPushButton;

public class ScriptForm extends WDialog {
	private Label newColumnsL;
	private TextField newColumnsTF;
	private Label scriptL;
	private TextArea scriptTE;
	
	private ScriptDefinition script;
	private DataProvider dataProvider;

	public ScriptForm(ImportToolForm form) {
		super(tr("form.importTool.details.script.title"));
		
		if (form.getDefinition() == null || form.getDefinition().getScript() == null) {
			this.script = new ScriptDefinition();
		} else {
			this.script = form.getDefinition().getScript();
		}
		
		newColumnsL = new Label(tr("form.importTool.details.script.newColumns"));
		getContents().addWidget(newColumnsL);
		newColumnsTF = new TextField(form.getInteractionState(), null);
		newColumnsTF.setTextSize(50);
		newColumnsTF.setText(getNewColumnsString(this.script.getNewColumns()));
		getContents().addWidget(newColumnsTF);
		scriptL = new Label(tr("form.importTool.details.script.script"));
		getContents().addWidget(scriptL);
		scriptTE = new TextArea(form.getInteractionState(), null);
		if (scriptTE.getFormWidget() != null) {
			JSlot blockFocus = new JSlot();
			blockFocus.setJavaScript(
					"function(obj, event){" + 
					"	if(event.keyCode == 9){" + 
					"		var WT = " + WEnvironment.getJavaScriptWtScope() + ";" + 
					"		var range = WT.getSelectionRange(obj);" + 
					"		obj.value = obj.value.substring(0, range.selectionStart) " +
					"					+  \"\t\" " +
					"					+ obj.value.substring(range.selectionEnd, obj.value.length);" + 
					"		WT.cancelEvent(event, WT.CancelDefaultAction);" +  
					"		WT.setSelectionRange(obj, range.selectionStart + 1, range.selectionEnd + 1);" +
					"	}" + 
					"}"
			);
			scriptTE.getFormWidget().keyWentDown().addListener(blockFocus);
		}
		scriptTE.setSize(50, 20);
		scriptTE.setText(script.getScript());
		getContents().addWidget(scriptTE);
		
        WPushButton ok = new WPushButton(tr("form.importTool.details.script.box.ok"), getContents());
        ok.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent arg) {
				hide();
				ScriptForm.this.script.setNewColumns(Arrays.asList(newColumnsTF.text().split(",")));
				ScriptForm.this.script.setScript(scriptTE.text());
				if (dataProvider != null) {
					dataProvider.setScript(getScript());
				}
			}
		});
        WPushButton cancel = new WPushButton(tr("form.importTool.details.script.box.cancel"), getContents());
        cancel.clicked().addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent arg) {
				newColumnsTF.setText(getNewColumnsString(ScriptForm.this.script.getNewColumns()));
				scriptTE.setText(ScriptForm.this.script.getScript());
				hide();
			}
		});
	}
	
	private String getNewColumnsString(List<String> newColumns) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < newColumns.size(); i++) {
			sb.append(newColumns.get(i));
			if (i != newColumns.size() - 1) 
				sb.append(",");
		}
		return sb.toString();
	}
	
	public ScriptDefinition getScript() {
		return script;
	}

	public void setDataProvider(DataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}
}