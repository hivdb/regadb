package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JPanel;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;

/**
 * Wrapper for a list of configurers
 * @author fromba0
 *
 */
public class JCombinedConfigurer extends JPanel implements WordConfigurer{
	
	private List<WordConfigurer> words;
	
	public JCombinedConfigurer(List<WordConfigurer> words) {
		this.words = words;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));

		for (WordConfigurer confy : words) {
			this.add((Component) confy);
		}
	}

	/**
	 * not a composed configurer. does nothing
	 */
	@Override
	public void add(List<WordConfigurer> words) {
	}

	@Override
	public void configureWord() {
		for (WordConfigurer confy : words) {
			confy.configureWord();
		}
	}

	@Override
	public void freeResources() {
		for (WordConfigurer confy : words) {
			confy.freeResources();
		}
	}

	@Override
	public int getSelectedIndex() {
		return 0;
	}

	@Override
	public ConfigurableWord getWord() {
		return words.get(0).getWord();
	}

	@Override
	public void reAssign(Object o) {
		JCombinedConfigurer confy = (JCombinedConfigurer) o;
		this.words = confy.words;
	}
	
    public void addFocusListener(java.awt.event.FocusListener listener) {
		for (WordConfigurer  confy : this.words) {
			Component comp = (Component) confy;
			comp.addFocusListener(listener);
    	}
		super.addFocusListener(listener);
    }
	
    public void addMouseListener(MouseListener listener) {
		for (WordConfigurer  confy : this.words) {
			Component comp = (Component) confy;
			comp.addMouseListener(listener);
    	}
		super.addMouseListener(listener);
    }
}
