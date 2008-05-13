package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;

public interface ComposedAWCEditorPanel {
	public List<WordConfigurer> getConfigurers();
	public abstract void initConfigurers();
    public abstract void applyEditings();
    public abstract AtomicWhereClause getClause();
    public abstract void createComposedWord(List<ConfigurableWord> words, ComposedWordConfigurer configurer);    
    public abstract void composeWord(List<WordConfigurer> additions, AtomicWhereClauseEditor  editor);
    public ConfigurationController getEditor();
}
