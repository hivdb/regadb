package net.sf.regadb.csv;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;


public class CsvCombine {
    public static void main(String [] args) {
        combine(args[0], args[1], System.out, ',');
    }
    
    public static void combine(String file1, String file2, OutputStream os, char delimiter) {
        /*
         * Open two csv files, and merge them columnwise.
         */

        Table table1;
        Table table2;
        try {
            table1 = new Table(new BufferedInputStream(new FileInputStream(file1)), false, delimiter);
            table2 = new Table(new BufferedInputStream(new FileInputStream(file2)), false, delimiter);

            table1.append(table2);
            
            Table finalTable = new Table(); 
            for(int i = 0; i<table1.numRows(); i++) {
                int foundIndex = -1;
                for(int j = 0; j<finalTable.numRows(); j++) {
                    if(finalTable.valueAt(0, j).equals(table1.valueAt(0, i))) {
                        foundIndex = j;
                        break;
                    }
                }
                if(foundIndex==-1) {
                    ArrayList<String> list = new ArrayList<String>();
                    
                    for(int j = 0; j<table1.numColumns(); j++) {
                        list.add(table1.valueAt(j, i));
                    }
                    
                    finalTable.addRow(list);
                } else {
                    for(int j = 0; j<table1.numColumns(); j++) {
                        if(!table1.valueAt(j, i).trim().equals("-"))
                            finalTable.setValue(j, foundIndex, table1.valueAt(j, i));
                    }
                }
            }

            finalTable.exportAsCsv(os, delimiter, false);
            System.out.flush();
        } catch (FileNotFoundException e) {
            System.err.println("Could not open file");
            System.exit(1);
        }
    }
}
