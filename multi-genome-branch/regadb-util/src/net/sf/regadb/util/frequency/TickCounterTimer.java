package net.sf.regadb.util.frequency;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

public class TickCounterTimer extends TimerTask{
    private PrintStream out;
    private Timer timer = null;
    private int delay;
    
    private NumberFormat format;
    private TickCounter counter;
    
    public TickCounterTimer(PrintStream out, int delay){
        this.out = out;
        this.delay = delay;
        counter = new TickCounter();
        
        init();
    }
    
    public TickCounterTimer(PrintStream out, int delay, TickCounter counter){
        this.out = out;
        this.delay = delay;
        this.counter = counter;
        
        init();
    }
    
    private void init(){
        format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(1);
        format.setMinimumFractionDigits(1);
    }
    
    public void start(){
        counter.start();        
        timer = new Timer();
        timer.scheduleAtFixedRate(this, delay, delay);
    }
    
    public void tick(){
        counter.tick();
    }
    
    public void stop(){
        timer.cancel();
    }

    @Override
    public void run() {
        print();
    }
    
    public void print(){
        out.println(counter.getTotal() +"\t"+ format.format(counter.getRate()) +"\t"+ format.format(counter.getAvgRate()));
    }
}
