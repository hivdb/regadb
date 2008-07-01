package net.sf.regadb.ui.form.query.querytool.configurers;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.FixedString;
import com.pharmadm.custom.rega.queryeditor.FromVariable;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.catalog.DbObject.ValueType;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConfigurationController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConstantController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.InputVariableController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.VisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WVisualizationComponentFactory extends
		VisualizationComponentFactory {

	public WVisualizationComponentFactory(ConfigurationController controller) {
		super(controller, null);
	}

	@Override
	public WordConfigurer createComponent(ConfigurableWord word) {
		if (word instanceof FixedString) {
			return new WFixedStringConfigurer((FixedString) word);
		}
		else if (word instanceof FromVariable) {
			return new WFromVariableConfigurer((FromVariable) word);
		}
		else if (word instanceof OutputVariable) {
			return new WOutputVariableConfigurer((OutputVariable) word);
		}
		else if (word instanceof InputVariable) {
			return new WInputVariableConfigurer((InputVariable) word, (InputVariableController) getConfigurationController());
		}
		else if (word instanceof Constant) {
			Constant constant = (Constant) word;
			if (constant.getSuggestedValues().isEmpty()) {
				WordConfigurer confy = new WConstantConfigurer(constant, (ConstantController) getConfigurationController());
				if (constant.getDbObject().getValueType() == ValueType.Date) {
					confy =  new WDateConstantConfigurer(confy);
				}
				return confy;
			}
			else {
				return new WConstantChoiceConfigurer(constant, (ConstantController) getConfigurationController());
			}
		}
		else {
			return null;
		}
	}

}
