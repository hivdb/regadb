package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;

public interface ComposedAWCEditorPanel {
	public List<WordConfigurer> getConfigurers();
	public abstract void initConfigurers();
    public abstract void applyEditings();
    public abstract AtomicWhereClause getClause();
    public abstract void createComposedWord(List<ConfigurableWord> keys, List<ConfigurableWord> words, ComposedWordConfigurer configurer);    
    public abstract void composeWord(List<WordConfigurer> keys, List<WordConfigurer> additions, AtomicWhereClauseEditor  editor, boolean makeSelected);
    public ConfigurationController getEditor();
    
    /**
     * return true if it is useless to select this clause
     * @return
     */
    public abstract boolean isUseless();
}
