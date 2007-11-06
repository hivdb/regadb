package ovid.preproc;

public class StringLiteralReplaceStdString implements IStringLiteral {
    String [] operators;
    public StringLiteralReplaceStdString(String [] operators) {
        this.operators = operators;
    }
    
    public int locateStringAction(StringBuffer content, int startIndex, int endIndex) {
        String stdstring = "std::string(";
        for(int j = startIndex-1; j<content.length(); j--) {
            if(j<0) 
                break;
            if(content.charAt(j) != ' ') {
                for(String operator : operators) {
                    if(j-operator.length()>=0) {
                        String sub = content.substring(j-operator.length()+1, j+1);
                        if(operator.equals(sub)) {
                            content.replace(startIndex, endIndex+1, stdstring + content.substring(startIndex, endIndex+1) + ")");
                            return endIndex + stdstring.length() + 1 /*( bracket*/;
                        }
                    }
                }
                break;
            }
        }
        return endIndex;
    }

}
