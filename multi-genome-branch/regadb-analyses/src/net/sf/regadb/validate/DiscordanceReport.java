package net.sf.regadb.validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.sf.regadb.csv.Table;

public class DiscordanceReport {
    static String [] order = {"IR", "IS", "RI", "RS", "SI", "SR"};
    
    public static void main(String [] args) {
        DiscordanceReport dr = new DiscordanceReport();
        dr.run(new File(args[0]), 800);
    }
    
    public void run(File input, int amountOfSequences) {
        try {
            ArrayList<DiscordanceLine> lines = new ArrayList<DiscordanceLine>(); 
            
            Table rawReport = new Table(new FileInputStream(input), false);
            for(int i = 0; i < rawReport.numRows(); i++) {
                DiscordanceLine dl = new DiscordanceLine();
                dl.drug_ = rawReport.valueAt(0, i);
                dl.sir1_ = rawReport.valueAt(1, i).charAt(0);
                dl.sir2_ = rawReport.valueAt(2, i).charAt(0);
                dl.mutations1_ = rawReport.valueAt(3, i);
                dl.mutations2_ = rawReport.valueAt(4, i);
                lines.add(dl);
            }
            System.err.println(lines.size());
            Collections.sort(lines);
            
            List<Integer> startPositions = new ArrayList<Integer>();
            
            String oldComb = "";
            String newComb;
            for(int i=0; i<lines.size(); i++) {
                newComb = lines.get(i).drug_ + " " + lines.get(i).sir1_ + "->" + lines.get(i).sir2_;
                if(!newComb.equals(oldComb)) {
                    startPositions.add(i);
                }
                oldComb = newComb;
            }
            //add end position
            startPositions.add(lines.size());
            
            HashMap<String, Double> freqParticularChangeDrug = new HashMap<String, Double>();
            
            for(int i = 0; i<startPositions.size()-1; i++) {
                int size = startPositions.get(i+1);
                for(int j = startPositions.get(i); j<size; j++) {
                    if(j==startPositions.get(i)) {
                        lines.get(j).freqParticularChangeDrug_ = (startPositions.get(i+1)-startPositions.get(i)) / (double)amountOfSequences;
                        freqParticularChangeDrug.put(lines.get(j).drug_+lines.get(j).sir1_+""+lines.get(j).sir2_, lines.get(j).freqParticularChangeDrug_);
                    }
                    if(lines.get(j).occurence_!=-1) {
                        String mut_1 = lines.get(j).mutations1_.trim();
                        String mut_2 = lines.get(j).mutations2_.trim();
                        lines.get(j).numberSeqs_ = startPositions.get(i+1)-startPositions.get(i);
                        for(int k = j+1; k<size; k++) {
                            if(mut_1.equals(lines.get(k).mutations1_.trim()) && mut_2.equals(lines.get(k).mutations2_.trim())) {
                                lines.get(j).occurence_++;
                                lines.get(k).occurence_ = -1;
                            }
                        }
                    }
                }
            }
            
            //remove duplicates
            for(Iterator<DiscordanceLine> i = lines.iterator(); i.hasNext();) {
                DiscordanceLine dl = i.next();
                if(dl.occurence_==-1)
                    i.remove();
            }
            
            String newDrug = "";
            String oldDrug = "";
            ArrayList<Double> lower = new ArrayList<Double>();
            ArrayList<Double> higher = new ArrayList<Double>();
            for(DiscordanceLine dl : lines) {
                newDrug = dl.drug_;
                if(!oldDrug.equals(newDrug)) {
                    lower.clear();
                    higher.clear();
                    lower.add(freqParticularChangeDrug.get(newDrug+"IS"));
                    lower.add(freqParticularChangeDrug.get(newDrug+"RI"));
                    lower.add(freqParticularChangeDrug.get(newDrug+"RS"));
                    higher.add(freqParticularChangeDrug.get(newDrug+"IR"));
                    higher.add(freqParticularChangeDrug.get(newDrug+"SI"));
                    higher.add(freqParticularChangeDrug.get(newDrug+"SR"));
                    dl.lowerScore = getSum(lower);
                    dl.higherScore = getSum(higher);
                }
                oldDrug = newDrug;
            }
            
            System.out.println("drug, level 1, level 2, mutations 1, mutations 2, occurence, numberSeqs, freq particular change/drug, freq total change/drug, higher score, lower score");
            for(DiscordanceLine dl : lines) {
                System.out.print(dl.drug_ + ",");
                System.out.print(dl.sir1_ + ",");
                System.out.print(dl.sir2_ + ",");
                System.out.print(dl.mutations1_ + ",");
                System.out.print(dl.mutations2_ + ",");
                System.out.print(dl.occurence_ + ",");
                System.out.print(dl.numberSeqs_ + ",");
                if(dl.freqParticularChangeDrug_!=null) {
                    System.out.print(dl.freqParticularChangeDrug_ + ",");
                } else {
                    System.out.print(",");
                }
                if(dl.higherScore!=null) {
                    System.out.print(dl.higherScore+dl.lowerScore + "," + dl.higherScore + "," + dl.lowerScore);
                } else {
                    System.out.print(",,");
                }
                System.out.println("");
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private double getSum(ArrayList<Double> dblList) {
        double sum = 0.0;
        for(Double i : dblList) {
            if(i!=null) {
                sum += i;
            }
        }
        return sum;
    }
    
    class DiscordanceLine implements Comparable {
        public String drug_;
        public char sir1_;
        public char sir2_;
        public String mutations1_;
        public String mutations2_;
        
        public int occurence_ = 1;
        public int numberSeqs_;
        
        public Double freqParticularChangeDrug_;
        
        public Double higherScore;
        public Double lowerScore;
        
        public int compareTo(Object o) {
            DiscordanceLine dl = (DiscordanceLine)o;
            if(drug_.equals(dl.drug_)) {
                return getOrder(sir1_+""+sir2_) - getOrder(dl.sir1_+""+dl.sir2_);
            } else {
                return drug_.compareTo(dl.drug_);
            }
        }
        
        public int getOrder(String jump) {
            
            for(int i = 0; i<order.length; i++) {
                if(order[i].equals(jump))
                    return i;
            }
            return -1;
        }
    }
}
