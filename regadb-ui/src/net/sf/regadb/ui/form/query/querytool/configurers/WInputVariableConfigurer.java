package net.sf.regadb.ui.form.query.querytool.configurers;


import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.i8n.WMessage;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.InputVariable;
import com.pharmadm.custom.rega.queryeditor.OutputVariable;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.InputVariableController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WInputVariableConfigurer extends WComboBox implements WordConfigurer {
    private InputVariable var;
    private InputVariableController controller;
    private OutputVariable[] outputVars;
    
    /** 
     * <p>
     * Creates a new instance of JInputVariableConfigurer to show and configure
     * a particular InputVariable through a given InputVariableController
     * controller
     * </p>
     * <p>
     * @param var The InputVariable that the JComponent will configure
     * @param controller The controller in charge of configuration
     * </p>
     */
    public WInputVariableConfigurer(InputVariable input, InputVariableController controller) {
        super();
        this.setStyleClass("inputvariableconfigurer");
        outputVars = controller.getCompatibleOutputVariables(input).toArray(new OutputVariable[0]);
        
        for (OutputVariable ovar : outputVars) {
        	this.addItem(new WMessage(ovar.toString(), true));
        }
        
        this.var = input;
        this.controller = controller;
    }
    
    public ConfigurableWord getWord() {
        return var;
    }    
    
    public void configureWord() {
    	// only configure the inputvariable when there are available outputvariables
    	// this check is needed because the user may have removed all outputvariables 
    	// compatible with our inputvariable (making the clause this inputvariable belongs to
    	// invalid)
    	// if the user then tries to modify the clause we should leave this
    	// inputvariable unconfigured
    	if (this.currentIndex() >= 0 && this.currentIndex() < outputVars.length) {
    		this.controller.assignOutputVariable(var, outputVars[this.currentIndex()]);
    	}
    	else if (var.getOutputVariable() == null) {
    		// if we have changed clauses the old outputvariable is lost
    		// replace it with an undefined variable
    		this.controller.assignOutputVariable(var, new OutputVariable(var.getVariableType(), "[undefined]", var.getHumanStringValue()));
    	}
    }    

	public void reAssign(Object o) {
		WInputVariableConfigurer confy = (WInputVariableConfigurer) o;
		confy.controller = this.controller;
		confy.var = this.var; 
		confy.outputVars = this.outputVars;
		this.setCurrentIndex(confy.currentIndex());
	}
}
