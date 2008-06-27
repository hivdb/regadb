package net.sf.regadb.io.db.portugal;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sf.regadb.csv.Table;

public class ExtraAttributes {
    
    public static void main(String [] args) {
        try {
            Table sampleTable = readTable("/home/plibin0/import/pt/pt_regadb/v8/export/Sample.csv");
            for(int i = 0; i<sampleTable.numColumns(); i++) {
                ArrayList<String> col = sampleTable.getColumn(i);
                if(col.get(0).equals("Seroconvertion")) {
                    showDifferentValues(col);
                }
                if(col.get(0).equals("TherapyFailure")) {
                    showDifferentValues(col);
                }
                if(col.get(0).equals("Pregnancy")) {
                    showDifferentValues(col);
                } 
                if(col.get(0).equals("TherapyInterruption")) {
                    showDifferentValues(col);
                }
                if(col.get(0).equals("Protocol")) {
                    showDifferentValues(col);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static void showDifferentValues(ArrayList<String> col) {
        System.err.println(col.get(0) + ":");
        Set<String> uniqueValues = new HashSet<String>();
        int posCount = 0;
        int negCount = 0;
        for(int i = 1; i<col.size(); i++) {
            uniqueValues.add(col.get(i));
            if(col.get(i).trim().equals("1")) {
                posCount++;
            } else if(col.get(i).trim().equals("0")) {
                negCount++;
            }
        }
        for(String s : uniqueValues) {
            System.err.println("\t" + s + " + " + posCount + " - " + negCount);
        }
    }
    
    private static Table readTable(String filename) throws FileNotFoundException {
        System.err.println(filename);
        return new Table(new BufferedInputStream(new FileInputStream(filename)), false);
    }
}
