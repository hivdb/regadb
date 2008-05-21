package com.pharmadm.custom.rega.gui.configurers;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JPanel;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

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

	public void configureWord() {
		for (WordConfigurer confy : words) {
			confy.configureWord();
		}
	}

	public ConfigurableWord getWord() {
		return words.get(0).getWord();
	}

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

	public boolean isUseless() {
		return false;
	}
}
