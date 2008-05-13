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
	
    public void createComposedWord(List<ConfigurableWord> words, ComposedWordConfigurer configurer) {
    	if (CompositionBehaviour.replaceByComposedWord(editPanel.getConfigurers(), words, configurer)) {
    		indexConfigurer = configurer;
        	editPanel.initConfigurers();
    	}
    }
    
    public void composeWord(List<WordConfigurer> additions, AtomicWhereClauseEditor  editor) {
    	if (indexConfigurer == null) {
    		System.err.println("Trying to compose a clause before the composing word has been initialized.");
    		System.exit(1);
    	}
		indexConfigurer.add(additions);
        controllers.add(editor);
    }	
}
