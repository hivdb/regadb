package net.sf.regadb.ui.form.query.querytool;

import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.QueryEditorComponent;
import com.pharmadm.custom.rega.savable.Savable;

public interface QueryToolApp {
	public Savable getSavable();
	public QueryEditorComponent getEditorModel();
	public QueryContext getQueryContext();
	public void setQueryEditable(boolean editable);
	public boolean isQueryEditable();
	public void updateControls();
	public void runQuery();
}
