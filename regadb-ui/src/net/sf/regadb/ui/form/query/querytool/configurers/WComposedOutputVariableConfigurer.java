package net.sf.regadb.ui.form.query.querytool.configurers;

import java.util.List;
import java.util.Vector;

import net.sf.regadb.ui.framework.widgets.MyComboBox;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.OutputVariableConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WComposedOutputVariableConfigurer extends MyComboBox implements ComposedWordConfigurer {

	private Vector<OutputVariableConfigurer> vars;
	
	public WComposedOutputVariableConfigurer(WordConfigurer var) {
		super();
		setStyleClass("composedoutputvariableconfigurer");
		vars = new Vector<OutputVariableConfigurer>();
		addItem((OutputVariableConfigurer) var);
	}
	
	private void addItem(OutputVariableConfigurer confy) {
		this.addItem(confy.toString());
		vars.add(confy);
	}
	
	public void add(List<WordConfigurer> keys, List<WordConfigurer> words) {
		addItem((OutputVariableConfigurer) keys.get(0));
	}
	
	public void configureWord() {
		OutputVariableConfigurer confy = getSelectedItem();
		confy.configureWord();
	}
	
	private OutputVariableConfigurer getSelectedItem() {
		return vars.get(getCurrentIndex());
	}

	public ConfigurableWord getWord() {
		OutputVariableConfigurer confy = getSelectedItem();
		return confy.getWord();
	}

	public void reAssign(Object o) {
		WComposedOutputVariableConfigurer confy = (WComposedOutputVariableConfigurer) o;
		this.vars = confy.vars;
	}

	public int getSelectedIndex() {
		return getCurrentIndex();
	}

	public void setSelectedIndex(int index) {
		setCurrentIndex(index);
	}

	public boolean isUseless() {
		return getSelectedItem().isUseless();
	}
}
