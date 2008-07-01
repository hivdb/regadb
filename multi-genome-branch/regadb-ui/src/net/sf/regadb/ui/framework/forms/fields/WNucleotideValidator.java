package net.sf.regadb.ui.framework.forms.fields;

import net.sf.regadb.analysis.functions.NtSequenceHelper;
import net.sf.witty.wt.validation.WValidator;
import net.sf.witty.wt.validation.WValidatorPosition;
import net.sf.witty.wt.validation.WValidatorState;

public class WNucleotideValidator extends WValidator
{
    public WNucleotideValidator()
    {
        
    }
    
    @Override
    public WValidatorState validate(String input, WValidatorPosition pos) 
    {
        if (isMandatory())
        {
            if (input==null || input.length() == 0)
                return WValidatorState.InvalidEmpty;
        }
        else
        {
            if (input==null || input.length() == 0)
                return WValidatorState.Valid;
        }
        
        for(int i = 0; i<input.length(); i++)
        {
            if(!NtSequenceHelper.isValidNtCharacter(input.charAt(i)))
            {
                return WValidatorState.Invalid;
            }
        }
        
        return WValidatorState.Valid;
    }

}
