package com.pharmadm.custom.rega.gui.awceditor;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditor;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditorPanel;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCManager;

/**
 * Specialized version of {@link WLOEditorPanel} for configuring an {@link AtomicWhereClause}
 * Objects of this class can contain multiple {@link AtomicWhereClauseEditor}s. This
 * allows for editing of composed clauses.
 * @author  kristof
 */
public class AWCEditorPanel extends WLOEditorPanel implements ComposedAWCEditorPanel{
	private ComposedAWCManager manager;
    
    /** Creates a new instance of AWCEditorPanel */
    public AWCEditorPanel(AtomicWhereClauseEditor controller) {
        super(controller);
        manager = new ComposedAWCManager(new ComposedAWCEditor(this, controller));
    }
    
    /** Applies changes made to all visualisation components in the componentList to the corresponding AWCWords */
    public void applyEditings() {
    	getManager().applyEditings();
    }
    
	public List<WordConfigurer> getConfigurers() {
		return configList;
	}

	public ComposedAWCManager getManager() {
		return manager;
	}	
}
