package net.sf.regadb.analysis.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojavax.bio.seq.RichSequenceIterator;

public class FastaHelper 
{
    public static FastaRead readFastaFile(File fileName)
    {
        RichSequenceIterator sequencesDNA = null;
        RichSequenceIterator sequencesRNA = null;
        try 
        {
            FileInputStream uploadedStream = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(uploadedStream));
            sequencesDNA = org.biojavax.bio.seq.RichSequence.IOTools.readFastaDNA(br, null);
            sequencesRNA = org.biojavax.bio.seq.RichSequence.IOTools.readFastaRNA(br, null);
        }
        catch (NoSuchElementException ex) 
        {
            System.err.println("no such element");
        }
        catch (FileNotFoundException ex) 
        {
            return new FastaRead(FastaReadStatus.FileNotFound); 
        }
        
        if(sequencesDNA==null && sequencesRNA==null)
        {
            return new FastaRead(FastaReadStatus.Invalid);
        }
        
        ArrayList<String> sequences = new ArrayList<String>();
        
        Sequence seq;
        while(sequencesRNA.hasNext())
        {
            try 
            {
                seq = sequencesRNA.nextRichSequence();
                sequences.add(seq.seqString());
            } 
            catch (NoSuchElementException e) 
            {
                break;
            } 
            catch (BioException e) 
            {
                break;
            }
        }
        
        while(sequencesDNA.hasNext())
        {
            try 
            {
                seq = sequencesDNA.nextRichSequence();
                sequences.add(seq.seqString());
            } 
            catch (NoSuchElementException e) 
            {
                break;
            } 
            catch (BioException e) 
            {
                break;
            }
        }
        
        if(sequences.size()==1)
        {
            return new FastaRead(sequences.get(0));
        }
        else if(sequences.size()==0)
        {
            return new FastaRead(FastaReadStatus.Invalid);
        }
        else //if size > 1
        {
            return new FastaRead(FastaReadStatus.MultipleSequences);
        }
    }
    
    public static void main(String [] args)
    {
        FastaRead read = FastaHelper.readFastaFile(new File("/home/plibin0/Desktop/fasta/Seqs_Pieter/54751.fasta"));
        System.err.println(read.status_);
    }
}
