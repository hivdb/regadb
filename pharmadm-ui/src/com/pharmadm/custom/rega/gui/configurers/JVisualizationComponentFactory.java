
/** Java class "VisualizationComponentFactory.java" generated from Poseidon for UML.
 *  Poseidon for UML is developed by <A HREF="http://www.gentleware.com">Gentleware</A>.
 *  Generated with <A HREF="http://jakarta.apache.org/velocity/">velocity</A> template engine.
 */
/*
 * (C) Copyright 2000-2007 PharmaDM n.v. All rights reserved.
 * 
 * This file is licensed under the terms of the GNU General Public License (GPL) version 2.
 * See http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */
package com.pharmadm.custom.rega.gui.configurers;

import com.pharmadm.custom.rega.queryeditor.*;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConfigurationController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConstantController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.InputVariableController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.VisualizationComponentFactory;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;
import com.pharmadm.custom.rega.reporteditor.*;
import com.pharmadm.custom.rega.reporteditor.wordconfiguration.*;

/**
 * <p>
 * A factory producing JComponents that allow GUI access to individual
 * ConfigurableWords.
 * </p>
 * 
 */
public class JVisualizationComponentFactory extends VisualizationComponentFactory{

	public JVisualizationComponentFactory(ConfigurationController controller,OutputReportSeeder seedController) {
		super(controller, seedController);
	}

	public JVisualizationComponentFactory(ConfigurationController controller) {
		super(controller, null);
	}
  
  ///////////////////////////////////////
  // operations



/**
 * <p>
 * Creates an appropriate JComponent for a given ConfigurableWord. The JComponent
 * can be used in the GUI to configure the ConfigurableWord. In principle, updates 
 * to any word's configuration should happen through the ConfigurationController
 * controller
 * </p>
 * <p>
 * If a seedController is specified, this can take care of ObjectListVariable seedings
 * </p>
 * <p>
 * 
 * @param word The ConfigurableWord that the JComponent will configure
 * </p>
 */
    public WordConfigurer createComponent(ConfigurableWord word) {   
        if (word instanceof Constant) {
        	Constant constant = (Constant)word;
	        if (constant.getSuggestedValues().isEmpty()) {
	            return new JConstantConfigurer(constant, (ConstantController)getConfigurationController());
	        } else {
	            return new JConstantChoiceConfigurer(constant, (ConstantController) getConfigurationController());
	        }
        } else if (word instanceof FixedString) {
            return new JFixedStringConfigurer((FixedString)word);
        } else if (word instanceof FromVariable) {
            return new JFromVariableConfigurer((FromVariable)word); 
        } else if (word instanceof ObjectListVariable) {
            if (getSeedController() != null) {
                return new JObjectListVariableSeeder((ObjectListVariable)word, getSeedController());
            } else {
                return new JObjectListVariableConfigurer((ObjectListVariable)word, getConfigurationController());
            }
        } else if (word instanceof OutputVariable) {
            return new JOutputVariableConfigurer((OutputVariable)word);
        } else if (word instanceof InputVariable) {
            return new JInputVariableConfigurer((InputVariable)word, (InputVariableController) getConfigurationController()); 
        } else if (word instanceof DataOutputVariable) {
            return new JDataOutputVariableConfigurer((DataOutputVariable)word, getConfigurationController());
        } else if (word instanceof DataInputVariable) {
            return new JDataInputVariableConfigurer((DataInputVariable)word, (DataInputVariableController) getConfigurationController(), getSeedController()); 
        } else {
            return null;
        }
        // your code here
    } // end createComponent 
} // end VisualizationComponentFactory