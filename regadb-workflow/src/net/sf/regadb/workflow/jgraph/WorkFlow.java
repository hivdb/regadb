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
    private WFAnalysisBox ab1_;
    private WFAnalysisBox ab2_;
    private String[] input1 = {"Raw nucleotide sequences", "Reference sequence"};
    private String[] input2 = {"Aligned nucleotide sequences"};
    private String[] output1 = {"Aligned nucleotide sequences"};
    private String[] output2 = {"No duplicate sequences"};
    private String[] label = {"RegaDB", "AlignService"};
    private String[] label2 = {"RegaDB", "RemoveDups"};
    
    public WorkFlow(DefaultGraphModel model)
    {
        super(model);
        setBackground(Color.white);
        setPortsVisible(true);
        this.setJumpToDefaultPort(true);
        this.setMarqueeHandler(new WFMarqueeHandler(this));
        
        this.getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {
            /**
             * Constructs a new instance of a PortView view for the specified object
             */
            protected PortView createPortView(Port p) {
                return new WFPortView(p);
            }
        });
        
        

        //ab1_ = new WFAnalysisBox(200,200,label,input1,output1);
        //ab2_ = new WFAnalysisBox(400,200,label2,input2,output2);
        //Object[] cells = {ab1_, ab2_};
        //getGraphLayoutCache().insert(cells);
    }
}
