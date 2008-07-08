package net.sf.regadb.ui.form.query.querytool.configurers;

import java.util.List;
import java.util.Vector;

import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.i8n.WMessage;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WComposedWordConfigurer extends WComboBox implements ComposedWordConfigurer  {

	private Vector<WordConfigurer> vars;
	
	public WComposedWordConfigurer(WordConfigurer var) {
		super();
		setStyleClass("composedoutputvariableconfigurer");
		vars = new Vector<WordConfigurer>();
		addItem((WordConfigurer) var);
	}
	
	private void addItem(WordConfigurer confy) {
		this.addItem(new WMessage(confy.toString(), true));
		vars.add(confy);
	}
	
	public void add(List<WordConfigurer> keys, List<WordConfigurer> words) {
		addItem(new WCombinedConfigurer(keys));
	}
	
	public void configureWord() {
		WordConfigurer confy = getSelectedItem();
		confy.configureWord();
	}
	
	private WordConfigurer getSelectedItem() {
		return vars.get(currentIndex());
	}

	public ConfigurableWord getWord() {
		WordConfigurer confy = getSelectedItem();
		return confy.getWord();
	}

	public void reAssign(Object o) {
		WComposedWordConfigurer confy = (WComposedWordConfigurer) o;
		this.vars = confy.vars;
	}

	public int getSelectedIndex() {
		return currentIndex();
	}

	public void setSelectedIndex(int index) {
		setCurrentIndex(index);
	}

	public boolean isUseless() {
		return getSelectedItem().isUseless();
	}

}
