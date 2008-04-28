/*
 * Created on Jul 6, 2005
 */
package net.sf.regadb.tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;

import net.sf.regadb.csv.Table;

/**
 * @author kdforc0
 */
public class MutPos {
    public static void main(String[] args) throws FileNotFoundException {
        Table t = new Table(new BufferedInputStream(new FileInputStream(args[0])), false);
        PrintStream mutFile = new PrintStream(new FileOutputStream(args[1]));
        PrintStream posFile = new PrintStream(new FileOutputStream(args[2]));
        PrintStream wtFile = new PrintStream(new FileOutputStream(args[3]));
        String region = args[4];
        
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
    }
}
