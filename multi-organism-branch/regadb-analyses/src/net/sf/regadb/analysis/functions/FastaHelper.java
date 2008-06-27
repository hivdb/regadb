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
    public static FastaRead readFastaFile(File file, boolean autoFix)
    {
        ArrayList<Sequence> sequences = new ArrayList<Sequence>();
   
        FastaRead read = handleXNA(sequences, file, true);

        if(read!=null)
        {
            if(autoFix)
            {
                autoFixSequence(read);
            }
            
            return read;
        }
        
        read = handleXNA(sequences, file, false);
        
        if(read!=null)
        {
            if(autoFix)
            {
                autoFixSequence(read);
            }

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
    
    private static FastaRead handleXNA(ArrayList<Sequence> sequences, File file, boolean desoxy)
    {
        
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
                    Sequence seq = xna.nextRichSequence();
                    sequences.add(seq);
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
    
    private static void autoFixSequence(FastaRead fastaRead)
    {
        StringBuffer validChars = new StringBuffer();
        StringBuffer invalidChars = new StringBuffer();
        
        for(int i = 0; i<fastaRead.xna_.length(); i++)
        {
            if(!NtSequenceHelper.isValidNtCharacter(fastaRead.xna_.charAt(i)))
            {
                invalidChars.append(fastaRead.xna_.charAt(i));
            }
            else    
            {
                validChars.append(fastaRead.xna_.charAt(i));
            }
        }
        
        if(invalidChars.length() > 0)
        {
            fastaRead.status_ = FastaReadStatus.ValidButFixed;
            fastaRead.invalidChars_ = invalidChars.toString();
            fastaRead.xna_ = validChars.toString();
        }
    }
    
    public static void main(String [] args)
    {
        FastaRead read = FastaHelper.readFastaFile(new File("/home/plibin0/Desktop/fasta/Seqs_Pieter/54751_rna.fasta"), true);
        System.err.println(read.status_);
        System.err.println(read.xna_.length());
    }
}
