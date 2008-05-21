package net.sf.regadb.ui.form.query.querytool.configurers;

import java.util.List;

import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WWidget;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WCombinedConfigurer extends WContainerWidget implements WordConfigurer {
	private List<WordConfigurer> words;
	
	public WCombinedConfigurer(List<WordConfigurer> words) {
		super();
		setStyleClass("combinedconfigurer");
		this.words = words;
		setInline(true);

		for (WordConfigurer confy : words) {
			addWidget((WWidget) confy);
		}
	}

	public void configureWord() {
		for (WordConfigurer confy : words) {
			confy.configureWord();
		}
	}

	public ConfigurableWord getWord() {
		return words.get(0).getWord();
	}

	public void reAssign(Object o) {
		WCombinedConfigurer confy = (WCombinedConfigurer) o;
		this.words = confy.words;
	}

	public boolean isUseless() {
		return false;
	}
}
