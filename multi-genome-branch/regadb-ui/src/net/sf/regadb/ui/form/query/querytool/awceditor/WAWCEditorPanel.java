package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.AtomicWhereClause;
import com.pharmadm.custom.rega.queryeditor.ConfigurableWord;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditor;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditorPanel;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedWordConfigurer;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConfigurationController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

import net.sf.witty.wt.WContainerWidget;
import net.sf.witty.wt.WWidget;

public class WAWCEditorPanel extends WContainerWidget implements ComposedAWCEditorPanel{
	
    private List<WordConfigurer> configurers;
    private ComposedAWCEditor editor;
    private AtomicWhereClauseEditor controller;
    
	public WAWCEditorPanel(AtomicWhereClauseEditor controller) {
		super();
		editor = new ComposedAWCEditor(this, controller);
		configurers = getConfigurers(controller);
		this.controller = controller;
		setStyleClass("editorpanel");
		initConfigurers();
	}
	
	public void initConfigurers() {
		// clear() does not work
		while (children().size() > 0) {
			this.removeWidget(children().get(0));
		}
		
		for (WordConfigurer confy : configurers) {
            try {
                addWidget((WWidget) confy);
            }
            catch (ClassCastException cce) {
                System.out.println("Warning : Can only add objects of class net.sf.witty.wt.WWidget to GUI");
            }			
		}
	}
	
	private List<WordConfigurer> getConfigurers(ConfigurationController controller) {
		return controller.getVisualizationComponentFactory().createComponents(controller.getVisualizationList());
	}
	
    /** Applies changes made to all visualisation components in the componentList to the corresponding AWCWords */
    public void applyEditings() {
    	editor.applyEditings();
    }
    
    public AtomicWhereClause getClause() {
        return editor.getSelectedEditor().getAtomicWhereClause();
    }      
    
    public void createComposedWord(List<ConfigurableWord> keys, List<ConfigurableWord> words, ComposedWordConfigurer configurer) {
    	editor.createComposedWord(keys, words, configurer);
    }
    
    public void composeWord(List<WordConfigurer> keys, List<WordConfigurer> additions, AtomicWhereClauseEditor  editor, boolean makeSelected) {
    	this.editor.composeWord(keys, additions, editor, makeSelected);
    }

	public List<WordConfigurer> getConfigurers() {
		return configurers;
	}    
	
    public ConfigurationController getEditor() {
        return controller;
    }

	public boolean isUseless() {
		return editor.isUseless();
	}}
