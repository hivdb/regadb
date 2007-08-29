package net.sf.regadb.workflow.analysis.io;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.jgraph.WFAnalysisBox;

import org.jdom.Element;

public class ImportWorkflow {
    public WFAnalysisBox importAnalysis(Element analysisEl) {
        int x = Integer.parseInt(analysisEl.getChild("location").getChild("x").getTextTrim());
        int y = Integer.parseInt(analysisEl.getChild("location").getChild("y").getTextTrim());
        
        String name = analysisEl.getChildTextTrim("name");
        String type = analysisEl.getChildTextTrim("type");
        
        return null;
    }
}
