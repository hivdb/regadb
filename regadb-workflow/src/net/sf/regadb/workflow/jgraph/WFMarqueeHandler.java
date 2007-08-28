package net.sf.regadb.workflow.jgraph;

import static net.sf.regadb.workflow.i18n.I18n.tr;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.sf.regadb.workflow.analysis.AnalysisConnection;
import net.sf.regadb.workflow.analysis.AnalysisInput;
import net.sf.regadb.workflow.analysis.AnalysisOutput;
import net.sf.regadb.workflow.analysis.files.InputFileAnalysis;
import net.sf.regadb.workflow.analysis.files.OutputFileAnalysis;
import net.sf.regadb.workflow.analysis.text.ReplaceAnalysis;
import net.sf.regadb.workflow.analysis.ui.AnalysisDialog;
import net.sf.regadb.workflow.tooltip.ToolTip;

import org.jgraph.JGraph;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

public class WFMarqueeHandler extends BasicMarqueeHandler {

    // Holds the Start and the Current Point
    protected Point2D start, current;

    // Holds the First and the Current Port
    protected PortView port, firstPort;
    
    private JGraph graph;
    private JFrame mainFrame;
    
    public WFMarqueeHandler(JFrame mainFrame, JGraph graph)
    {
        this.mainFrame = mainFrame;
        this.graph = graph;
    }

    // Override to Gain Control (for PopupMenu and ConnectMode)
    public boolean isForceMarqueeEvent(MouseEvent e) {
        if (e.isShiftDown())
            return false;
        // If Right Mouse Button we want to Display the PopupMenu
        if (SwingUtilities.isRightMouseButton(e))
            // Return Immediately
            return true;
        // Find and Remember Port
        port = getSourcePortAt(e.getPoint());
        // If Port Found and in ConnectMode (=Ports Visible)
        if (port != null && graph.isPortsVisible())
            return true;
        // Else Call Superclass
        return super.isForceMarqueeEvent(e);
    }

