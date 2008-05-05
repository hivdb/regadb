package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.WordConfigurer;

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
	
	public void configureWord() {
		JOutputVariableConfigurer confy = (JOutputVariableConfigurer) getSelectedItem();
		confy.configureWord();
		
	}

	public void freeResources() {
		// no resources used
	}

	public ConfigurableWord getWord() {
		JOutputVariableConfigurer confy = (JOutputVariableConfigurer) getSelectedItem();
		return confy.getWord();
	}

	public void reAssign(Object o) {
		JComposedOutputVariableConfigurer confy = (JComposedOutputVariableConfigurer) o;
		this.vars = confy.vars;
	}
	
    public void addFocusListener(java.awt.event.FocusListener listener) {
		for (Component  comp : this.getComponents()) {
			comp.addFocusListener(listener);
    	}
		super.addFocusListener(listener);
    }
	
    public void addMouseListener(MouseListener listener) {
		for (Component  comp : this.getComponents()) {
			comp.addMouseListener(listener);
    	}
		super.addMouseListener(listener);
    }	
}
