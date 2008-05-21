package net.sf.regadb.ui.form.query.querytool.configurers;

import net.sf.witty.wt.WLineEdit;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConstantController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WConstantConfigurer extends WLineEdit implements WordConfigurer {
    private Constant constant;
    private ConstantController controller;
    
    public WConstantConfigurer(Constant constant, ConstantController controller) {
    	super(constant.getHumanStringValue());
    	this.setStyleClass("constantconfigurer");
    	this.constant = constant;
    	this.controller = controller;
    }
    
    public ConfigurableWord getWord() {
        return constant;
    }
    
    
    public void configureWord() {
    	System.err.println("text:" + this.text());
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
}
