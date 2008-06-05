package com.pharmadm.custom.rega.gui.configurers;

import java.util.List;

import com.pharmadm.custom.rega.awccomposition.NamedTablePropertyComposition;
import com.pharmadm.custom.rega.awccomposition.PrimitiveDeclarationComposition;
import com.pharmadm.custom.rega.awccomposition.PropertyFetchComposition;
import com.pharmadm.custom.rega.awccomposition.PropertySetComposition;
import com.pharmadm.custom.rega.awccomposition.TableFetchComposition;
import com.pharmadm.custom.rega.awccomposition.NamedTableFetchComposition;
import com.pharmadm.custom.rega.awccomposition.NewTableComposition;
import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.NullComposition;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedVisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class JComposedVisualizationComponentFactory extends
		ComposedVisualizationComponentFactory {

	@Override
	public ComposedWordConfigurer createWord(CompositionBehaviour behaviour,
			List<WordConfigurer> configurers) {
		
		if (behaviour instanceof NullComposition) {
			return null;
		}
		else if (behaviour instanceof PrimitiveDeclarationComposition) {
			JCombinedConfigurer constants = new JCombinedConfigurer(configurers.subList(1, 3));
			JComposedWordConfigurer string = new JComposedWordConfigurer(configurers.get(0));
			return new JAttributeConfigurer(string, constants);			
		}
		else if (behaviour instanceof NamedTablePropertyComposition) {
			JCombinedConfigurer constants = new JCombinedConfigurer(configurers.subList(1, 4));
			JComposedWordConfigurer string = new JComposedWordConfigurer(configurers.get(0));
			return new JAttributeConfigurer(string, constants);			
		}
		else if (behaviour instanceof PropertySetComposition) {
			JCombinedConfigurer constants = new JCombinedConfigurer(configurers.subList(1, 3));
			JComposedWordConfigurer string = new JComposedWordConfigurer(configurers.get(0));
			return new JAttributeConfigurer(string, constants);			
		}
		else if (behaviour instanceof NamedTableFetchComposition) {
			JCombinedConfigurer constants = new JCombinedConfigurer(configurers.subList(1, 4));
			JComposedWordConfigurer string = new JComposedWordConfigurer(configurers.get(0));
			return new JAttributeConfigurer(string, constants);			
		}
		else if (behaviour instanceof PropertyFetchComposition) {
			return new JComposedWordConfigurer(configurers.get(0));
		}
		else if (behaviour instanceof TableFetchComposition) {
			return new JComposedWordConfigurer(configurers.get(0));
		}
		else if (behaviour instanceof NewTableComposition) {
			return new JComposedWordConfigurer(configurers.get(0));
		}
		else {
			return null;
		}
	}

}
