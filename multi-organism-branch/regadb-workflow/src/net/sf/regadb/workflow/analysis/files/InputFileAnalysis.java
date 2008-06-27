package net.sf.regadb.workflow.analysis.files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.regadb.workflow.analysis.Analysis;
import net.sf.regadb.workflow.analysis.AnalysisOutput;
import net.sf.regadb.workflow.analysis.ui.IAnalysisUI;

import org.apache.commons.io.FileUtils;

public class InputFileAnalysis extends Analysis {

    public InputFileAnalysis() {
        this.setSpecType("InputFile");
        this.setDescription("Input file");
        this.getOutputs().add(new AnalysisOutput("External input file", this));
    }
    
    @Override
    public boolean execute(File workingDir, ArrayList<String> log) {
        File wp = getAnalysisPath(workingDir);
        File from = new File(this.getAttributeValue(InputFileAnalysisForm.inputFile));
        File to = new File(wp.getAbsolutePath() + File.separatorChar + "External input file");
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
        return new InputFileAnalysisForm(this);
    }
}
