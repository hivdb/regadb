package net.sf.regadb.workflow.ui;

import static net.sf.regadb.swing.i18n.I18n.tr;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import net.sf.regadb.swing.i18n.I18n;
import net.sf.regadb.workflow.jgraph.WorkFlow;

import org.jgraph.graph.DefaultGraphModel;

public class MainFrame extends JFrame
{
    private MenuBar menuBar_;
    private JDesktopPane desktop_;
    
    private List<Integer> untitledWorkflowIndexes_ = new ArrayList<Integer>();
    
    static {
    	I18n.setBundleURI("net.sf.regadb.workflow.i18n.regadb-workflow");	
    }
    
    public MainFrame()
    {
    	super(tr("mainFrame.title"));
     	
        menuBar_ = new MenuBar(this);
        setJMenuBar(menuBar_);
        
        desktop_ = new JDesktopPane();
        this.getContentPane().add(desktop_);
        
        this.setSize(new Dimension(600, 800));
    }
    
    public void createNewWorkflow(JFrame frame) {
        int index = getMaxWorkflowIndex()+1;
        JInternalFrame internal = new JInternalFrame("Untitled - " + index);
        internal.setSize(400, 600);
        internal.setVisible(true);
        internal.setResizable(true);
        desktop_.add(internal);
        untitledWorkflowIndexes_.add(index);
        
        WorkFlow wf = new WorkFlow(frame, new DefaultGraphModel());
        internal.add(wf);
    }
    
    private int getMaxWorkflowIndex() {
        if(untitledWorkflowIndexes_.size()==0)
            return 0;
        
        Collections.sort(untitledWorkflowIndexes_);
        return untitledWorkflowIndexes_.get(untitledWorkflowIndexes_.size()-1);
    }

    public JDesktopPane getDesktop() {
        return desktop_;
    }
    
}
