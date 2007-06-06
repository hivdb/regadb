package net.sf.regadb.align.view;

import net.sf.regadb.analysis.functions.AaSequenceHelper;
import net.sf.regadb.db.AaInsertion;
import net.sf.regadb.db.AaMutation;
import net.sf.regadb.db.AaSequence;
import net.sf.regadb.db.Protein;
import net.sf.regadb.genome.HivGenome;

public class VisualizeAaSequence 
{
    public static final int LINE_SIZE = 81;
    
    private StringBuffer page = new StringBuffer();
    
    private StringBuffer refAa = new StringBuffer();
    private StringBuffer refNt = new StringBuffer();
    private StringBuffer diff = new StringBuffer();
    private StringBuffer tarNt = new StringBuffer();
    private StringBuffer tarAa = new StringBuffer();

    private StringBuffer refCodon = new StringBuffer();
    private StringBuffer tarCodon = new StringBuffer();
    
    private int aaCounter = 0;
    private int lineCounter = 0;
    
    private static String newLine = "<br>";
    
    public String getAlignmentView (AaSequence aaseq)
    {
        clear();
        
        HivGenome genome = HivGenome.getHxb2();
        String proteinNt = genome.getNtSequenceForProtein(aaseq.getProtein().getAbbreviation());
                
        AaMutation[] mutations = AaSequenceHelper.getSortedAaMutationArray(aaseq);
        int mutationIndex = 0;
        AaInsertion[] insertions = AaSequenceHelper.getSortedAaInsertionArray(aaseq);
        int insertionIndex = 0;
        
        String mutationCodon;
        
        for(int i = 0 ; i<proteinNt.length(); i++)
        {
            if(i<(aaseq.getFirstAaPos()-1*3) || i>=aaseq.getLastAaPos()*3)
            {
                addNt(proteinNt.charAt(i), '-');
            }
            else if(mutations.length != mutationIndex && (mutations[mutationIndex].getId().getPosition()-1)*3==i)
            {
                mutationCodon = mutations[mutationIndex].getNtMutationCodon();
                addNt(proteinNt.charAt(i), mutationCodon.charAt(0));
                addNt(proteinNt.charAt(i+1), mutationCodon.charAt(1));
                addNt(proteinNt.charAt(i+2), mutationCodon.charAt(2));
                i+=2;
                mutationIndex++;
            }
            else
            {
                addNt(proteinNt.charAt(i), proteinNt.charAt(i));
            }
            
            if(insertions.length != insertionIndex && (insertions[insertionIndex].getId().getPosition()-1)*3==i)
            {
                short pos = insertions[insertionIndex].getId().getPosition();
                while(insertionIndex!=insertions.length && insertions[insertionIndex].getId().getPosition()==pos)
                {
                    mutationCodon = insertions[insertionIndex].getNtInsertionCodon();
                    addNt('-', mutationCodon.charAt(0));
                    addNt('-', mutationCodon.charAt(1));
                    addNt('-', mutationCodon.charAt(2));
                    
                    insertionIndex++;
                }
            }
            
            if(refCodon.length()>=3)
            {
                addAa();
            }
        }
        
        endOfAlignment();

        //return refAa.toString() + '\n' +refNt.toString() + '\n'+  tarNt.toString() + '\n' + tarAa.toString() + '\n';
        //return '\n' +refNt.toString() + '\n' + tarNt.toString() + '\n';
        return page.toString();
    }
    
    private void addNt(char reference, char target)
    {
        if(reference==target || target == '-')
        {
            refNt.append(reference);
            tarNt.append(target);
        }
        else
        {
            refNt.append("<font color=red>"+reference+"</font>");
            tarNt.append("<font color=red>"+target+"</font>");
        }
        
        refCodon.append(reference);
        tarCodon.append(target);
        diff.append(reference==target?'|':' ');
    }
    
    private void addAa()
    {
        String ref = AaSequenceHelper.getAminoAcid(refCodon.toString());
        String tar = AaSequenceHelper.getAminoAcid(tarCodon.toString());
        if(ref.equals(tar) || tar.toString().equals(" - "))
        {
            refAa.append(ref);
            tarAa.append(tar);
        }
        else
        {
            refAa.append("<font color=red>"+ref+"</font>");
            tarAa.append("<font color=red>"+tar+"</font>");
        }

        refCodon.delete(0, 3);
        tarCodon.delete(0, 3);
        aaCounter++;
        
        nextLine();
    }
    
    private void nextLine()
    {
        if(aaCounter==(LINE_SIZE/3.0))
        {
            endOfAlignment();
        }
    }
    
    private void endOfAlignment()
    {
        int fromNt = ((LINE_SIZE * lineCounter) + 1);
        int toNt = fromNt + (aaCounter*3) - 1;
        int fromAa = (((LINE_SIZE/3) * lineCounter) + 1);
        int toAa = fromAa + aaCounter - 1;

        page.append("Going from " + fromNt + " to " + toNt + " (" + fromAa + " to " + toAa + ")"+ newLine);
        
        appendLineToPage(refAa);
        appendLineToPage(refNt);
        appendLineToPage(diff);
        appendLineToPage(tarNt);
        appendLineToPage(tarAa);

        aaCounter = 0;
        lineCounter++;
    }
    
    private void appendLineToPage(StringBuffer b)
    {
        page.append(b+newLine);
        b.delete(0, b.length());
    }
    
    private void clear()
    {
        if(page.length()!=0)
        {
            page.delete(0, page.length());
        }
        
        aaCounter = 0;
        lineCounter = 0;
    }
    
    public static void main(String [] args)
    {
       String c = AaSequenceHelper.getAminoAcid("%cg");
       System.err.println(c);
       
       AaSequence aaseq = new AaSequence();
       aaseq.setFirstAaPos((short)1);
       aaseq.setLastAaPos((short)99);
       //aaseq.getAaInsertions().add(new AaInsertion());
       Protein p = new Protein();
       p.setAbbreviation("PRO");
       aaseq.setProtein(p);
       
       VisualizeAaSequence v = new VisualizeAaSequence();
       System.err.println(v.getAlignmentView (aaseq));
    }
}