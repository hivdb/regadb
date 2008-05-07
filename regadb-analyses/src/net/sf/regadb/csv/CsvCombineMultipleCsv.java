package net.sf.regadb.csv;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class CsvCombineMultipleCsv {
    public static void main(String [] args) {
        File dir = new File(args[0]);
        String outputFile = args[1];
        
        ByteOutputStream bos = new ByteOutputStream();
        
        File[] filesInDir = dir.listFiles();
        CsvCombine.combine(filesInDir[0].getAbsolutePath(), filesInDir[1].getAbsolutePath(), bos, ';');
        bos.close();
        
        try {
            FileUtils.writeStringToFile(new File(outputFile), bos.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for(int i = 2; i<filesInDir.length; i++) {
            if(filesInDir[i].isFile() && filesInDir[i].getAbsolutePath().endsWith(".csv")) {
                bos = new ByteOutputStream();
                System.err.println(filesInDir[i].getAbsolutePath());
                CsvCombine.combine(filesInDir[i].getAbsolutePath(), outputFile, bos, ';');
                bos.close();
                new File(outputFile).delete();
                try {
                    FileUtils.writeStringToFile(new File(outputFile), bos.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
