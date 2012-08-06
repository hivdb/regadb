package net.sf.regadb.ui.form.administrator;

import java.util.Map.Entry;

import net.sf.regadb.ui.framework.forms.FormWidget;
import net.sf.regadb.ui.framework.forms.InteractionState;
import net.sf.regadb.util.settings.RegaDBSettings;
import net.sf.regadb.util.version.Version;
import eu.webtoolkit.jwt.TextFormat;
import eu.webtoolkit.jwt.WGroupBox;
import eu.webtoolkit.jwt.WLabel;
import eu.webtoolkit.jwt.WString;
import eu.webtoolkit.jwt.WTable;
import eu.webtoolkit.jwt.WText;

public class AboutForm extends FormWidget {

	public AboutForm() {
		super(tr("form.about"), InteractionState.Viewing);
		init();
	}
	
	private void init(){
		WGroupBox versionGroup = new WGroupBox(tr("form.about.version"), this);
		WTable t = new WTable(versionGroup);
		
		int i = 0;
		if(Version.getProperties() != null){
			for(Entry<Object, Object> e : Version.getProperties().entrySet()){
				t.insertRow(i);
				t.getElementAt(i, 0).addWidget(new WText(tr("form.about.version."+ (String)e.getKey())));
				t.getElementAt(i, 1).addWidget(new WText(e.getValue().toString(),TextFormat.PlainText));
				++i;
			}
		} else {
			t.getElementAt(0,0).addWidget(new WText(tr("form.about.version.notAvailable")));
		}
		
		WGroupBox configGroup = new WGroupBox(tr("form.about.configuration"), this);
		t = new WTable(configGroup);
		i = 0;
		
		t.getElementAt(i,0).addWidget(new WLabel(tr("form.about.configuration.configFile")));
		t.getElementAt(i++,1).addWidget(new WText(RegaDBSettings.getInstance().getConfigFile().getAbsolutePath()));
		
		t.getElementAt(i,0).addWidget(new WLabel(tr("form.about.configuration.timezone")));
		t.getElementAt(i++,1).addWidget(new WText(System.getProperty("user.timezone")));
		
		long bytesPerMegaByte = 1024*1024;
		Runtime rt = Runtime.getRuntime();
		t.getElementAt(i,0).addWidget(new WLabel(tr("form.about.configuration.maxMemory")));
		t.getElementAt(i++,1).addWidget(new WText(Math.round(rt.maxMemory() / bytesPerMegaByte) +"MB"));
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