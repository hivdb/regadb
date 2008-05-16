package com.pharmadm.custom.rega.gui.configurers;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.OutputVariableConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class JComposedOutputVariableConfigurer extends javax.swing.JComboBox implements ComposedWordConfigurer {

	private Vector<OutputVariableConfigurer> vars;
	
	public JComposedOutputVariableConfigurer(WordConfigurer var) {
		vars = new Vector<OutputVariableConfigurer>();
		vars.add((OutputVariableConfigurer) var);
		setModel(new DefaultComboBoxModel(vars));
		setSelectedIndex(0);
		setEditable(false);
	}
	
	public void add(List<WordConfigurer> words) {
		vars.add((OutputVariableConfigurer) words.get(0));
	}
	
	public void configureWord() {
		OutputVariableConfigurer confy = (OutputVariableConfigurer) getSelectedItem();
		confy.configureWord();
		
	}

	public ConfigurableWord getWord() {
		OutputVariableConfigurer confy = (OutputVariableConfigurer) getSelectedItem();
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

	public void setSelectedIndex() {}	
}
