package net.sf.regadb.analysis.functions;

public class NtSequenceHelper 
{
    public static boolean isValidNtCharacter(char nt)
    {
        switch (Character.toUpperCase(nt))
        {
        case 'A': 
        case 'C': 
        case 'G': 
        case 'T': case 'U':
        case 'M': 
        case 'R': 
        case 'W': 
        case 'S': 
        case 'Y': 
        case 'K': 
        case 'V': 
        case 'H': 
        case 'D': 
        case 'B': 
        case 'N': 
        case '-': return true;
        
        default: return false;
        }
    }
}
