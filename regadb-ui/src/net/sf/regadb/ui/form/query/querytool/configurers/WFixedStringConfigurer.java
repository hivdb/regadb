package net.sf.regadb.ui.form.query.querytool.configurers;

import net.sf.witty.wt.WText;
import net.sf.witty.wt.i8n.WMessage;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.FixedString;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WFixedStringConfigurer extends WText implements WordConfigurer {

    private FixedString string;
    
    public WFixedStringConfigurer(FixedString string) {
        super(new WMessage(string.getHumanStringValue(), true));
        this.setStyleClass("fixedstringconfigurer");
        this.string = string;
    }
    
    /** does nothing, fixed strings can not be configured */
    public void configureWord() {
    }
    
    public ConfigurableWord getWord() {
        return string;
    }

	public void reAssign(Object o) {
		// does nothing. no configuration required
	}
}
