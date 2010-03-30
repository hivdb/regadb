package net.sf.regadb.util.mapper.matcher;

import java.util.Map;

import org.jdom.Element;

public class NumberMatcher extends VariableMatcher{
    private char comp = '=';
    private double number;

    public boolean matchesCondition(Map<String,String> variables) throws MatcherException {
        String s = getValue(variables);
        try{
            double n = Double.parseDouble(s);
            
            switch(comp){
                case '<':
                    return n < number;
                case '>':
                    return n > number;
                default:
                    return n == number;
            }
        }
        catch(Exception ex){
            System.err.println("Not a number: "+ s);
        }
        
        return false;
    }

    public void parseCondition(Element e) {
        String snum = e.getTextTrim();
        if(snum.length() > 0){
        
            char c = snum.charAt(0);
            
            if(c == '<' || c == '>' || c == '='){
                snum = snum.substring(1);
                comp = c;
            }    
            try{
                number = Double.parseDouble(snum);
            }catch(Exception ex){
                System.err.println("Not a number: "+ snum);
            }
        }
    }

    public double getNumber(){
        return number;
    }
    public void setNumber(double number){
        this.number = number;
    }
}
