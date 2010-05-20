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
		for(Entry<Object, Object> e : Version.getProperties().entrySet()){
			t.insertRow(i);
			t.getElementAt(i, 0).addWidget(new WText(tr("form.version."+ (String)e.getKey())));
			t.getElementAt(i, 1).addWidget(new WText(e.getValue().toString(),TextFormat.PlainText));
			++i;
		}
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

}
