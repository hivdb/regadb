package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;

public abstract class ComposedVisualizationComponentFactory {

	public abstract ComposedWordConfigurer createWord(CompositionBehaviour behaviour, List<WordConfigurer> configurers);
}
