package ovid.preproc;

import java.util.ArrayList;

public class StringLiteralContinuingOnNewLine implements IStringLiteral {
    public int locateStringAction(StringBuffer content, int startIndex, int endIndex) {
        ArrayList<Character> recordCharsBeforeNewLine = new ArrayList<Character>(); 
        for(int i = startIndex-1; i>=0; i--) {
            if(content.charAt(i)=='\n')
                break;
            recordCharsBeforeNewLine.add(content.charAt(i));
        }
        for(char c : recordCharsBeforeNewLine) {
            if(!Character.isWhitespace(c)) {
                return endIndex;
            }
        }
        content.insert(startIndex, '+');
        return endIndex+1;
    }
}
