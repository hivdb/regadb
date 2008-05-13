package com.pharmadm.custom.rega.gui.awceditor;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditor;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditorPanel;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;


/**
 * Specialized version of {@link WLOEditorPanel} for configuring an {@link AtomicWhereClause}
 * Objects of this class can contain multiple {@link AtomicWhereClauseEditor}s. This
 * allows for editing of composed clauses.
 * @author  kristof
 */
public class AWCEditorPanel extends WLOEditorPanel implements ComposedAWCEditorPanel{
    private ComposedAWCEditor editor;
	
    /** Creates a new instance of AWCEditorPanel */
    public AWCEditorPanel(AtomicWhereClauseEditor controller) {
        super(controller);
        editor = new ComposedAWCEditor(this, controller);
    }
    
    /** Applies changes made to all visualisation components in the componentList to the corresponding AWCWords */
    public void applyEditings() {
    	editor.applyEditings();
    }
    
    
    
    public AtomicWhereClause getClause() {
    	return editor.getSelectedEditor().getAtomicWhereClause();
    }
    
    public AtomicWhereClauseEditor getWhereClauseEditor() {
    	return (AtomicWhereClauseEditor)controller;
    }
    
    public void createComposedWord(List<ConfigurableWord> words, ComposedWordConfigurer configurer) {
    	editor.createComposedWord(words, configurer);
    }
    
    public void composeWord(List<WordConfigurer> additions, AtomicWhereClauseEditor  editor) {
    	this.editor.composeWord(additions, editor);
    }

	public List<WordConfigurer> getConfigurers() {
		return configList;
	}
}
