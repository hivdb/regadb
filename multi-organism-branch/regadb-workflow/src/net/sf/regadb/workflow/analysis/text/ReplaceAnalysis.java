package net.sf.regadb.workflow.analysis.text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.regadb.util.replace.ReplaceUtils;
import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisInput;
import net.sf.regadb.workflow.analysis.AnalysisOutput;
import net.sf.regadb.workflow.analysis.ui.IAnalysisUI;


public class ReplaceAnalysis extends Analysis {

    public ReplaceAnalysis() {
        this.setSpecType("Replace");
        this.setDescription("Replace all occurences of a match");
        this.getInputs().add(new AnalysisInput("Original file", this));
        this.getOutputs().add(new AnalysisOutput("Replacement file", this));
    }
    
    @Override
    public boolean execute(File workingDir, ArrayList<String> log) {
        File i = new File(getAnalysisPath(workingDir).getAbsolutePath() + File.separatorChar + "Original file");
        File o = new File(getAnalysisPath(workingDir).getAbsolutePath() + File.separatorChar + "Replacement file");
        try {
            ReplaceUtils.replaceAllInFile(i, o, this.getAttributeValue(ReplaceAnalysisForm.toReplace), this.getAttributeValue(ReplaceAnalysisForm.replaceValue));
        } catch (IOException e) {
            log.add(e.getMessage());
            return false;        
        }
        return true;
    }

    @Override
    public String getType() {
        return "Text";
    }

    @Override
    public IAnalysisUI getUI() {
        return new ReplaceAnalysisForm(this);
    }
}
