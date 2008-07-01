package net.sf.regadb.install.generateDrugs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.sf.regadb.csv.Table;

//This class creates commercial drugs without a dosage in the name
public class GenerateCommercialDrugsNoDosage {
    public static void main(String [] args) {
        try {
            List<String> done = new ArrayList<String>(); 
            
            List<String> commercial_generic_lines = new ArrayList<String>();
            
            Table commercial = new Table(new BufferedInputStream(new FileInputStream("drugs-csv"+File.separatorChar+"drug_commercial.csv")), false);
            Table commercial_generic = new Table(new BufferedInputStream(new FileInputStream("drugs-csv"+File.separatorChar+"commercial_generic.csv")), false);
            
            int counter = commercial.numRows();
            
            System.err.println("drug_commercial.csv");
            System.err.println("-------------------");
            for(int i = 1; i<commercial.numRows(); i++) {
                String drug = commercial.valueAt(1, i);
                if(drug.contains("mg") || drug.contains("ml")) {
                    int startRemove = -1;
                    for(int j = 0; j<drug.length(); j++) {
                        if(Character.isDigit(drug.charAt(j))) {
                            startRemove = j;
                            break;
                        }
                    }
                    
                    int endRemove = drug.indexOf(" mg ");
                    if(endRemove==-1)
                        endRemove = drug.indexOf(" ml ");
                    
                    if(startRemove==-1 || endRemove==-1) {
                        System.err.println("ERROR no dosage");
                    } else {
                        StringBuffer drugBuffer = new StringBuffer(drug);
                        drugBuffer.delete(startRemove, endRemove+4);
                        String processedDrug = drugBuffer.toString();
                        if(!processedDrug.equals("") && !done.contains(processedDrug)) {
                            int id = Integer.parseInt(commercial.valueAt(0, i));
                            System.err.println(++counter + "," + processedDrug + "," + commercial.valueAt(2, i));
                            done.add(processedDrug);
                            for(int k = 1; k<commercial_generic.numRows(); k++) {
                                if(commercial_generic.valueAt(0, k).equals(id+"")) {
                                    commercial_generic_lines.add(counter + "," +commercial_generic.valueAt(1, k));
                                }
                            }
                        }
                    }
                }
            }
            
            System.err.println();
            System.err.println("commercial_generic.csv");
            System.err.println("-------------------");
            for(String line : commercial_generic_lines) {
                System.err.println(line);
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }
}
