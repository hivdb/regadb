package net.sf.regadb.workflow.ui;

import static net.sf.regadb.workflow.i18n.I18n.tr;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

public class MainFrame extends JFrame
{
    private MenuBar menuBar_;
    private JDesktopPane desktop_;
    
    public MainFrame()
    {
        super(tr("mainFrame.title"));
        
        menuBar_ = new MenuBar();
        setJMenuBar(menuBar_);
        
        desktop_ = new JDesktopPane();
        this.getContentPane().add(desktop_);
         
        JInternalFrame internal = new JInternalFrame("lala");
        internal.setSize(200, 200);
        internal.setVisible(true);
        desktop_.add(internal, 0);
    }
    
}
