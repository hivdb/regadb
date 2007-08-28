package net.sf.regadb.workflow.analysis.execution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.regadb.workflow.analysis.AnalysisConnection;
import net.sf.regadb.workflow.jgraph.WFAnalysisBox;
import net.sf.regadb.workflow.jgraph.WFInputPortUserObject;
import net.sf.regadb.workflow.jgraph.WorkFlow;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultPort;

public class Execute {
    public boolean edgeControl(WorkFlow workflow) {
        CellView[] cv = workflow.getGraphLayoutCache().getAllViews();
        Object[] os = workflow.getGraphLayoutCache().getCells(cv);
        for(Object o : os) {
            if(o instanceof DefaultPort) {
                if(((DefaultPort)o).getEdges().size()==0)
                    return false;
            }
        }
        return true;
    }
    
    public void exec(WorkFlow workflow) {
        CellView[] cv = workflow.getGraphLayoutCache().getAllViews();
        Object[] os = workflow.getGraphLayoutCache().getCells(cv);
        ArrayList<DefaultPort> ports = new ArrayList<DefaultPort>();
        ArrayList<WFAnalysisBox> boxes = new ArrayList<WFAnalysisBox>();
        for(Object o : os) {
            if(o instanceof WFAnalysisBox) {
                boxes.add((WFAnalysisBox)o);
            } else if(o instanceof DefaultPort) {
                ports.add((DefaultPort)o);
            }
        }
        
        WFAnalysisBox box;
        boolean stop = false;
        while(boxes.size()!=0 && !stop) {
            for(Iterator<WFAnalysisBox> i = boxes.iterator(); i.hasNext();) {
                box = i.next();
                if(readyForExecution(box)) {
                        box.getAnalysis().setReady(true);
                        i.remove();
                }
            }
        }
    }
    
    private boolean readyForExecution(WFAnalysisBox box) {
        ArrayList<DefaultPort> inputs = new ArrayList<DefaultPort>(); 
        List children = box.getChildren();
        for(Object c : children) {
            if(c instanceof DefaultPort) {
                Object uo  = ((DefaultPort)c).getUserObject();
                if(uo instanceof WFInputPortUserObject) {
                    inputs.add(((DefaultPort)c));
                }
            }
        }
        
        if(inputs.size()==0)
            return true;
        
        for(DefaultPort input : inputs) {
            Set edges = input.getEdges();
            for(Object edge : edges) {
                AnalysisConnection ac = (AnalysisConnection)edge;
                DefaultPort source = ((DefaultPort)ac.getSource());
                WFAnalysisBox parentBox = (WFAnalysisBox)source.getParent();
                if(!parentBox.getAnalysis().isReady())
                    return false;
            }
        }
        return true;
    }
}
