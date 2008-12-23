package net.sf.regadb.ui.form.query.querytool.configurers;

import net.sf.regadb.ui.form.query.querytool.widgets.CssClasses;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConstantController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

import eu.webtoolkit.jwt.Signal;
import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WKeyEvent;
import eu.webtoolkit.jwt.WLineEdit;
import eu.webtoolkit.jwt.WValidator;

public class WConstantConfigurer extends WLineEdit implements WordConfigurer {
    private Constant constant;
    private ConstantController controller;
    private CssClasses css;
    
    public WConstantConfigurer(Constant constant, ConstantController controller) {
    	super(constant.getHumanStringValue());
    	this.setStyleClass("constantconfigurer");
    	this.constant = constant;
    	this.controller = controller;
    	css = new CssClasses(this);
    	setValidator(new WConstantValidator());
    	validator().setMandatory(true);
    	
    	this.keyWentUp.addListener(this, new Signal1.Listener<WKeyEvent>() {
			public void trigger(WKeyEvent a) {
				validateConstant();
			}
    	});
    	
    	this.changed.addListener(this, new Signal.Listener() {
			public void trigger() {
				validateConstant();
			}
    	});
    }
    
    private void validateConstant() {
    	if (validate() == WValidator.State.Valid) {
    		css.removeStyle("form-field textfield edit-invalid");
    	}
    	else {
    		css.addStyle("form-field textfield edit-invalid");
    	}
    }
    
    private class WConstantValidator extends WValidator {
    	public WValidator.State validate(String input) {
    		if (constant.validateValue(input)) {
    			return WValidator.State.Valid;
    		}
    		else {
    			return WValidator.State.Invalid;
    		}
    	}
    }
    
    
    public ConfigurableWord getWord() {
        return constant;
    }
    
    
    public void configureWord() {
        if (! controller.setConstantValueString(constant, this.text())) {
            System.err.println("Warning : word configuration failed !");
        }
    }
    

	public void reAssign(Object o) {
		WConstantConfigurer confy = (WConstantConfigurer) o;
		this.controller = confy.controller;
		this.constant = confy.constant;
		this.setText(confy.text());
	}

	public boolean isUseless() {
		return false;
	}
}
