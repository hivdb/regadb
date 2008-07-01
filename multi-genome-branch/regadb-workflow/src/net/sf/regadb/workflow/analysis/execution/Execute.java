package net.sf.regadb.workflow.analysis.execution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisConnection;
import net.sf.regadb.workflow.jgraph.WFAnalysisBox;
import net.sf.regadb.workflow.jgraph.WFInputPortUserObject;
import net.sf.regadb.workflow.jgraph.WFOutputPortUserObject;
import net.sf.regadb.workflow.jgraph.WorkFlow;

import org.apache.commons.io.FileUtils;
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
    
    public void generateDirectoryStructure(File workingDir, ArrayList<WFAnalysisBox> boxes) {
        for(WFAnalysisBox box : boxes) {
            File wp = box.getAnalysis().getAnalysisPath(workingDir);
            wp.mkdirs();
        }
    }
    
    public void exec(WorkFlow workflow, File workingDir) {
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
        
        generateDirectoryStructure(workingDir, boxes);
        
        //remove analyses which are already ready
        for(Iterator<WFAnalysisBox> i = boxes.iterator(); i.hasNext();) {
            if(i.next().getAnalysis().isReady())
                i.remove();
        }
        
        if(boxes.size()==0) {
        writeLog("Nothing to be done...");
        return;
        }
        
        WFAnalysisBox box;
        boolean stop = false;
        ArrayList<String> log = new ArrayList<String>();
        while(boxes.size()!=0 && !stop) {
            for(Iterator<WFAnalysisBox> i = boxes.iterator(); i.hasNext();) {
                box = i.next();
                if(readyForExecution(box)) {
                    writeLog("Preparing analysis " + getAnalysisName(box.getAnalysis()));
                    
                    String copyLog = copyDependencyFiles(box, workingDir);
                    if(copyLog!=null){
                        writeLog("Stopping execution!!!!");
                        writeLog("Could not copy necessary input files, in analysis " + box.getAnalysis().getType() + " " + box.getAnalysis().getSpecType() + " " + box.getAnalysis().getName());
                        writeLog(copyLog);
                        stop = true;
                        break;
                    }
                    
                    writeLog("Executing analysis " + getAnalysisName(box.getAnalysis()));
                    log.clear();
                        if(box.getAnalysis().execute(workingDir, log)) {
                        box.getAnalysis().setReady(true);
                        writeLog("Successfully executed analysis " + getAnalysisName(box.getAnalysis()));
                        i.remove();
                        } else {
                            writeLog("Stopping execution!!!!");
                            writeLog("Stopped Analysis " +  getAnalysisName(box.getAnalysis()));
                            writeLog(log);
                            stop = true;
                            break;
                        }
                }
            }
        }
    }
    
    public String getAnalysisName(Analysis analysis) {
        return analysis.getType() + " " + analysis.getSpecType() + " " + analysis.getName();
    }
    
    public void writeLog(String log) {
        System.err.println(log);
    }
    
    public void writeLog(ArrayList<String> log) {
        for(String l : log) {
            writeLog(l);
        }
    }
    
    private String copyDependencyFiles(WFAnalysisBox box, File workingDir) {
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
        
        for(DefaultPort input : inputs) {
            Set edges = input.getEdges();
            for(Object edge : edges) {
                AnalysisConnection ac = (AnalysisConnection)edge;
                DefaultPort source = ((DefaultPort)ac.getSource());
                Object uo = source.getUserObject();
                WFOutputPortUserObject ouput = (WFOutputPortUserObject)uo;
                WFAnalysisBox parentBox = (WFAnalysisBox)source.getParent();
                File from = new File(parentBox.getAnalysis().getAnalysisPath(workingDir).getAbsolutePath() + File.separatorChar + ouput.portName_);
                File to = new File(box.getAnalysis().getAnalysisPath(workingDir).getAbsolutePath() + File.separatorChar + ((WFInputPortUserObject)input.getUserObject()).portName_);
                if(!from.exists())
                    return "Following file does not exist: " + from.getAbsolutePath();
                try {
                    FileUtils.copyFile(from, to);
                } catch (IOException e) {
                    return "Copying failed: " + from.getAbsolutePath() + " -> " + to.getAbsolutePath();
                }
            }
        }
        
        return null;
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
