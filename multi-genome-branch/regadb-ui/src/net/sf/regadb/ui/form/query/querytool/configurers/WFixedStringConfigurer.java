package net.sf.regadb.ui.form.query.querytool.configurers;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.FixedString;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

import eu.webtoolkit.jwt.WText;

public class WFixedStringConfigurer extends WText implements WordConfigurer {

    private FixedString string;
    
    public WFixedStringConfigurer(FixedString string) {
        super(lt(string.getHumanStringValue()));
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

	public boolean isUseless() {
		return false;
	}
	
	public String toString() {
		return string.getString();
	}
}
