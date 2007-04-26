package net.sf.regadb.workflow.jgraph;

import java.awt.Color;
import java.awt.event.MouseEvent;

import javax.swing.ToolTipManager;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

public class WorkFlow extends JGraph
{
    private AnalysesBox ab1_;
    private AnalysesBox ab2_;
    private String[] input1 = {"Joris", "Pieter", "Bart"};
    private String[] input2 = {"Joris", "Jose", "Ishmael", "Fatima"};
    private String[] output1 = {"Joris", "Joris"};
    private String[] output2 = {"XXX"};
    
    
    public WorkFlow(DefaultGraphModel model)
    {
        super(model);
        setBackground(Color.white);
        setPortsVisible(true);
        this.setJumpToDefaultPort(true);
        this.setMarqueeHandler(new MyMarqueeHandler(this));
        
        this.getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {
            /**
             * Constructs a new instance of a PortView view for the specified object
             */
            protected PortView createPortView(Port p) {
                return new MyPortView(p);
            }
        });
        

        ab1_ = new AnalysesBox(this,200,200,"Joris","Bart","jkjkjkjkjkjkjkjkjkjkjjkjjk",input1,output1);
        ab2_ = new AnalysesBox(this,400,200,"Pieter","Bart","Joris",input2,output2);
        Object[] cells = {ab1_, ab2_};
        getGraphLayoutCache().insert(cells);
        
        ToolTipManager.sharedInstance().registerComponent(this);
    }
    
    public String getToolTipText(MouseEvent e)
    {
        if(e != null) 
        {
            Object c = getFirstCellForLocation(e.getX(), e.getY());
            if(c instanceof MyPortView)
            {
                MyPortView mpv = (MyPortView)c;
                return "port tooltiptext";
            }
            
            return null;
        }
        return null;
    }
}
