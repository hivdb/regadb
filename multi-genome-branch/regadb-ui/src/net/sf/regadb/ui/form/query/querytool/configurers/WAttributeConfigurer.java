package net.sf.regadb.ui.form.query.querytool.configurers;

import java.util.ArrayList;
import java.util.List;

import net.sf.witty.wt.SignalListener;
import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WInteractWidget;
import net.sf.witty.wt.WKeyEvent;
import net.sf.witty.wt.WMouseEvent;
import net.sf.witty.wt.WWidget;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

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
		
		((WInteractWidget) ovar).clicked.addListener(new SignalListener<WMouseEvent>() {
			public void notify(WMouseEvent a) {
				changeSelection();
			}
		});
		
		((WInteractWidget) ovar).keyWentUp.addListener(new SignalListener<WKeyEvent>() {
			public void notify(WKeyEvent a) {
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
