package net.sf.regadb.ui.form.query.querytool;

import java.io.IOException;

import net.sf.regadb.db.QueryDefinition;

import com.pharmadm.custom.rega.queryeditor.Query;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.savable.DirtinessListener;
import com.pharmadm.custom.rega.savable.Savable;
import com.thoughtworks.xstream.XStream;

public class QueryLoader implements Savable {

	private QueryToolApp mainForm;
	private InfoContainer infoContainer;
	private QueryEditorComponent comp;
	
	// is query loaded
	private boolean queryLoaded;
	
	public QueryLoader(QueryToolApp form, InfoContainer infoContainer, QueryEditorComponent editor) {
		this.infoContainer = infoContainer;
		mainForm = form;
		this.comp = editor;
	}
	
	public void addDirtinessListener(DirtinessListener listener) {
		mainForm.getEditorModel().getQueryEditor().addDirtinessListener(listener);

	}

	public boolean isDirty() {
		return mainForm.getEditorModel().getQueryEditor().isDirty();
	}

	public void load(Object file) throws IOException {
		comp.setQuery(loadQuery((QueryDefinition) file));

	}

	public void save(Object file) throws IOException {
		saveData((QueryDefinition) file);

	}
	
	private void saveData(QueryDefinition definition) {
    	definition.setName(infoContainer.getName());
    	definition.setDescription(infoContainer.getDescription());
    	if (mainForm.getSavable().isLoaded()) {
    		definition.setQuery(new XStream().toXML(mainForm.getEditorModel().getQueryEditor().getQuery()));
    	}
	}
	
	private Query loadQuery(QueryDefinition def) {
		queryLoaded = true;
		if (def.getQuery() == null) {
			return new Query();
		}
		else {
			try {
				infoContainer.setName(def.getName());
				infoContainer.setDescription(def.getDescription());
				infoContainer.setUser(def.getSettingsUser().getUid());
		    	XStream xs = new XStream();
		    	return (Query) xs.fromXML(def.getQuery());
			}
			catch (Throwable t) {
				queryLoaded = false;
				return new Query();
			}
		}
	}
	

	public boolean isLoaded() {
		return queryLoaded;
	}
}
