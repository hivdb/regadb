package net.sf.regadb.align.local;

public class Bounds {
    private int x1,y1,x2,y2;
    
    public Bounds(int x1, int y1, int x2, int y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    
    public int sizeX(){
        return x2 - x1;
    }
    public int sizeY(){
        return y2 - y1;
    }
    
    public int x1(){
        return x1;
    }
    public int x2(){
        return x2;
    }
    public int y1(){
        return y1;
    }
    public int y2(){
        return y2;
    }
}
