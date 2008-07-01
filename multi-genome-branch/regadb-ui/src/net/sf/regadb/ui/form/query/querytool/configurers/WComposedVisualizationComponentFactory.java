package net.sf.regadb.ui.form.query.querytool.configurers;

import java.util.List;

import com.pharmadm.custom.rega.awccomposition.PropertyFetchComposition;
import com.pharmadm.custom.rega.awccomposition.TableFetchComposition;
import com.pharmadm.custom.rega.awccomposition.NewTableComposition;
import com.pharmadm.custom.rega.queryeditor.CompositionBehaviour;
import com.pharmadm.custom.rega.queryeditor.NullComposition;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedVisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WComposedVisualizationComponentFactory extends
		ComposedVisualizationComponentFactory {

	@Override
	public ComposedWordConfigurer createWord(CompositionBehaviour behaviour,
			List<WordConfigurer> configurers) {
		if (behaviour instanceof NullComposition) {
			return null;
		}
		else if (behaviour instanceof PropertyFetchComposition) {
			return new WComposedOutputVariableConfigurer(configurers.get(0));
		}
		else if (behaviour instanceof TableFetchComposition) {
			return new WComposedOutputVariableConfigurer(configurers.get(0));
		}
		else if (behaviour instanceof NewTableComposition) {
			return new WComposedOutputVariableConfigurer(configurers.get(0));
		}
		else {
			return getAttributeConfigurer(configurers);
		}
	}
	
	private WAttributeConfigurer getAttributeConfigurer(List<WordConfigurer> configurers) {
		WCombinedConfigurer constants = new WCombinedConfigurer(configurers.subList(1, configurers.size()));
		WComposedWordConfigurer string = new WComposedWordConfigurer(configurers.get(0));
		return new WAttributeConfigurer(string, constants);			
		
	}

}
