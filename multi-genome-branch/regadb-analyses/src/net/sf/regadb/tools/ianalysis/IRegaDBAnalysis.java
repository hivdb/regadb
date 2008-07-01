package net.sf.regadb.tools.ianalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public interface IRegaDBAnalysis {
    public boolean execute(Map<String, File> inputFiles, Map<String, File> outputFiles, Map<String, String> inputs, Map<String, String> outputs, ArrayList<String> log);
    public String[] getInputFileNames();
    public String[] getOutputFileNames();
    public String[] getInputNames();
    public String getDescription();
}
