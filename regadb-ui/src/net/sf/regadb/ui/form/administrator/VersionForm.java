package net.sf.regadb.ui.form.administrator;

import java.util.Map.Entry;

import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.version.Version;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class VersionForm extends FormWidget {

	public VersionForm() {
		super(tr("form.version"), InteractionState.Viewing);
		init();
	}
	
	private void init(){
		WTable t = new WTable(this);
		
		int i = 0;
		if(Version.getProperties() != null){
			for(Entry<Object, Object> e : Version.getProperties().entrySet()){
				t.insertRow(i);
				t.getElementAt(i, 0).addWidget(new WText(tr("form.version."+ (String)e.getKey())));
				t.getElementAt(i, 1).addWidget(new WText(e.getValue().toString(),TextFormat.PlainText));
				++i;
			}
		} else {
			t.getElementAt(0,0).addWidget(new WText(tr("form.version.notAvailable")));
		}
	}

	@Override
	public void cancel() {
	}

	@Override
	public WString deleteObject() {
		return null;
	}

	@Override
	public void redirectAfterDelete() {
	}

	@Override
	public void saveData() {
	}

	@Override
	public void redirectAfterSave() {
	}

	@Override
	public void redirectAfterCancel() {
	}

}
