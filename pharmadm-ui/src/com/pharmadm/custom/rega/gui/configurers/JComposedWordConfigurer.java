package com.pharmadm.custom.rega.gui.configurers;

import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

public class JComposedWordConfigurer extends javax.swing.JComboBox implements ComposedWordConfigurer {

	private Vector<WordConfigurer> vars;
	
	public JComposedWordConfigurer(WordConfigurer var) {
		vars = new Vector<WordConfigurer>();
		vars.add(var);
		setModel(new DefaultComboBoxModel(vars));
		setSelectedIndex(0);
		setEditable(false);
	}
	
	public void add(List<WordConfigurer> keys, List<WordConfigurer> words) {
		vars.add(new JCombinedConfigurer(keys));
	}
	
	public void configureWord() {
		WordConfigurer confy = (WordConfigurer) getSelectedItem();
		confy.configureWord();
		
	}

	public ConfigurableWord getWord() {
		WordConfigurer confy = (WordConfigurer) getSelectedItem();
		return confy.getWord();
	}

	public void reAssign(Object o) {
		JComposedWordConfigurer confy = (JComposedWordConfigurer) o;
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

	public boolean isUseless() {
		return false;
	}	
}
