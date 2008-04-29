package com.pharmadm.custom.rega.gui;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.WordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;


/**
 *
 * @author  kristof
 */
public class AWCEditorPanel extends WLOEditorPanel {
    
	private List<AtomicWhereClauseEditor> editors;
	private WordConfigurer indexConfigurer;
	
    /** Creates a new instance of AWCEditorPanel */
    public AWCEditorPanel(AtomicWhereClauseEditor controller) {
        super(controller);
        editors = new ArrayList<AtomicWhereClauseEditor>();
        editors.add(controller);
        indexConfigurer = null;
    }
    
    /** Applies changes made to all visualisation components in the componentList to the corresponding AWCWords */
    public void applyEditings() {
    	// reassign configurers of the first clause to the active clause
    	AtomicWhereClauseEditor newEditor = getSelectedEditor();
    	
    	if (indexConfigurer != null) {
	    	List<WordConfigurer> newConfigList = getConfigurers(newEditor);
	    	for (int i = 0 ; i < configList.size() ; i++) {
	    		if (!configList.get(i).equals(indexConfigurer)) {
	    			newConfigList.get(i).reAssign(configList.get(i));
	    			configList.set(i, newConfigList.get(i));
	    		}
	    	}
    	}
    	super.applyEditings();
    }
    
    private AtomicWhereClauseEditor getSelectedEditor() {
    	if (indexConfigurer == null) {
    		return editors.get(0);
    	}
    	else {
    		return editors.get(indexConfigurer.getSelectedIndex());
    	}
    }
    
    
    public AtomicWhereClause getClause() {
        return getSelectedEditor().getAtomicWhereClause();
    }
    
    public AtomicWhereClauseEditor getWhereClauseEditor() {
    	return (AtomicWhereClauseEditor)controller;
    }
    
    public void createComposedWord(List<ConfigurableWord> words, WordConfigurer configurer) {
    	List<ConfigurableWord> oldWords = new ArrayList<ConfigurableWord>();
    	oldWords.addAll(words);
    	int i = 0;
    	boolean firstWordFound = false;
    	while (i < configList.size() && oldWords.size() > 0) {
			if (configList.get(i).getWord().equals(oldWords.get(0))) {
				if (!firstWordFound) {
	    			configList.set(i, configurer);
	    			indexConfigurer = configurer;
	    			firstWordFound = true;
	    			i++;
				}
				else {
					configList.remove(i);
				}
				oldWords.remove(0);
			}
			else {
				i++;
			}
    	}
    	initConfigurers();
    }
    
    public void composeWord(List<WordConfigurer> additions, AtomicWhereClauseEditor  editor) {
    	if (indexConfigurer == null) {
    		System.err.println("Trying to compose a clause before the composing word has been initialized.");
    		System.exit(1);
    	}
		indexConfigurer.add(additions);
        editors.add(editor);
    }
}
