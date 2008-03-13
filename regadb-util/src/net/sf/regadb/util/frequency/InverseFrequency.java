package net.sf.regadb.util.frequency;

public class InverseFrequency extends Frequency {

    public InverseFrequency(){
        
    }
    
    public InverseFrequency(String unit, double seconds){
        super(unit, seconds);
    }

    public double timesToInterval(double times){
        return super.timesToInterval(1/times);
    }
    
    @Override
    public String toString(){
        return "per x "+ getUnit() +"s";
    }
}
