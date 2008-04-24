package com.pharmadm.custom.rega.queryeditor.wordconfiguration;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;

public class JAttributeConfigurer extends JPanel implements WordConfigurer {

	private JComposedOutputVariableConfigurer ovar;
	private List<JCombinedConfigurer> constantPanels;
	
	public JAttributeConfigurer(JComposedOutputVariableConfigurer ovar, JCombinedConfigurer constantPanel) {
		this.ovar = ovar;
		this.constantPanels = new ArrayList<JCombinedConfigurer>();
		constantPanels.add(constantPanel);
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.add(ovar);
		this.add(constantPanels.get(ovar.getSelectedIndex()));
		
		this.ovar.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changeSelection(arg0);
			}
		});
	}
	
	private void changeSelection(ActionEvent evt) {
		this.remove(1);
		this.add(constantPanels.get(ovar.getSelectedIndex()));
		this.revalidate();
	}
	
	@Override
	public void add(List<WordConfigurer> words) {
		ovar.add(words.subList(0, 1));
		constantPanels.add(new JCombinedConfigurer(words.subList(1, words.size())));
	}

	@Override
	public void configureWord() {
		constantPanels.get(ovar.getSelectedIndex()).configureWord();
		ovar.configureWord();
	}

	@Override
	public void freeResources() {
		ovar.freeResources();
		for (WordConfigurer confy : constantPanels) {
			confy.freeResources();
		}
	}

	@Override
	public int getSelectedIndex() {
		return ovar.getSelectedIndex();
	}

	@Override
	public ConfigurableWord getWord() {
		return ovar.getWord();
	}

	@Override
	public void reAssign(Object o) {
		JAttributeConfigurer confy = (JAttributeConfigurer) o;
		this.constantPanels = confy.constantPanels;
		this.ovar = confy.ovar;
	}
}
