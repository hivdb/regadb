package net.sf.regadb.ui.form.query.querytool.configurers;

import net.sf.regadb.ui.form.query.querytool.widgets.CssClasses;
import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WEmptyEvent;
import net.sf.witty.wt.WKeyEvent;
import net.sf.witty.wt.WLineEdit;
import net.sf.witty.wt.validation.WValidator;
import net.sf.witty.wt.validation.WValidatorPosition;
import net.sf.witty.wt.validation.WValidatorState;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConstantController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

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
    	
    	this.keyWentUp.addListener(new SignalListener<WKeyEvent>() {
			public void notify(WKeyEvent a) {
				validateConstant();
			}
    	});
    	
    	this.changed.addListener(new SignalListener<WEmptyEvent>() {
			public void notify(WEmptyEvent a) {
				validateConstant();
			}
    	});
    }
    
    private void validateConstant() {
    	if (validate() == WValidatorState.Valid) {
    		css.removeStyle("form-field textfield edit-invalid");
    	}
    	else {
    		css.addStyle("form-field textfield edit-invalid");
    	}
    }
    
    private class WConstantValidator extends WValidator {
    	public WValidatorState validate(String input, WValidatorPosition pos) {
    		if (constant.validateValue(input)) {
    			return WValidatorState.Valid;
    		}
    		else {
    			return WValidatorState.Invalid;
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
