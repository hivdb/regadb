package net.sf.regadb.ui.form.query.querytool.configurers;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WImage;
import eu.webtoolkit.jwt.WWidget;

public class WDateConstantConfigurer extends WContainerWidget implements WordConfigurer{
	private WordConfigurer confy;
	private WImage calendarIcon_ = new WImage("pics/calendar.png");
	
	public WDateConstantConfigurer(WordConfigurer confy) {
		this.confy = confy;
		addWidget((WWidget) confy);
		addWidget(calendarIcon_);
		setInline(true);
	}

	public void configureWord() {
		confy.configureWord();
	}

	public ConfigurableWord getWord() {
		return confy.getWord();
	}

	public boolean isUseless() {
		return confy.isUseless();
	}

	public void reAssign(Object o) {
		confy.reAssign(o);
	}


}
