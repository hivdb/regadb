package net.sf.regadb.ui.form.query.querytool.configurers;

import java.util.ArrayList;
import java.util.List;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

import eu.webtoolkit.jwt.Signal1;
import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WInteractWidget;
import eu.webtoolkit.jwt.WKeyEvent;
import eu.webtoolkit.jwt.WMouseEvent;
import eu.webtoolkit.jwt.WWidget;

public class WAttributeConfigurer extends WContainerWidget implements ComposedWordConfigurer {

	private ComposedWordConfigurer ovar;
	private List<WordConfigurer> constantPanels;
	private WContainerWidget contentTable;
	
	public WAttributeConfigurer(ComposedWordConfigurer ovar, WordConfigurer constantPanel) {
		this.ovar = ovar;
		this.setInline(true);
		this.constantPanels = new ArrayList<WordConfigurer>();
		constantPanels.add(constantPanel);
		
		this.addWidget((WWidget) ovar);
		contentTable = new WContainerWidget(this);
		contentTable.setInline(true);
		contentTable.addWidget((WWidget) constantPanels.get(ovar.getSelectedIndex()));
		
		((WInteractWidget) ovar).clicked.addListener(this, new Signal1.Listener<WMouseEvent>() {
			public void trigger(WMouseEvent a) {
				changeSelection();
			}
		});
		
		((WInteractWidget) ovar).keyWentUp.addListener(this, new Signal1.Listener<WKeyEvent>() {
			public void trigger(WKeyEvent a) {
				changeSelection();
			}
		});
	}
	
	private void changeSelection() {
		int index = ovar.getSelectedIndex();
		if (index >= 0 && index < constantPanels.size()) {
			contentTable.removeWidget(contentTable.children().get(0));
			contentTable.addWidget((WWidget) constantPanels.get(index));
		}
	}
	
	public void add(List<WordConfigurer> keys, List<WordConfigurer> words) {
		ovar.add(keys, words);
		constantPanels.add(new WCombinedConfigurer(words));
	}

	public void configureWord() {
		constantPanels.get(ovar.getSelectedIndex()).configureWord();
		ovar.configureWord();
	}

	public int getSelectedIndex() {
		return ovar.getSelectedIndex();
	}

	public ConfigurableWord getWord() {
		return ovar.getWord();
	}

	public void reAssign(Object o) {
		WAttributeConfigurer confy = (WAttributeConfigurer) o;
		this.constantPanels = confy.constantPanels;
		this.ovar = confy.ovar;
	}

	public void setSelectedIndex(int index) {
		ovar.setSelectedIndex(index);
		changeSelection();
	}

	public boolean isUseless() {
		return (ovar.isUseless() || constantPanels.get(ovar.getSelectedIndex()).isUseless());
	}
}
