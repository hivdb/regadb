package net.sf.regadb.ui.form.query.querytool.awceditor;

import java.util.List;

import com.pharmadm.custom.rega.queryeditor.wordconfiguration.AtomicWhereClauseEditor;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditor;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCEditorPanel;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ComposedAWCManager;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.ConfigurationController;
import com.pharmadm.custom.rega.queryeditor.wordconfiguration.WordConfigurer;

import eu.webtoolkit.jwt.WContainerWidget;
import eu.webtoolkit.jwt.WWidget;

public class WAWCEditorPanel extends WContainerWidget implements ComposedAWCEditorPanel{
	private ComposedAWCManager  manager;
    private List<WordConfigurer> configurers;
    
	public WAWCEditorPanel(AtomicWhereClauseEditor controller) {
		super();
		manager = new ComposedAWCManager(new ComposedAWCEditor(this, controller));
		configurers = getConfigurers(controller);
		setStyleClass("editorpanel");
		initConfigurers();
	}
	
	public void initConfigurers() {
		//TODO check
		// clear() does not work
		while (getChildren().size() > 0) {
			this.removeWidget(getChildren().get(0));
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
	
	public List<WordConfigurer> getConfigurers() {
		return configurers;
	}

	public ComposedAWCManager getManager() {
		return manager;
	}
}
