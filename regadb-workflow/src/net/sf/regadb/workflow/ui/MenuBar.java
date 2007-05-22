package net.sf.regadb.workflow.ui;

import static net.sf.regadb.workflow.i18n.I18n.tr;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar extends JMenuBar
{
    public MenuBar()
    {
        super();
        
        JMenu fileMenu = this.add(new JMenu(tr("menuBar.fileMenu.main")));
        
        JMenuItem newItem = fileMenu.add(new JMenuItem(tr("menuBar.fileMenu.newItem")));
        JMenuItem openItem = fileMenu.add(new JMenuItem(tr("menuBar.fileMenu.openItem")));
        JMenuItem saveItem = fileMenu.add(new JMenuItem(tr("menuBar.fileMenu.saveItem")));
        
        fileMenu.addSeparator();
    }
}
