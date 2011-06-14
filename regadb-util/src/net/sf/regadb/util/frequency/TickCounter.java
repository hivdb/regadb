package net.sf.regadb.util.frequency;


public class TickCounter{
    long n=0;
    long t=0;
    long total=0;
    long start=0;
    
    public double getRate(){
        double e = (double)(System.currentTimeMillis() - t)/(double)1000;
        double r = (e == 0 ? -1 : (double)n/e);
        t = System.currentTimeMillis();
        n = 0;
        return r;
    }
    
    public double getAvgRate(){
        double e = (double)(System.currentTimeMillis() - start)/(double)1000;
        double r = (e == 0 ? -1 : (double)total/e);
        return r;
    }       
    
    public void start(){
        start = t = System.currentTimeMillis();
    }
    
    public void tick(){
        ++n;
        ++total;
    }
    
    public long getTotal(){
        return total;
    }
}