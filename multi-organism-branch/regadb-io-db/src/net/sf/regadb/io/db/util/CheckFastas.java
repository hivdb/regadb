package net.sf.regadb.io.db.util;

import java.io.File;
import java.util.ArrayList;

import net.sf.regadb.analysis.functions.FastaHelper;
import net.sf.regadb.analysis.functions.FastaRead;

public class CheckFastas 
{
    public static void main(String [] args)
    {
        File [] files = new File(args[0]).listFiles();
        ArrayList<String> invalidFiles = new ArrayList<String>();
        int invalidFilesCounter = 0;
        ArrayList<String> differentFiles = new ArrayList<String>();
        int differentFilesCounter = 0;
        for (int i = 0; i < files.length; ++i) {         
            FastaRead fr = FastaHelper.readFastaFile(files[i], true);

            switch (fr.status_) {
            case Valid:
            case ValidButFixed:

                break;
            case MultipleSequences:
            case FileNotFound:
            case Invalid:
                invalidFiles.add("INVALID FASTA: " + files[i] + " " + fr.status_);
                invalidFilesCounter++;
                continue;
            }
            
            String baseFileName = files[i].getName().substring(0, files[i].getName().lastIndexOf('.'));
            String seqName = fr.seq_.getName();
            if(!baseFileName.equals(seqName))
            {
                differentFiles.add("diff: " + files[i] + " " + baseFileName + " - " + seqName);
                differentFilesCounter++;
            }
        }
        System.err.println("Following files have differences in filename and sequencename (counted=" + differentFilesCounter +")");
        for(String diff : differentFiles)
        {
            System.err.println(diff);
        }
        System.err.println("Following files are invalid fasta files (counted=" + invalidFilesCounter +")");
        for(String invalid : invalidFiles)
        {
            System.err.println(invalid);
        }
    }
}
