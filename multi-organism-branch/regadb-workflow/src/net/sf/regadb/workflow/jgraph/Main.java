package net.sf.regadb.workflow.jgraph;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.jgraph.graph.DefaultGraphModel;

public class Main
{    
    public static void main(String[] args)
    {
        JFrame fr = new JFrame("WorkFlow");
        
        WorkFlow wf = new WorkFlow(fr, new DefaultGraphModel());
        
        fr.addWindowListener(new WindowListener()
        {

            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void windowClosed(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        });
        fr.setSize(700, 700);
        fr.getContentPane().add(wf);
        fr.setVisible(true);
    }
}
