package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.reporteditor.OutputReportSeeder;

public abstract class VisualizationComponentFactory {
	private ConfigurationController controller;
	private OutputReportSeeder seedController = null;
	  
	public ConfigurationController getConfigurationController() {
		 return controller;
	}
	  
	public OutputReportSeeder getSeedController() {
		return seedController;
	}
	  
	public VisualizationComponentFactory(ConfigurationController controller, OutputReportSeeder seedController) {
	    this.controller = controller;
	    this.seedController = seedController;
	}
	  
	  
    public List<WordConfigurer> createComponents(List<ConfigurableWord> words) {
    	List<WordConfigurer> configurers = new ArrayList<WordConfigurer>();
    	for (ConfigurableWord word : words) {
    		configurers.add(createComponent(word));
    	}
    	return configurers;
    }

	public abstract WordConfigurer createComponent(ConfigurableWord word);
}
