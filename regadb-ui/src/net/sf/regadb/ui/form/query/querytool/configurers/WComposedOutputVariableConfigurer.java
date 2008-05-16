package net.sf.regadb.ui.form.query.querytool.configurers;

import java.util.List;
import java.util.Vector;

import net.sf.witty.wt.WComboBox;
import net.sf.witty.wt.i8n.WMessage;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.OutputVariableConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class WComposedOutputVariableConfigurer extends WComboBox implements ComposedWordConfigurer {

	private Vector<OutputVariableConfigurer> vars;
	
	public WComposedOutputVariableConfigurer(WordConfigurer var) {
		super();
		setStyleClass("composedoutputvariableconfigurer");
		vars = new Vector<OutputVariableConfigurer>();
		addItem((OutputVariableConfigurer) var);
	}
	
	private void addItem(OutputVariableConfigurer confy) {
		this.addItem(new WMessage(confy.toString(), true));
		vars.add(confy);
	}
	
	public void add(List<WordConfigurer> words) {
		addItem((OutputVariableConfigurer) words.get(0));
	}
	
	public void configureWord() {
		OutputVariableConfigurer confy = getSelectedItem();
		confy.configureWord();
	}
	
	private OutputVariableConfigurer getSelectedItem() {
		return vars.get(currentIndex());
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
		return currentIndex();
	}

	public void setSelectedIndex(int index) {
		setCurrentIndex(index);
	}
}
