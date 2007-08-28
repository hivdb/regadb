package net.sf.regadb.workflow.analysis.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisInput;
import net.sf.regadb.workflow.analysis.ui.IAnalysisUI;

public class OutputFileAnalysis extends Analysis {

    public OutputFileAnalysis() {
        this.setSpecType("OutputFile");
        this.setDescription("Output file");
        this.getInputs().add(new AnalysisInput("External output file", this));
    }
    
    @Override
    public boolean execute(File workingDir, ArrayList<String> log) {
        File wp = getAnalysisPath(workingDir);
        File to = new File(this.getAttributeValue(OutputFileAnalysisForm.outputFile));
        File from = new File(wp.getAbsolutePath() + File.separatorChar + "External output file");
        try {
            FileUtils.copyFile(from, to);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public String getType() {
        return "File";
    }

    @Override
    public IAnalysisUI getUI() {
        return new OutputFileAnalysisForm(this);
    }
}