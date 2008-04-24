package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;

public class JComposedOutputVariableConfigurer extends javax.swing.JComboBox implements WordConfigurer {

	private Vector<JOutputVariableConfigurer> vars;
	
	public JComposedOutputVariableConfigurer(WordConfigurer var) {
		vars = new Vector<JOutputVariableConfigurer>();
		vars.add((JOutputVariableConfigurer) var);
		setModel(new DefaultComboBoxModel(vars));
		setSelectedIndex(0);
		setEditable(false);
	}
	
	public void add(List<WordConfigurer> words) {
		vars.add((JOutputVariableConfigurer) words.get(0));
	}
	
	@Override
	public void configureWord() {
		JOutputVariableConfigurer confy = (JOutputVariableConfigurer) getSelectedItem();
		confy.configureWord();
		
	}

	@Override
	public void freeResources() {
		// no resources used
	}

	@Override
	public ConfigurableWord getWord() {
		JOutputVariableConfigurer confy = (JOutputVariableConfigurer) getSelectedItem();
		return confy.getWord();
	}

	@Override
	public void reAssign(Object o) {
		JComposedOutputVariableConfigurer confy = (JComposedOutputVariableConfigurer) o;
		this.vars = confy.vars;
	}
}