    // Display PopupMenu or Remember Start Location and First Port
    public void mousePressed(final MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e)) {
            WFAnalysisBox box = null;
            CellView[] cellViews = graph.getGraphLayoutCache().getAllViews();
            for(CellView cv : cellViews) {
                if(cv.getBounds().contains(e.getX(), e.getY()))
                {
                    CellView[] cva = {cv};
                    Object [] cells = graph.getGraphLayoutCache().getCells(cva);
                    for(Object o : cells) {
                        if(o instanceof WFAnalysisBox) {
                            box = (WFAnalysisBox)o;
                            break;
                        }
                    }
                    if(box!=null)
                        break;
                }
            }
            if(box==null)
                createInsertPopupMenu(e).show(graph, e.getX(), e.getY());
            else
                createEditCellPopupMenu(e, box).show(graph, e.getX(), e.getY());
        }
        else if (port != null && graph.isPortsVisible()) {
            // Remember Start Location
            start = graph.toScreen(port.getLocation());
            // Remember First Port
            firstPort = port;
        } else {
            // Call Superclass
            super.mousePressed(e);
        }
    }
    
    public JPopupMenu createEditCellPopupMenu(final MouseEvent me, final WFAnalysisBox box) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem edit = new JMenuItem(tr("workflow.menu.editCell"));
        edit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                AnalysisDialog dialog = new AnalysisDialog(mainFrame, box.getAnalysis().getUI());
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        menu.add(edit);
        return menu;
    }
    
    public JPopupMenu createInsertPopupMenu(final MouseEvent me) {
        JPopupMenu menu = new JPopupMenu();
        JMenu insert = new JMenu(tr("workflow.menu.insert"));
        menu.add(insert);
        JMenu text = new JMenu("Text");
        insert.add(text);
        JMenuItem textReplace = new JMenuItem("Replace all");
        text.add(textReplace);
        textReplace.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                WFAnalysisBox box = new WFAnalysisBox(new ReplaceAnalysis(), me.getX(), me.getY());
                graph.getGraphLayoutCache().insert(box);
            }
        });
        JMenu file = new JMenu("File");
        insert.add(file);
        JMenuItem inputFile = new JMenuItem("InputFile");
        file.add(inputFile);
        inputFile.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                WFAnalysisBox box = new WFAnalysisBox(new InputFileAnalysis(), me.getX(), me.getY());
                graph.getGraphLayoutCache().insert(box);
            }
        });
        JMenuItem outputFile = new JMenuItem("OutputFile");
        file.add(outputFile);
        outputFile.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                WFAnalysisBox box = new WFAnalysisBox(new OutputFileAnalysis(), me.getX(), me.getY());
                graph.getGraphLayoutCache().insert(box);
            }
        });
        return menu;
    }

    // Find Port under Mouse and Repaint Connector
    public void mouseDragged(MouseEvent e) {
        // If remembered Start Point is Valid
        if (start != null) {
            // Fetch Graphics from Graph
            Graphics g = graph.getGraphics();
            // Reset Remembered Port
            PortView newPort = getTargetPortAt(e.getPoint());
            // Do not flicker (repaint only on real changes)
            if (newPort == null || newPort != port) {
                // Xor-Paint the old Connector (Hide old Connector)
                paintConnector(Color.black, graph.getBackground(), g, true);
                // If Port was found then Point to Port Location
                if(port!=null && port!=firstPort)
                {
                    paintPort(graph.getGraphics(), false);
                }
                port = newPort;
                if (port != null)
                    current = graph.toScreen(port.getLocation());
                // Else If no Port was found then Point to Mouse Location
                else
                    current = graph.snap(e.getPoint());
                // Xor-Paint the new Connector
                paintConnector(graph.getBackground(), Color.black, g, true);
                paintPort(graph.getGraphics(), true);
            }
        }
        // Call Superclass
        super.mouseDragged(e);
    }

    public PortView getSourcePortAt(Point2D point) {
        // Disable jumping
        graph.setJumpToDefaultPort(false);
        PortView result;
        try {
            // Find a Port View in Model Coordinates and Remember
            result = graph.getPortViewAt(point.getX(), point.getY());
        } finally {
            graph.setJumpToDefaultPort(true);
        }
        return result;
    }

    // Find a Cell at point and Return its first Port as a PortView
    protected PortView getTargetPortAt(Point2D point) {
        // Find a Port View in Model Coordinates and Remember
        return graph.getPortViewAt(point.getX(), point.getY());
    }

    // Connect the First Port and the Current Port in the Graph or Repaint
    public void mouseReleased(MouseEvent e) {
        // If Valid Event, Current and First Port
        if (e != null && port != null && firstPort != null
                && firstPort != port) {
            // Then Establish Connection
            if(!connect((Port) firstPort.getCell(), (Port) port.getCell())){
                graph.repaint();
            }
            e.consume();
            // Else Repaint the Graph
        } else
            graph.repaint();
        // Reset Global Vars
        firstPort = port = null;
        start = current = null;
        // Call Superclass
        super.mouseReleased(e);
    }

    // Show Special Cursor if Over Port
    public void mouseMoved(MouseEvent e) {
        // Check Mode and Find Port
        //!! make this somewhat abstract
        PortView pv = getSourcePortAt(e.getPoint());
        if (e != null && pv != null && graph.isPortsVisible()) {
            // Set Cusor on Graph (Automatically Reset)
            graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
            ToolTip.insideComponent = graph;
            CellView [] cellviews = {pv};
            String portName = ((WFPortUserObject)(((DefaultPort)graph.getGraphLayoutCache().getCells(cellviews)[0]).getUserObject())).getName();
            ToolTip.showTipWindow(portName, e.getX(), e.getY());
            // Consume Event
            // Note: This is to signal the BasicGraphUI's
            // MouseHandle to stop further event processing.
            e.consume();
        } else
            // Call Superclass
            ToolTip.hideTipWindow();
            super.mouseMoved(e);
    }

    // Use Xor-Mode on Graphics to Paint Connector
    protected void paintConnector(Color fg, Color bg, Graphics g, boolean preview) {
        // Set Foreground
        g.setColor(fg);
        // Set Xor-Mode Color
        g.setXORMode(bg);
        // Highlight the Current Port
        //paintPort(graph.getGraphics(), preview);
        // If Valid First Port, Start and Current Point
        if (firstPort != null && start != null && current != null)
            // Then Draw A Line From Start to Current Point
            g.drawLine((int) start.getX(), (int) start.getY(),
                    (int) current.getX(), (int) current.getY());
    }

    // Use the Preview Flag to Draw a Highlighted Port
    protected void paintPort(Graphics g, boolean preview) {
        // If Current Port is Valid
        if (port != null) 
        {
            graph.getUI().paintCell(g, port, port.getBounds(), preview);
        }
        if(firstPort!=null)
        {
            graph.getUI().paintCell(g, firstPort, firstPort.getBounds(), true);
        }
    }
    
//  Insert a new Edge between source and target
    public boolean connect(Port output, Port input)
    {
        // Construct Edge with no label
        Object sourceUO = ((DefaultPort)input).getUserObject();
        AnalysisInput ai;
        if(sourceUO instanceof WFInputPortUserObject) {
            ai = ((WFInputPortUserObject)sourceUO).input;
        } else {
            JOptionPane.showMessageDialog(null, tr("worklflow.graph.connection.connectIToO"), tr("workflow.general.warningMessage"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        Object targetUO = ((DefaultPort)output).getUserObject();
        AnalysisOutput ao;
        if(targetUO instanceof WFOutputPortUserObject) {
            ao = ((WFOutputPortUserObject)targetUO).output;
        } else {
            JOptionPane.showMessageDialog(null, tr("worklflow.graph.connection.connectIToO"), tr("workflow.general.warningMessage"), JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        AnalysisConnection edge = new AnalysisConnection();
        edge.setInput(ai);
        edge.setOutput(ao);
        
        if (graph.getModel().acceptsSource(edge, output)
                && graph.getModel().acceptsTarget(edge, input)) {
            // Create a Map thath holds the attributes for the edge
            edge.getAttributes().applyMap(createEdgeAttributes());
            // Insert the Edge and its Attributes
            graph.getGraphLayoutCache().insertEdge(edge, output, input);
        }
        return true;
    }
    
    public Map createEdgeAttributes()
    {
        Map map = new Hashtable();
        // Add a Line End Attribute
        GraphConstants.setLineEnd(map, GraphConstants.ARROW_CLASSIC);
        GraphConstants.setEndFill(map, true);
        return map;
    }

} // End of Editor.MyMarqueeHandler
