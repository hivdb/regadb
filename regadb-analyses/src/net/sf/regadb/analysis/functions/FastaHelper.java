package net.sf.regadb.analysis.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojavax.bio.seq.RichSequenceIterator;

public class FastaHelper
{
    public static FastaRead readFastaFile(File file)
    {
        ArrayList<String> sequences = new ArrayList<String>();
   
        FastaRead read = handleXNA(sequences, file, true);
        
        if(read!=null)
        {
            return read;
        }
        
        read = handleXNA(sequences, file, false);
        
        if(read!=null)
        {
            return read;
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
    
    public static FastaRead handleXNA(ArrayList<String> sequences, File file, boolean desoxy)
    {
        Sequence seq;
        
        RichSequenceIterator xna = null;
        FileReader uploadedStream = null;
        BufferedReader br = null;
        try 
        {
            uploadedStream = new FileReader(file);
            br = new BufferedReader(uploadedStream);
            if(desoxy)
                xna = org.biojavax.bio.seq.RichSequence.IOTools.readFastaDNA(br, null);
            else
                xna = org.biojavax.bio.seq.RichSequence.IOTools.readFastaRNA(br, null);
        }
        catch (NoSuchElementException ex) 
        {

        }
        catch (FileNotFoundException ex) 
        {
            return new FastaRead(FastaReadStatus.FileNotFound); 
        }
        catch(IOException ioe)
        {
            
        }
        
        if(xna!=null)
        { 
            while(xna.hasNext())
            {
                try 
                {
                    seq = xna.nextRichSequence();
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
        }
        
        try 
        {
            uploadedStream.close();
            br.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static void main(String [] args)
    {
        FastaRead read = FastaHelper.readFastaFile(new File("/home/plibin0/Desktop/fasta/Seqs_Pieter/54751_rna.fasta"));
        System.err.println(read.status_);
        System.err.println(read.xna_.length());
    }
}
