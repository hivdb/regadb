package net.sf.regadb.ui.form.query.querytool.configurers;

import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.FromVariable;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WFromVariableConfigurer extends WText implements WordConfigurer {

    private FromVariable var;
    public WFromVariableConfigurer(FromVariable var) {
        super(new WMessage(var.getUniqueName(), true));
        this.setStyleClass("fromvariableconfigurer");
        this.var = var;
    }
    
    /** does nothing, from variables can not be configured */
    public void configureWord() {
    }
    
    public ConfigurableWord getWord() {
        return var;
    }

	public void reAssign(Object o) {
		// does nothing. no configuration required
	}

	public boolean isUseless() {
		return false;
	}
}
