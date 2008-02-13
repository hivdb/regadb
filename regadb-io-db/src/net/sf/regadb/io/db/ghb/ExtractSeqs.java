package net.sf.regadb.io.db.ghb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;
import net.sf.regadb.analysis.functions.FastaReadStatus;

import org.apache.commons.io.FileUtils;

public class ExtractSeqs {
    int succes = 0;
    int failed = 0;
    private HashMap<String, String> seqs = new HashMap<String, String>();
    
    public ExtractSeqs() {
        
    }
    
    private void run(ArrayList<File> sourcePaths, File targetFasta) {
        for(File sourcePath : sourcePaths) {
            parseSourceDir(sourcePath);
        }
        
        System.err.println("succes " + succes + " failed " + failed);
    }
    
    private void parseSourceDir(File sourcePath) {
        for(File yearDir : sourcePath.listFiles()) {
            if(yearDir.isDirectory()) {
                for(File sampleDir : yearDir.listFiles()) {
                    if(sampleDir.isDirectory()) {
                        parseSample(sampleDir);
                    }
                }
            }
        }
    }
    
    private void parseSample(File sampleDir) {
        File fasta = getFileWithSuffix(sampleDir, ".fasta");
        File fsta = getFileWithSuffix(sampleDir, ".fsta");
        File consensus = getFileWithSuffix(sampleDir, "con");
        
        boolean multipleFiles = multipleFilesOfType(sampleDir, ".fasta") || multipleFilesOfType(sampleDir, ".fsta") || multipleFilesOfType(sampleDir, "con");
        if(multipleFiles) {
            System.err.println("Multiple files of the same type for sample -> " + sampleDir);
        }
        
        ArrayList<File> seqFile = new ArrayList<File>();
        if(fasta!=null)
            seqFile.add(fasta);
        if(fsta!=null) 
            seqFile.add(fsta);
        if(consensus!=null)
            seqFile.add(consensus);
        if(seqFile.size()>1) {
            System.err.println("Multiple seq files " + sampleDir.getAbsolutePath());
            failed++;
        }
        
        if(fasta==null && fsta==null && consensus==null) {
            System.err.println("Could not find any sequence file in sampledir ->" + sampleDir);
            failed++;
        } else {
            if(fasta!=null) {
                parseFasta(fasta, sampleDir);
            } else if(fsta!=null) {
                parseFasta(fsta, sampleDir);
            } else if(consensus!=null) {
                parseConsensus(consensus, sampleDir);
            }
            succes++;
        }
    }
    
    private boolean multipleFilesOfType(File dir, String suffix) {
        int counter = 0;
        for(File f : dir.listFiles()) {
            if(f.getAbsolutePath().endsWith(suffix)) {
                counter++;
            }
        }
        return counter>1;
    }
    
    private void parseConsensus(File consensusFile, File sampleDir) {
        try {
            String seq = new String(FileUtils.readFileToByteArray(consensusFile));
            storeSeq(sampleDir, consensusFile, seq);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void parseFasta(File fastaFile, File sampleDir) {
        FastaRead read = FastaHelper.readFastaFile(fastaFile, false);
        if(read.status_ == FastaReadStatus.Valid) {
            storeSeq(sampleDir, fastaFile, read.xna_);
        } else {
            System.err.println("Invalid fasta file: " + fastaFile.getAbsolutePath());
        }
    }
    
    private void storeSeq(File sampleDir, File fastaFile, String seq) {
        String sampleId = sampleDir.getAbsolutePath().substring(sampleDir.getAbsolutePath().lastIndexOf(File.separatorChar)+1);
        
        seq = seq.replace('\n', ' ');
        seq = seq.replace((char)13, ' ');
        seq = seq.replaceAll(" ", "");
        seq = seq.replaceAll("-", "");
        
        for(int i = 0; i<seq.length(); i++) {
            if(!Character.isLetter(seq.charAt(i))) {
                System.err.println("Invalid sequence in file " + fastaFile.getAbsolutePath() + " -> \"" + seq.charAt(i) + "\"");
                return;
            }
        }
        seqs.put(sampleId, seq);
    }
    
    private File getFileWithSuffix(File dir, String suffix) {
        for(File f : dir.listFiles()) {
            if(f.getAbsolutePath().endsWith(suffix) && !f.isDirectory()) {
                return f;
            }
        }
        return null;
    }
    
    public static void main(String [] args) {
        ArrayList<File> sourcePaths = new ArrayList<File>();
        for(int i = 0; i < args.length; i++) {
            if(i!=args.length-1) {
                File f = new File(args[i]);
                if(!f.exists() || !f.isDirectory()) {
                    System.err.println(f.getAbsolutePath() + " is not a directory or does not exist");
                    System.exit(0);
                }
                sourcePaths.add(f);
            }
        }
        ExtractSeqs es = new ExtractSeqs();
        es.run(sourcePaths, new File(args[args.length-1]));
    }
}
