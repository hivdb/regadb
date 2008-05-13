package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.List;

public interface ComposedWordConfigurer extends WordConfigurer {
    /**
     * adds the given words to this configurer
     * @param words
     */
    public void add(List<WordConfigurer> words);
    
    /**
     * gets the index of the selected word
     * @return
     */
    public int getSelectedIndex();
}
