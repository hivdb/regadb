package net.sf.regadb.ui.form.query.querytool.awceditor;

import net.sf.regadb.ui.form.query.querytool.configurers.WVisualizationComponentFactory;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.QueryContext;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;

public class WAtomicWhereClauseEditor extends AtomicWhereClauseEditor {

	public WAtomicWhereClauseEditor(QueryContext context,
			AtomicWhereClause clause) {
		super(context, clause);
		setVisualizationComponentFactory(new WVisualizationComponentFactory(this));
	}

}
