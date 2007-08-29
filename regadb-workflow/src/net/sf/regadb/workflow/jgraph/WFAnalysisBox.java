package net.sf.regadb.workflow.jgraph;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisInput;
import net.sf.regadb.workflow.analysis.AnalysisOutput;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

public class WFAnalysisBox extends DefaultGraphCell
{
    private Analysis analysis;
    
    public WFAnalysisBox(Analysis analysis, int posX, int posY)
    {
        setSettings(analysis, posX,posY,new Color(171, 171, 171), true);
    }
    
    public void setSettings(Analysis analysis, int x, int y, boolean setPorts) {
        setSettings(analysis, x, y, new Color(171, 171, 171), setPorts);
    }
    
    public void setSettings(Analysis analysis, int x, int y, Color c, boolean setPorts)
    {
        this.analysis = analysis;

        List<String> labelNames = new ArrayList<String>();
        labelNames.add(analysis.getType());
        if(analysis.getSpecType()!=null)
            labelNames.add(analysis.getSpecType());
        if(analysis.getName()!=null)
            labelNames.add(analysis.getName());
        
        AttributeMap map = new AttributeMap();
        
        int maxWidth = -1;
        String label = "<html><center>";
        for(int i = 0; i<labelNames.size(); i++)
        {
            maxWidth = Math.max(labelNames.get(i).length(), maxWidth);
            
            label += labelNames.get(i);
            if(i+1!=labelNames.size())
                label += "<br>";
        }
        label += "</center></html>";
        maxWidth=maxWidth*7 + 10; //supposed font width
        maxWidth = Math.max(maxWidth, 100);
        
        int maxHeigth = (Math.max(analysis.getInputs().size(), analysis.getOutputs().size())*30)+40;
        
        GraphConstants.setBorder(map, BorderFactory.createRaisedBevelBorder());
        GraphConstants.setBackground(map, c);
        GraphConstants.setForeground(map, Color.white);
        GraphConstants.setSizeable(map, false);
        GraphConstants.setBounds(map, map.createRect(x, y, maxWidth, maxHeigth));
        GraphConstants.setOpaque(map, true);
        GraphConstants.setVerticalAlignment(map, 1);
        
        if(setPorts)
            setPorts(analysis.getInputs(), analysis.getOutputs(), maxWidth, maxHeigth);
        
        this.setUserObject(label);
        
        attributes.applyMap(map);
    }
    
    public Point getLocation() {
        return new Point((int)GraphConstants.getBounds(attributes).getX(), (int)GraphConstants.getBounds(attributes).getY());
    }
    
    private void setPorts(List<AnalysisInput> inputs, List<AnalysisOutput> outputs, int maxWidth, int maxSize)
    {
        int startX = 15;
        int startY = maxSize-15;
        
        //InputPorts
        for(AnalysisInput i : inputs)
        {
            WFInputPortUserObject uo = new WFInputPortUserObject();
            uo.portName_ = i.getName();
            uo.input = i;
            addPort(new Point(startX, startY), uo);
            startY-=30;
        }
        
        startX = maxWidth-15;
        startY = maxSize-15;
 
        //OutputPorts
        for(AnalysisOutput o : outputs)
        {
            WFOutputPortUserObject uo = new WFOutputPortUserObject();
            uo.portName_ = o.getName();
            uo.output = o;
            addPort(new Point(startX, startY), uo);
            startY-=30;
        }
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }
}
