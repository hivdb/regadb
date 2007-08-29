/*
 * Created on Jul 6, 2005
 */
package net.sf.regadb.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

import net.sf.regadb.csv.Table;
import net.sf.regadb.tools.ianalysis.IRegaDBAnalysis;

/**
 * @author kdforc0
 */
public class MutPos implements IRegaDBAnalysis {

    private final static String[] inputFiles =  {"Boolean mutation table csv file (column name eg. PR25F)"};
    public String[] getInputFileNames() {
        return inputFiles;
    }

    private final static String[] inputs =  {"Protein region (PR)"};
    public String[] getInputNames() {
        return inputFiles;
    }

    private final static String[] outputFiles =  {"Mutation file", "Position file", "Wildtype file"};
    public String[] getOutputFileNames() {
        return outputFiles;
    }
    
    public boolean execute(Map<String, File> inputFiles, Map<String, File> outputFiles, Map<String, String> inputs, Map<String, String> outputs, ArrayList<String> log) {
        try {
            Table t = new Table(new BufferedInputStream(new FileInputStream(inputFiles.get(this.inputFiles[0]))), false);
            PrintStream mutFile = new PrintStream(new FileOutputStream(outputFiles.get(this.outputFiles[0])));
            PrintStream posFile = new PrintStream(new FileOutputStream(outputFiles.get(this.outputFiles[1])));
            PrintStream wtFile = new PrintStream(new FileOutputStream(outputFiles.get(this.outputFiles[2])));
            String region = inputs.get(this.inputs[0]);
            
            ArrayList histogram = t.histogram();
            
            int currentpos = 0;
            ArrayList mutations = null;
            ArrayList prevalences = null;
            boolean first_val_wt = true;
            boolean firstval = true;
            for (int i = 0; i <= histogram.size(); ++i) {
                int pos = -1;
                String mutation = null;
                Map m = null;

                if (i < histogram.size()) {
                    String colName = t.valueAt(i, 0);
                    pos = Integer.parseInt(colName.substring(region.length(), colName.length() - 1));
                    mutation = colName.substring(colName.length()-1, colName.length());
                    m = (Map) histogram.get(i);
                }

                if ((i == histogram.size()) || (currentpos != pos)) {
                    if (currentpos != 0) {
                        if (prevalences.size() != 1) {
                            int maxPrevalence = -1;
                            int maxPrevalence_i = -1;
                            
                            for (int j = 0; j < prevalences.size(); ++j) {
                                int prev = ((Integer) prevalences.get(j)).intValue();
                                if (prev > maxPrevalence) {
                                    maxPrevalence = prev;
                                    maxPrevalence_i = j;
                                }
                            }
                            
                            //if (currentpos != -1)
                            if(!first_val_wt)
                            {
                                wtFile.print(",");
                            }
                            else
                            {
                                first_val_wt = false;
                            }
                            
                            wtFile.print(region + currentpos + mutations.get(maxPrevalence_i));

                            for (int j = 0; j < mutations.size(); ++j) {
                                if (j != maxPrevalence_i)
                                {
                                    if(firstval)
                                    {
                                        firstval = false;
                                        mutFile.print(region + currentpos + mutations.get(j));
                                    }
                                    else
                                    {
                                        mutFile.print("," + region + currentpos + mutations.get(j));
                                    }
                                }
                            }
                            if (currentpos != -1)
                                posFile.print("|");
                            posFile.print("(" + region + currentpos + "_.)");
                        }
                    }
                    mutations = new ArrayList();
                    prevalences = new ArrayList();
                }
                
                if (i < histogram.size()) {
                    mutations.add(mutation);
                    prevalences.add(m.get("y"));
                }

                currentpos = pos;
            }
            mutFile.close();
            posFile.close();
            wtFile.close();
            return true;
        } catch (FileNotFoundException e) {
            log.add("Could not find one of the input/output files.");
            return false;
        }
    }

    public String getDescription() {
        return "MutPos";
    }
}
