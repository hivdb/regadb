package net.sf.regadb.workflow.analysis.text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public boolean execute() {
        return false;
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
