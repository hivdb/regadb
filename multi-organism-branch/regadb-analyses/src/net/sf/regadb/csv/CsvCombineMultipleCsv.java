package net.sf.regadb.csv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CsvCombineMultipleCsv {
    public static void main(String [] args) {
        if(args.length<2) {
            System.err.println("Usage: CsvCombineMultipleCsv directory outputfile");
            System.exit(0);
        }
        
        File dir = new File(args[0]);
        String outputFile = args[1];
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        
        File[] filesInDir = dir.listFiles();
        CsvCombine.combine(filesInDir[0].getAbsolutePath(), filesInDir[1].getAbsolutePath(), bos, ';');
        try {
            bos.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        
        try {
            FileUtils.writeStringToFile(new File(outputFile), bos.toString(), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for(int i = 2; i<filesInDir.length; i++) {
            if(filesInDir[i].isFile() && filesInDir[i].getAbsolutePath().endsWith(".csv")) {
                bos = new ByteArrayOutputStream();
                System.err.println(filesInDir[i].getAbsolutePath());
                CsvCombine.combine(filesInDir[i].getAbsolutePath(), outputFile, bos, ';');
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                new File(outputFile).delete();
                try {
                    FileUtils.writeStringToFile(new File(outputFile), bos.toString(), null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
