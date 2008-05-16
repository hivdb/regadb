package net.sf.regadb.ui.form.query.querytool.configurers;

import java.util.List;

import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.i8n.WMessage;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.constant.Constant;
import com.pharmadm.custom.rega.queryeditor.constant.SuggestedValuesOption;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConstantController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WConstantChoiceConfigurer extends WComboBox implements WordConfigurer {

    private Constant constant;
    private ConstantController controller;
    private List<SuggestedValuesOption> values;
    
    public WConstantChoiceConfigurer(Constant constant, ConstantController controller) {
        super();
		setStyleClass("constantchoiceconfigurer");
        this.controller = controller;
        this.constant = constant;
        this.values = constant.getSuggestedValuesList();
        for (SuggestedValuesOption option : values) {
        	this.addItem(new WMessage(option.getOption().toString(), true));
        }
        this.setCurrentItem(new WMessage(constant.getHumanStringValue(), true));
        
        // last item gets selected when setCurrentItem can't find the given item
        // set it back to zero if that happens
    	if (!this.currentText().keyOrValue().equals(constant.getHumanStringValue())) {
    		this.setCurrentIndex(0);
    	}
    	
    }
    
    public ConfigurableWord getWord() {
        return constant;
    }
    
    public void configureWord() {
        if (! controller.setConstantValueString(constant, values.get(currentIndex()).getValue())) {
            System.err.println("Warning : word configuration failed !");
        }
    }
    

	public void reAssign(Object o) {
		WConstantChoiceConfigurer confy = (WConstantChoiceConfigurer) o;
		this.controller = confy.controller;
		this.constant = confy.constant;
		this.values = confy.values;
		this.setCurrentIndex(confy.currentIndex());
	}
}
