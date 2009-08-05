package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.analysis.functions.NtSequenceHelper;
import eu.webtoolkit.jwt.WValidator;

public class WNucleotideValidator extends WValidator
{
    public WNucleotideValidator()
    {
        
    }
    
    @Override
    public WValidator.State validate(String input) 
    {
        if (isMandatory())
        {
            if (input==null || input.length() == 0)
                return WValidator.State.InvalidEmpty;
        }
        else
        {
            if (input==null || input.length() == 0)
                return WValidator.State.Valid;
        }
        
        for(int i = 0; i<input.length(); i++)
        {
            if(!NtSequenceHelper.isValidNtCharacter(input.charAt(i)))
            {
                return WValidator.State.Invalid;
            }
        }
        
        return WValidator.State.Valid;
    }

}
