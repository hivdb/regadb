package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;

public class ComposedAWCEditor {
    private List<AtomicWhereClauseEditor> controllers;
	private ComposedWordConfigurer indexConfigurer;
	private ComposedAWCEditorPanel editPanel;
	
	public ComposedAWCEditor(ComposedAWCEditorPanel panel, AtomicWhereClauseEditor controller) {
		this.editPanel = panel;
		controllers = new ArrayList<AtomicWhereClauseEditor>();
		controllers.add(controller);
        indexConfigurer = null;
	}
	
    /** Applies changes made to all visualisation components in the componentList to the corresponding AWCWords */
    public void applyEditings() {
    	List<WordConfigurer> configurers = editPanel.getConfigurers();
    	// reassign configurers of the first clause to the active clause
    	AtomicWhereClauseEditor newEditor = getSelectedEditor();
    	
    	if (indexConfigurer != null) {
	    	List<WordConfigurer> newConfigList = getConfigurers(newEditor);
	    	for (int i = 0 ; i < configurers.size() ; i++) {
	    		if (!configurers.get(i).equals(indexConfigurer)) {
	    			newConfigList.get(i).reAssign(configurers.get(i));
	    			configurers.set(i, newConfigList.get(i));
	    		}
	    	}
    	}
    	
    	for (WordConfigurer confy : configurers) {
            confy.configureWord();
        }
    }	
    
	private List<WordConfigurer> getConfigurers(ConfigurationController controller) {
		return controller.getVisualizationComponentFactory().createComponents(controller.getVisualizationList());
	}    
	
    public AtomicWhereClauseEditor getSelectedEditor() {
    	if (indexConfigurer == null) {
    		return controllers.get(0);
    	}
    	else {
    		return controllers.get(indexConfigurer.getSelectedIndex());
    	}
    }
    
    public void createComposedWord(List<ConfigurableWord> keys, List<ConfigurableWord> words, ComposedWordConfigurer configurer) {
    	List<ConfigurableWord> all = new ArrayList<ConfigurableWord>();
    	all.addAll(keys);
    	all.addAll(words);
    	if (CompositionBehaviour.replaceByComposedWord(editPanel.getConfigurers(), all, configurer)) {
    		indexConfigurer = configurer;
        	editPanel.initConfigurers();
    	}
    }
    
    public void composeWord(List<WordConfigurer> keys, List<WordConfigurer> additions, AtomicWhereClauseEditor  editor, boolean makeSelected) {
    	if (indexConfigurer == null) {
    		System.err.println("Trying to compose a clause before the composing word has been initialized.");
    		System.exit(1);
    	}
		indexConfigurer.add(keys, additions);
        controllers.add(editor);

        if (makeSelected) {
        	indexConfigurer.setSelectedIndex(controllers.size()-1);
        }
    }	
    
    /**
     * returns true if all composed clauses are useless
     * @return
     */
    public boolean isUseless() {
    	if (indexConfigurer == null) {
    		for (WordConfigurer confy : editPanel.getConfigurers()) {
    			if (confy.isUseless()) {
    				return true;
    			}
    		}   
    		return false;
    	}
    	else {
    		// composed clause
    		// let the index configurer decide
    		return indexConfigurer.isUseless();
    	}
    }
}
