
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
package com.pharmadm.custom.rega.queryeditor.gui;

import com.pharmadm.custom.rega.queryeditor.*;
//import com.pharmadm.custom.rega.chem.search.MoleculeConstant;
import com.pharmadm.custom.rega.reporteditor.*;
import com.pharmadm.custom.rega.reporteditor.gui.*;
import java.util.*;
import javax.swing.*;

/**
 * <p>
 * A factory producing JComponents that allow GUI access to individual
 * ConfigurableWords.
 * </p>
 * 
 */
public class VisualizationComponentFactory {


  private ConfigurationController controller;
  private QueryOutputReportSeeder seedController = null;
  
  public VisualizationComponentFactory(ConfigurationController controller) {
      this.controller = controller;
  }
  
  public VisualizationComponentFactory(ConfigurationController controller, QueryOutputReportSeeder seedController) {
      this.controller = controller;
      this.seedController = seedController;
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
//        if (word instanceof MoleculeConstant) {
//            return new JMoleculeConstantConfigurer((MoleculeConstant)word, (ConstantController)controller);
//        } else 
            if (word instanceof Constant) {
            Constant constant = (Constant)word;
            if (constant.getSuggestedValuesList().size() > 0) {
                return new JConstantChoiceConfigurer(constant, (ConstantController)controller);
            } else {
                return new JConstantConfigurer(constant, (ConstantController)controller);
            }
        } else if (word instanceof FixedString) {
            return new JFixedStringConfigurer((FixedString)word, controller);
        } else if (word instanceof FromVariable) {
            return new JFromVariableConfigurer((FromVariable)word, controller); 
        } else if (word instanceof ObjectListVariable) {
            if (seedController != null) {
                return new JObjectListVariableSeeder((ObjectListVariable)word, seedController);
            } else {
                return new JObjectListVariableConfigurer((ObjectListVariable)word, controller);
            }
        } else if (word instanceof OutputVariable) {
            return new JOutputVariableConfigurer((OutputVariable)word, controller);
        } else if (word instanceof InputVariable) {
            return new JInputVariableConfigurer((InputVariable)word, (InputVariableController)controller); 
        } else if (word instanceof DataOutputVariable) {
            return new JDataOutputVariableConfigurer((DataOutputVariable)word, controller);
        } else if (word instanceof DataInputVariable) {
            return new JDataInputVariableConfigurer((DataInputVariable)word, (DataInputVariableController)controller, seedController); 
        } else {
            return null;
        }
        // your code here
    } // end createComponent        

} // end VisualizationComponentFactory



