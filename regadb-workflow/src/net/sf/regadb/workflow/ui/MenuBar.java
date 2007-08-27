package net.sf.regadb.workflow.ui;

import static net.sf.regadb.workflow.i18n.I18n.tr;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.io.ExportWorkflow;
import net.sf.regadb.workflow.jgraph.WFAnalysisBox;
import net.sf.regadb.workflow.jgraph.WorkFlow;

import org.jgraph.graph.CellView;

public class MenuBar extends JMenuBar
{
    private MainFrame mainFrame_;
    
    public MenuBar(MainFrame mainFrame)
    {
        super();
        
        mainFrame_ = mainFrame;
        
        JMenu fileMenu = this.add(new JMenu(tr("menuBar.fileMenu.main")));
        
        JMenuItem newItem = fileMenu.add(new JMenuItem(tr("menuBar.fileMenu.newItem")));
        newItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                mainFrame_.createNewWorkflow();
            }
        });
        JMenuItem openItem = fileMenu.add(new JMenuItem(tr("menuBar.fileMenu.openItem")));
        JMenuItem saveItem = fileMenu.add(new JMenuItem(tr("menuBar.fileMenu.saveItem")));
        saveItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JInternalFrame internalFrame = mainFrame_.getDesktop().getSelectedFrame();
                if(internalFrame!=null) {
                    Component c = internalFrame.getContentPane().getComponent(0);
                    if(c instanceof WorkFlow) {
                        JFileChooser fc = new JFileChooser();
                        int returnVal = fc.showDialog(mainFrame_, tr("fileChooser.saveButton"));
                        if(returnVal==JFileChooser.APPROVE_OPTION) {
                            System.err.println(fc.getSelectedFile().getAbsolutePath());
                            CellView[] cv = ((WorkFlow)c).getGraphLayoutCache().getAllViews();
                            Object [] cells = ((WorkFlow)c).getGraphLayoutCache().getCells(cv);
                            ArrayList<Analysis> analyses = new ArrayList<Analysis>();
                            for(Object o : cells) {
                                if(o instanceof WFAnalysisBox) {
                                    analyses.add(((WFAnalysisBox)o).getAnalysis());
                                }
                            }
                            ExportWorkflow export = new ExportWorkflow();
                            export.writeXMLFile(analyses, fc.getSelectedFile());
                        }
                    }
                }
            }
        });
        
        fileMenu.addSeparator();
    }
}
